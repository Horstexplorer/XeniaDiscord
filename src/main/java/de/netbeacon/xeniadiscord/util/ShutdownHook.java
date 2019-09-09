package de.netbeacon.xeniadiscord.util;

import de.netbeacon.xeniadiscord.util.log.Log;

public class ShutdownHook {

    public ShutdownHook(){
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            public void run()
            {
                new Log().addEntry("ShutdownHook", "Shutdownhook executed", 0);
                savefiles();
                System.out.println("[EXIT]");
            }
        });
    }

    private void savefiles(){
        System.out.println("[INFO] Saving files...");

        System.out.println("> Log");
        new Log().export();
        System.out.println("> Blacklist");
        new BlackListUtility().writetofile();
    }
}
