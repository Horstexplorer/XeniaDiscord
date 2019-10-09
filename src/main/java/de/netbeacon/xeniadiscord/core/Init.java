package de.netbeacon.xeniadiscord.core;

import de.netbeacon.xeniadiscord.util.Config;
import de.netbeacon.xeniadiscord.util.ShutdownHook;
import de.netbeacon.xeniadiscord.util.gui.Terminal;
import de.netbeacon.xeniadiscord.util.log.Log;

public class Init {

    public static void main(String[] args){

        System.out.println("-------------------- Xenia Discord --------------------");

        //add shutdown hook
        new ShutdownHook();

        //prepare config file
        Config config = new Config();
        new Log().addEntry("Xenia", "Running Xenia v"+config.version(), 0);

        if(Boolean.parseBoolean(config.load("bot_gui_activate"))){
            // start gui
            new Thread(new Terminal()).start();
            System.out.println("-------------------- Xenia Discord --------------------");
        }

        if(Boolean.parseBoolean(config.load("activated"))){
            new Thread(new XCore()).start();
        }else {
            System.out.println("[INFO] Bot has been deactivated. Please check configuration file.");
        }

    }
}
