package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.HolidayAPI;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CalenderItemsDTO {

    private String summary;
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DateWrapper{
        @JsonDeserialize(using = LocalDateDeserializer.class)
        private LocalDate date;
    }
    private DateWrapper start;
    private DateWrapper end;
    private String description;
    private String transparency;



}
