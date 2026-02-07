package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.controllers;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String test(){
        return "API OK<3";
    }

}
