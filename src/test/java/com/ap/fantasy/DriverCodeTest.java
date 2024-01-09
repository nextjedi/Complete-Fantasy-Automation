package com.ap.fantasy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.MalformedURLException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DriverCodeTest {
    @Autowired
    private DriverCode driverCode;
    @Test
    void pastMatch() throws MalformedURLException, InterruptedException {
        driverCode.pastMatch();
    }
}