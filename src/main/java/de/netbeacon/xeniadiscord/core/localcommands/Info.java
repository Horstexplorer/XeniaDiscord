package de.netbeacon.xeniadiscord.core.localcommands;

import de.netbeacon.xeniadiscord.modulemanagement.GuildModuleProcessor;
import de.netbeacon.xeniadiscord.util.BlackListUtility;
import de.netbeacon.xeniadiscord.util.Config;
import de.netbeacon.xeniadiscord.util.log.Log;
import de.netbeacon.xeniadiscord.util.twitchwrap.gamecache.TwitchGameCache;
import de.netbeacon.xeniadiscord.util.twitchwrap.worker.TwitchWorker;
import de.netbeacon.xeniadiscord.util.webhooks.twitch.TwitchHookManagement;
import net.dv8tion.jda.api.JDA;

public class Info implements LocalCommands {
    @Override
    public void execute(JDA jda, String[] args) {

        if(args[0].toLowerCase().equals("info")){
            System.out.println("Xenia - Overview");
            System.out.println("Version: "+new Config().version());
            System.out.println("Guilds: "+jda.getGuilds().size()+" guilds");
            System.out.println("Blacklisted channels:\n " +new BlackListUtility().count()+" channels");
            System.out.println("TwitchWrap:\n "+"Next scheduled key change: "+new TwitchWorker().nextEstKeyChange()+"\n "+new TwitchHookManagement(jda).count()+" TwitchHooks registered\n "+new TwitchGameCache().count()+" games cached");
            System.out.println("Modules:\n " +new GuildModuleProcessor(null).listmodules());
            System.out.println("Log:\n "+ new Log().count(3)+" errors recorded");
            System.out.println("Memory:\n Used: "+((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1000000)+" / "+((Runtime.getRuntime().totalMemory())/1000000)+" MB");
        }

    }
}
