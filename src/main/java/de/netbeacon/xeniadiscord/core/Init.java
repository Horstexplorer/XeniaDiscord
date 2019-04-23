package de.netbeacon.xeniadiscord.core;

import de.netbeacon.xeniadiscord.util.Config;

public class Init {

    public static void main(String[] args){

        System.out.println("-------------------- Xenia Discord --------------------");

        //prepare all files
        Config config = new Config();

        if(Boolean.parseBoolean(config.load("activated"))){
            new Thread(new XCore()).start();
        }else {
            System.out.println("Bot has been deactivated. Please check configuration file.");
        }
    }
}
