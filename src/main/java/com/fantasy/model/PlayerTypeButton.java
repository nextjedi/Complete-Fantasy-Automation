package com.fantasy.model;

import org.openqa.selenium.WebElement;

public class PlayerTypeButton {
    private WebElement wkWeb;
    private WebElement batWeb;
    private WebElement arWeb;
    private WebElement bowlWeb;

    public PlayerTypeButton(WebElement wkWeb, WebElement batWeb, WebElement arWeb, WebElement bowlWeb) {
        this.wkWeb = wkWeb;
        this.batWeb = batWeb;
        this.arWeb = arWeb;
        this.bowlWeb = bowlWeb;
    }

    public WebElement getWkWeb() {
        return wkWeb;
    }

    public void setWkWeb(WebElement wkWeb) {
        this.wkWeb = wkWeb;
    }

    public WebElement getBatWeb() {
        return batWeb;
    }

    public void setBatWeb(WebElement batWeb) {
        this.batWeb = batWeb;
    }

    public WebElement getArWeb() {
        return arWeb;
    }

    public void setArWeb(WebElement arWeb) {
        this.arWeb = arWeb;
    }

    public WebElement getBowlWeb() {
        return bowlWeb;
    }

    public void setBowlWeb(WebElement bowlWeb) {
        this.bowlWeb = bowlWeb;
    }
}
