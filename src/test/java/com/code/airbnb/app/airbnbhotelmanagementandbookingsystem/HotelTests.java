package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.repositories.HotelRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;


public class HotelTests {



    @Test
    void Adder()
    {
        int a = 30;
        int b = 40;
        int c = addTwoNumber(a,b);
        System.out.println(c);

    }

    // Helper Method
    int addTwoNumber(int a, int b){
        return a+b;
    }





}
