package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/roomly/api/v1")
public class HealthController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "service", "roomly-backend",
                "checkedAt", Instant.now().toString()
        );
    }
}
