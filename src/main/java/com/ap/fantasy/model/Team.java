package com.ap.fantasy.model;

public enum Team {

    GT,CHE,PBKS,KOL,MI,SRH,RR,DC,LKN,
    SCO,ZIM,AUS,NZ,ENG,AFG,SL,IRE,IND,PAK,BAN,NED,SA,LIO,WEP,
    MIW,UPW,WI,GOR,GAM,ACT,ADD,
    SWH,BEL,STA,AMR,QUT,CHU,THR,CTB,ND,AZA,BOB,THU,CCH,FBA,DAT,GGI,PRL,JOH,KHT,RAN,KHW,SSCS,ME,STX,KRM;

    public static Team findByName(String name) {
        for (Team value : values()) {
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }
}
