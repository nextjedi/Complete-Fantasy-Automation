package com.ap.fantasy;

import com.ap.fantasy.creation.CreateDriverSession;
import com.ap.fantasy.creation.FetchDetails;
import com.ap.fantasy.creation.Helper;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.MalformedURLException;

@SpringBootApplication
@RestController
public class FantasyApplication {
	@Autowired
	private DriverCode driverCode;

	public static void main(String[] args) {
		SpringApplication.run(FantasyApplication.class, args);
	}

	@GetMapping("/hello")
	public void insertInstruments() throws MalformedURLException, InterruptedException {
		try{
			driverCode.normalFlow();
			Helper.wait(60*30);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
