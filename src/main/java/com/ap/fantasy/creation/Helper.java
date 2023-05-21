package com.ap.fantasy.creation;

import com.ap.fantasy.creation.dream11.FirstPage;
import com.ap.fantasy.creation.dream11.MatchDetailsPage;
import com.ap.fantasy.model.*;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;

import java.io.*;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.ap.fantasy.model.Constant.*;

public class Helper {

    private Helper(){}
    public static final Logger log = Logger.getLogger("Helper.class");


    public static Player parsePlayer(List<WebElement> playerDetails, PlayerType type, int playerRow){
//        todo: check at later stage
        Map<String, String> pls = playerDetails.stream().collect(Collectors.toMap(WebElement::getTagName, WebElement::getText));
        Player player = new Player(pls,type,playerRow);
        if(player.getName().isBlank() && player.getTeam() ==null){
            return null;
        }
        return player;
    }

    public static void scroll(PointOption<?> source, PointOption<?> destination, AppiumDriver<?> driver,boolean isTap ) throws InterruptedException {
        if((source ==null || destination== null)){{
            notFound("Source or destination is null");
        }}
        TouchAction<?> actions = new TouchAction<>(driver);
        actions.longPress(source).moveTo(destination).release().perform();
        wait(2);
        if(isTap){
            actions.tap(source).perform();
        }
    }

    public static float amountInWordToNumber(String amount){
        int crore = 10000000;
        int lakh = 100000;
        float n = 0;
        if(amount.contains("Crore")){
            amount =amount.replaceAll(DIGIT_ONLY_REGEX, "");
            n =Float.parseFloat(amount);
            n*=crore;
        }else if(amount.contains("Lakh")){
            amount =amount.replaceAll(DIGIT_ONLY_REGEX, "");
            n =Float.parseFloat(amount);
            n*=lakh;
        }
        return n;
    }

    public static Date parseTime(String time){
        var timeSplit = Arrays.stream(time.split(" ")).toList();
        int second = 0;
        for (String s : timeSplit) {
            if (s.contains("h")) {
                var hour = Integer.parseInt(s.replaceAll("h", ""));
                second += hour * 60 * 60;
            }
            if (s.contains("m")) {
                var min = Integer.parseInt(s.replaceAll("m", ""));
                second += min * 60;
            }
            if (s.contains("s")) {
                var sec = Integer.parseInt(s.replaceAll("s", ""));
                second += sec;
            }
        }
        return Date.from(new Date().toInstant().plusSeconds(second));
    }
    public static void notFound(String param){
        log.info("Not found "+ param);
    }

    public static void wait(int t) throws InterruptedException {
        TimeUnit.SECONDS.sleep(t);
    }
    public static boolean isPointInRectangle(Rectangle rectangle, Point point){
        return rectangle.getY()<point.y && rectangle.getY()+rectangle.getHeight() >point.y;
    }
}
