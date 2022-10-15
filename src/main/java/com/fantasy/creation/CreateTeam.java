package com.fantasy.creation;

import com.fantasy.model.*;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CreateTeam {
    public void init(List<FantasyTeamTO> teams, MatchDetails matchDetails) throws MalformedURLException, InterruptedException {
//        todo: create multiple driver session
        AppiumDriver driver = CreateDriverSession.getDriver();
//        todo: distribute teams and call create team for each set
//        make the create team call multi threaded
        create(teams, driver,matchDetails);

    }
    private void create(List<FantasyTeamTO> teams, AppiumDriver driver, MatchDetails matchDetails) throws InterruptedException {
//        call helper function to reach the match
        if(Helper.selectMatch(driver,matchDetails)){

            for(FantasyTeamTO team:teams){
//            todo: click create team robust
                List<WebElement> d = driver.findElementsByClassName("android.widget.Button");
                for(var text:d){
                    System.out.println(text.getText());
                    if(text.getText().equals("CREATE A TEAM")){
                        System.out.println(text.getText());
                        text.click();
                        break;
                    }
                }
                TimeUnit.SECONDS.sleep(3);

                //        fetch buttons

                WebElement typeH = driver.findElementByClassName("android.widget.HorizontalScrollView");
                List<WebElement> types =typeH.findElements(By.className("android.widget.TextView"));
                WebElement wkWeb = null;
                WebElement batWeb = null;
                WebElement arWeb = null;
                WebElement bowlWeb = null;
                for(WebElement ty : types){
                    switch (ty.getText()){
                        case "WK":
                            wkWeb = ty;
                            break;
                        case "BAT":
                            batWeb = ty;
                            break;
                        case "AR":
                            arWeb = ty;
                            break;
                        case "BOWL":
                            bowlWeb = ty;
                            break;
                    }
                }

                batWeb.click();
                TimeUnit.SECONDS.sleep(3);
                selectPlayers(driver, PlayerType.BAT,team.getBat());

                arWeb.click();
                TimeUnit.SECONDS.sleep(3);
                selectPlayers(driver, PlayerType.AR,team.getAl());

                bowlWeb.click();
                TimeUnit.SECONDS.sleep(3);
                selectPlayers(driver, PlayerType.BOWL,team.getBowl());

                wkWeb.click();
                TimeUnit.SECONDS.sleep(3);
                selectPlayers(driver, PlayerType.WK,team.getWk());

//                todo: select cvc
//                done

            }





        }




//        travel players
//        keep tapping
    }

    public static void selectPlayers(AppiumDriver driver, PlayerType type, List<String> players) throws InterruptedException {
        int count =0;
        boolean flag = false;
        PointOption source = null;
        PointOption destination = null;
        do {
            List<WebElement> pls = driver.findElementsByClassName("androidx.recyclerview.widget.RecyclerView");
            flag = !(pls.size()==3);

            for(WebElement p:pls){

                List<WebElement> ps = p.findElements(By.className("android.view.ViewGroup"));
                for (WebElement temp : ps) {

                    if (temp.getAttribute("content-desc") == null) {
                        break;
                    }
                    if (temp.getAttribute("content-desc").equals("create_team_player_row")) {
                        //fetch player details
                        List<WebElement> playerDetails = temp.findElements(By.className("android.widget.TextView"));
                        Player player = Helper.parsePlayer(playerDetails, type);
                        if(players.contains(player.getName())){
                            temp.click();
                            count++;
//                            todo: verify
                            players.remove(player.getName());
                        }
                        if(players.size()==0){
                            source = PointOption.point(temp.getLocation());
                        }else if (players.size()==2){
                            destination = PointOption.point(temp.getLocation());
                        }
                    }

                }

            }
            if(!(source ==null || destination== null)){
                TouchAction actions = new TouchAction(driver);
                actions.longPress(destination).moveTo(source).release().perform();
                TimeUnit.SECONDS.sleep(2);
                actions.tap(destination).perform();
                TimeUnit.SECONDS.sleep(1);
//                actions.longPress(PointOption.point(598,1584)).moveTo(PointOption.point(520,977)).release().perform();
            }
        }while (flag || players.isEmpty());
    }

}
