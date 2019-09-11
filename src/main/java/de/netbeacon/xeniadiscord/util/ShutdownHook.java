package de.netbeacon.xeniadiscord.util;

import de.netbeacon.xeniadiscord.modulemanagement.loader.CoreModuleLoader;
import de.netbeacon.xeniadiscord.modulemanagement.loader.ModuleLoader;
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
                unloadmodules();

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

    private void unloadmodules(){
        System.out.println("[INFO] Unloading modules...");
        new CoreModuleLoader(false).disable();
        new ModuleLoader(false).disable();
    }
}
