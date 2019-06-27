package de.netbeacon.xeniadiscord.core;

import de.netbeacon.xeniadiscord.util.webhooks.twitch.TwitchAPIWrap;


public class Test {

    public static void main(String[]args){
        try{
            System.out.println(new TwitchAPIWrap().getChannelid(""));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
