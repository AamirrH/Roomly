package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class BookingReportDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private Long totalBookings;
    private BigDecimal totalRevenue;
}
