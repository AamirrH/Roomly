package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.HolidayAPI;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CalenderDTO {

    private List<CalenderItemsDTO> items;


}
