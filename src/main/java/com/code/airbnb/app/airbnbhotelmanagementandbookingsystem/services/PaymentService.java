package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.services;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.CreatePaymentOrderRequestDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.CreatePaymentOrderResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.PaymentResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.VerifyPaymentRequestDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Booking;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Payment;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.enums.BookingStatus;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.enums.PaymentStatus;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.exceptions.BookingExpiredException;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.exceptions.BookingNotFoundException;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.exceptions.PaymentException;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.repositories.BookingRepository;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.repositories.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final BookingService bookingService;

    @Value("${razorpay.key-id}")
    private String razorpayKeyId;

    @Value("${razorpay.key-secret}")
    private String razorpayKeySecret;

    @Value("${razorpay.currency:INR}")
    private String currency;

    @Transactional
    public CreatePaymentOrderResponseDTO createPaymentOrder(CreatePaymentOrderRequestDTO requestDTO) {
        Booking booking = bookingRepository.findById(requestDTO.getBookingId())
                .orElseThrow(() -> new BookingNotFoundException("Booking with id " + requestDTO.getBookingId() + " does not exist"));

        bookingService.checkBookingOwnership(booking);
        validateBookingCanBePaid(booking);

        Payment payment = paymentRepository.findByBooking_Id(booking.getId())
                .orElseGet(() -> Payment.builder()
                        .booking(booking)
                        .price(booking.getFinalCalculatedPrice())
                        .currency(currency)
                        .status(PaymentStatus.PENDING)
                        .build());

        if (PaymentStatus.SUCCESS.equals(payment.getStatus())) {
            throw new PaymentException("Payment is already completed for this booking");
        }

        try {
            Order order = createRazorpayOrder(booking);
            payment.setRazorpayOrderId(order.get("id"));
            payment.setTransactionId(order.get("id"));
            payment.setPrice(booking.getFinalCalculatedPrice());
            payment.setCurrency(currency);
            payment.setStatus(PaymentStatus.PENDING);

            Payment savedPayment = paymentRepository.save(payment);
            return CreatePaymentOrderResponseDTO.builder()
                    .bookingId(booking.getId())
                    .paymentId(savedPayment.getId())
                    .razorpayOrderId(savedPayment.getRazorpayOrderId())
                    .razorpayKeyId(razorpayKeyId)
                    .amount(savedPayment.getPrice())
                    .amountInPaise(toPaise(savedPayment.getPrice()))
                    .currency(savedPayment.getCurrency())
                    .status(savedPayment.getStatus().name())
                    .build();
        } catch (RazorpayException ex) {
            log.warn("Razorpay order creation failed for booking {}: {}", booking.getId(), ex.getMessage());
            throw new PaymentException("Razorpay order creation failed: " + ex.getMessage());
        }
    }

    @Transactional
    public PaymentResponseDTO verifyPayment(VerifyPaymentRequestDTO requestDTO) {
        Booking booking = bookingRepository.findById(requestDTO.getBookingId())
                .orElseThrow(() -> new BookingNotFoundException("Booking with id " + requestDTO.getBookingId() + " does not exist"));

        bookingService.checkBookingOwnership(booking);

        Payment payment = paymentRepository.findByRazorpayOrderId(requestDTO.getRazorpayOrderId())
                .orElseThrow(() -> new PaymentException("Payment order not found"));

        if (!payment.getBooking().getId().equals(booking.getId())) {
            throw new PaymentException("Payment order does not belong to this booking");
        }

        verifyRazorpaySignature(requestDTO);

        payment.setRazorpayPaymentId(requestDTO.getRazorpayPaymentId());
        payment.setRazorpaySignature(requestDTO.getRazorpaySignature());
        payment.setTransactionId(requestDTO.getRazorpayPaymentId());
        payment.setStatus(PaymentStatus.SUCCESS);
        booking.setStatus(BookingStatus.CONFIRMED);

        bookingRepository.save(booking);
        Payment savedPayment = paymentRepository.save(payment);
        return mapToResponse(savedPayment);
    }

    private Order createRazorpayOrder(Booking booking) throws RazorpayException {
        validateRazorpayConfiguration();
        RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", toPaise(booking.getFinalCalculatedPrice()));
        orderRequest.put("currency", currency);
        orderRequest.put("receipt", "booking_" + booking.getId());
        return razorpayClient.orders.create(orderRequest);
    }

    private void verifyRazorpaySignature(VerifyPaymentRequestDTO requestDTO) {
        try {
            validateRazorpayConfiguration();
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", requestDTO.getRazorpayOrderId());
            options.put("razorpay_payment_id", requestDTO.getRazorpayPaymentId());
            options.put("razorpay_signature", requestDTO.getRazorpaySignature());
            boolean verified = Utils.verifyPaymentSignature(options, razorpayKeySecret);
            if (!verified) {
                throw new PaymentException("Payment signature verification failed");
            }
        } catch (RazorpayException ex) {
            log.warn("Razorpay signature verification failed for order {}: {}", requestDTO.getRazorpayOrderId(), ex.getMessage());
            throw new PaymentException("Payment signature verification failed");
        }
    }

    private void validateRazorpayConfiguration() {
        if (razorpayKeyId == null || razorpayKeyId.isBlank() || razorpayKeySecret == null || razorpayKeySecret.isBlank()) {
            throw new PaymentException("Razorpay is not configured");
        }
    }

    private void validateBookingCanBePaid(Booking booking) {
        if (bookingService.isBookingExpired(booking)) {
            throw new BookingExpiredException("Booking is expired");
        }
        if (BookingStatus.CANCELLED.equals(booking.getStatus())) {
            throw new BookingExpiredException("Cancelled bookings cannot be paid");
        }
        if (BookingStatus.CONFIRMED.equals(booking.getStatus())) {
            throw new PaymentException("Booking is already confirmed");
        }
    }

    private Long toPaise(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
    }

    private PaymentResponseDTO mapToResponse(Payment payment) {
        return PaymentResponseDTO.builder()
                .paymentId(payment.getId())
                .bookingId(payment.getBooking().getId())
                .amount(payment.getPrice())
                .currency(payment.getCurrency())
                .paymentStatus(payment.getStatus())
                .bookingStatus(payment.getBooking().getStatus())
                .razorpayOrderId(payment.getRazorpayOrderId())
                .razorpayPaymentId(payment.getRazorpayPaymentId())
                .build();
    }
}
