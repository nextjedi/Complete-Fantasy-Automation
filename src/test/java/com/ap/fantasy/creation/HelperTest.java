package com.ap.fantasy.creation;

import io.appium.java_client.AppiumDriver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;

class HelperTest {
    AppiumDriver driver;

    void setUp() throws MalformedURLException {
        driver = CreateDriverSession.getDriver("",0);
    }

    @Test
    void selectTeamToEdit() {
        try {
            setUp();
            boolean isTeamSelected = true;
            for(int i = 5; i < 21; i++){
                Helper.notFound("test wait");
                if(!Helper.selectTeamToEdit(i,driver)){
                    isTeamSelected = false;
                }

            }
            Assertions.assertTrue(isTeamSelected);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}