package com.ap.fantasy.model;

import io.appium.java_client.android.AndroidElement;
import lombok.Data;
@Data
public class PlayerTypeButton {
    private AndroidElement wkWeb;
    private AndroidElement batWeb;
    private AndroidElement arWeb;
    private AndroidElement bowlWeb;

    public PlayerTypeButton(AndroidElement wkWeb, AndroidElement batWeb, AndroidElement arWeb, AndroidElement bowlWeb) {
        this.wkWeb = wkWeb;
        this.batWeb = batWeb;
        this.arWeb = arWeb;
        this.bowlWeb = bowlWeb;
    }
}
