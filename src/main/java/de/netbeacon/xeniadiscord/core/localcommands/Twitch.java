package de.netbeacon.xeniadiscord.core.localcommands;

import de.netbeacon.xeniadiscord.util.twitchwrap.gamecache.TwitchGameCache;
import de.netbeacon.xeniadiscord.util.webhooks.twitch.TwitchHookManagement;
import de.netbeacon.xeniadiscord.util.webhooks.twitch.TwitchHookObjekt;
import net.dv8tion.jda.api.JDA;

import java.util.Map;

public class Twitch implements LocalCommands {

    @Override
    public void execute(JDA jda, String[] args) {
        if(args[0].toLowerCase().equals("twitch") && args.length > 1){
            if (args[1].toLowerCase().equals("listhooks")){
                System.out.println("TwitchHooks: ");
                for(TwitchHookObjekt tho: new TwitchHookManagement(jda).getAll()){
                    System.out.println(" "+jda.getTextChannelById(tho.getGuildChannel()).getGuild().getName()+" #"+jda.getTextChannelById(tho.getGuildChannel()).getName()+"("+tho.getGuildChannel()+") "+tho.getChannelName()+" "+tho.getStatus());
                }
            }
            if (args[1].toLowerCase().equals("listgames")){
                System.out.println("Games: ");
                for(Map.Entry<String, String> entry : new TwitchGameCache().getAll().entrySet()) {
                    System.out.println(" "+entry.getValue()+" "+entry.getKey());
                }
            }
        }
    }

}
