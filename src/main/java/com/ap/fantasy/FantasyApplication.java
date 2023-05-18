package com.ap.fantasy;

import com.ap.fantasy.creation.CreateDriverSession;
import com.ap.fantasy.creation.FetchDetails;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.MalformedURLException;

@SpringBootApplication
public class FantasyApplication {

	public static void main(String[] args) throws MalformedURLException, InterruptedException {
		SpringApplication.run(FantasyApplication.class, args);
		AndroidDriver<AndroidElement> driver = CreateDriverSession.getDriver("", 0);
		DriverCode driverCode = new DriverCode();
		FetchDetails fetchDetails = new FetchDetails();
		driverCode.normalFlow();
	}

}
