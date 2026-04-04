package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.HolidayAPI;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;


import java.time.LocalDate;


@NoArgsConstructor
@Data
public class HolidayResponseDTO {

    // Deserialise the incoming String into a Date
    @JsonDeserialize(using = LocalDateDeserializer.class)
    // We Just Need the Date of the Holiday
    private LocalDate date;



}
