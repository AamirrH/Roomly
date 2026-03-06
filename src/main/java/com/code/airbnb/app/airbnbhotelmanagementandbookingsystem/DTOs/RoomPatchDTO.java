package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class RoomPatchDTO {
    private String type;
    private BigDecimal basePrice;
    private Integer totalCount;
    private Integer capacity;
    private List<String> amenities;
    private List<String> photos;
}