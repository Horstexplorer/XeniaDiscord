package de.netbeacon.xeniadiscord.util;

import de.netbeacon.xeniadiscord.modulemanagement.loader.CoreModuleLoader;
import de.netbeacon.xeniadiscord.modulemanagement.loader.ModuleLoader;
import de.netbeacon.xeniadiscord.util.extperm.ExtPermManager;
import de.netbeacon.xeniadiscord.util.log.Log;
import de.netbeacon.xeniadiscord.util.webhooks.twitch.TwitchHookManagement;

public class ShutdownHook {

    public ShutdownHook(){
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            public void run()
            {
                System.out.println("[INFO] Shutdownhook executed");
                new Log().addEntry("ShutdownHook", "Shutdownhook executed", 0);

                unloadmodules();
                savefiles();

            }
        });
    }

    private void savefiles(){
        System.out.println("[INFO] Saving files...");

        try{
            System.out.println("> ExtPerm");
            new ExtPermManager().writetofile();
        }catch (Exception ignore){}
        try{
            System.out.println("> Blacklist");
            new BlackListUtility().writetofile();
        }catch (Exception ignore){}
        try{
            System.out.println("> TwitchHooks");
            new TwitchHookManagement(null).writetofile();
        }catch (Exception ignore){}
        try{
            System.out.println("> Log");
            new Log().export();
        }catch (Exception ignore){}
    }

    private void unloadmodules(){
        System.out.println("[INFO] Unloading modules...");
        new CoreModuleLoader(false).disable();
        new ModuleLoader(false).disable();
    }
}
