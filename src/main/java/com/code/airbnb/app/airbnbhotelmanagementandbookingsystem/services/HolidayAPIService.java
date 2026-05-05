package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.services;


import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.HolidayAPI.CalenderDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.HolidayAPI.CalenderItemsDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.HolidayAPI.HolidayResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.exceptions.HolidayAPIException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HolidayAPIService {

    private final RestClient restClient;

    public List<CalenderItemsDTO> getHolidays(String timeMin, String timeMax) {
        // Response catches the object which we get uppon hitting the URL+uri and maps subsequent fields.
        CalenderDTO response = restClient.get() // HTTP Method
        .uri(uriBuilder -> uriBuilder
                .queryParam("timeMin",timeMin)
                .queryParam("timeMax",timeMax)
                .queryParam("q","Public Holiday")
                .build()) //Variables and URI, append the URI to Base url
                .retrieve() // Retrieve the Results.
                .body(CalenderDTO.class);

        if (response == null) {
            throw new HolidayAPIException("Holiday response is null");
        }
        else {
            return response.getItems();
        }
    }



}



