package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.configurations;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

// Creates Bean for RestClient to call third party holiday API
@Configuration
public class RestClientConfig {


    @Value("${holiday.api.base.url}")
    private String BASE_URL;


    @Bean
    RestClient getRestClient() {
        return RestClient
                .builder()
                .baseUrl(BASE_URL)
                .build();
    }

}

