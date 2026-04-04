package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.services;


import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.HolidayAPI.HolidayResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HolidayAPIService {

    private final RestClient restClient;

    public List<HolidayResponseDTO> getHolidays(Integer year,String countryCode) {
        return restClient.get() // HTTP Method
        .uri("/Publicholidays/{year}/{countryCode}", year,countryCode) //Variables and URI, append the URI to Base url
                .retrieve() // Execute
                .body(new ParameterizedTypeReference<List<HolidayResponseDTO>>() {
                });


    }


}



