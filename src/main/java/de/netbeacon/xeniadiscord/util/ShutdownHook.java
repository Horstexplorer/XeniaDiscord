package de.netbeacon.xeniadiscord.util;

import de.netbeacon.xeniadiscord.util.log.Log;

public class ShutdownHook {

    public ShutdownHook(){
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            public void run()
            {
                System.out.println("[INFO] Shutdownhook executed");
                new Log().addEntry("ShutdownHook", "Shutdownhook executed", 0);

                savefiles();

                System.out.println("[EXIT]");
                try{
                    sleep(1000);
                }catch (Exception e){
                    e.printStackTrace();
                }
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
