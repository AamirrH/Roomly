package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem;

import org.aspectj.weaver.tools.ISupportsMessageContext;
import org.hibernate.dialect.function.AvgFunction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class Tests {


    @Test
    void testTokenSplit(){
        String token = "Bearer dfmsdgfdgjks.463tegjnmewogjfndjfio.sj30reghrdfthyhdxrhgfjxr5r67s4tgy9tavl";
        String splitToken = token.split("Bearer ")[1];
        Assertions.assertEquals("dfmsdgfdgjks.463tegjnmewogjfndjfio.sj30reghrdfthyhdxrhgfjxr5r67s4tgy9tavl",splitToken);




    }




}
