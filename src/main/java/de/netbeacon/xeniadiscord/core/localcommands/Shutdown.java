package de.netbeacon.xeniadiscord.core.localcommands;

import de.netbeacon.xeniadiscord.util.BlackListUtility;
import de.netbeacon.xeniadiscord.util.webhooks.twitch.TwitchHookManagement;
import net.dv8tion.jda.api.JDA;

public class Shutdown implements LocalCommands {

    @Override
    public void execute(JDA jda, String[] args) {
        if(args[0].toLowerCase().equals("shutdown")){
            new BlackListUtility().writetofile();
            new TwitchHookManagement(jda).writetofile();
            jda.shutdownNow();
            System.out.println("See you again!");
            try{
                Thread.sleep(2500);
            }catch (Exception ignore){}
            System.exit(0);
        }
    }

}
