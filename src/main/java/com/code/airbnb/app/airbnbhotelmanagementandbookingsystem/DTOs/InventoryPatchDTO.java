package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class InventoryPatchDTO {
    private Integer totalCount;
    private Integer bookedCount;
    private BigDecimal surgeFactor;
    private Boolean closed;
}
