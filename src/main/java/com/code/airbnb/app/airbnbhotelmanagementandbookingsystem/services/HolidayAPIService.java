package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.services;


import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.HolidayAPI.CalenderDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.HolidayAPI.CalenderItemsDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.exceptions.HolidayAPIException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HolidayAPIService {

    private final RestClient restClient;

    @Value("${google.calender.holiday.api.key}")
    private String apiKey;

    @Value("${google.calender.holiday.base.region}")
    private String region;

    @Value("${google.calender.holiday.base.calender.id}")
    private String calendarId;

    public List<CalenderItemsDTO> getHolidays(String timeMin, String timeMax) {
        String publicHolidayCalendarId = region + "#" + calendarId;

        // Response catches the object which we get upon hitting the URL+uri and maps subsequent fields.
        CalenderDTO response = restClient.get() // HTTP Method
        .uri(uriBuilder -> uriBuilder
                .pathSegment(publicHolidayCalendarId, "events")
                .queryParam("key", apiKey)
                // Filter results from a specific start date to specific end date, in our case a whole year
                .queryParam("timeMin",timeMin)
                .queryParam("timeMax",timeMax)
                // Filter results with q to find only public holidays
                .queryParam("q","Public Holiday")
                .build()) //Variables and URI, append the URI to Base url
                .retrieve() // Retrieve the Results.
                .body(CalenderDTO.class);

        if (response == null) {
            throw new HolidayAPIException("Holiday response is null");
        }
        if (response.getItems() == null) {
            return List.of();
        }

        return response.getItems();
    }



}



