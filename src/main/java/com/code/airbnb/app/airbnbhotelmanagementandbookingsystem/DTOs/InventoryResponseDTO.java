package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class InventoryResponseDTO {
    private Long id;
    private Long hotelId;
    private Long roomId;
    private LocalDate date;
    private Integer bookedCount;
    private Integer totalCount;
    private BigDecimal surgeFactor;
    private Boolean closed;
}
