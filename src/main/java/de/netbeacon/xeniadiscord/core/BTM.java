package de.netbeacon.xeniadiscord.core;

import de.netbeacon.xeniadiscord.util.BlackListUtility;
import de.netbeacon.xeniadiscord.util.Config;
import de.netbeacon.xeniadiscord.util.webhooks.twitch.TwitchHookManagement;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.*;

public class BTM implements Runnable{

    private JDA jda;

    BTM(JDA jda){
        this.jda = jda;
    }


    @Override
    public void run() {

        Timer time = new Timer();
        // create tasks
        TimerTask update_status = new TimerTask() {
            @Override
            public void run() {
                String[] activity = {new Config().load("bot_status"),"on "+jda.getGuilds().size()+" servers", "with "+user_count()+" users"};
                jda.getPresence().setActivity(Activity.playing(activity[new Random().nextInt(activity.length)]));
            }
        };
        TimerTask save_blacklist = new TimerTask() {
            @Override
            public void run() {
                if(!new BlackListUtility().writetofile()){
                    System.err.println("Updating blacklist failed.");
                }
            }
        };
        TimerTask update_twitchhooks = new TimerTask() {
            @Override
            public void run() {
                new TwitchHookManagement(jda).update();
            }
        };
        TimerTask reset_apicallcounter = new TimerTask() {
            @Override
            public void run() {
                new TwitchHookManagement(jda).resetapicalls();
            }
        };
        // schedule tasks
        time.schedule(update_status,1000*60,1000*60);               // wait 1 minute then update every minute
        time.schedule(save_blacklist, 1000*60*60, 1000*60*60);      // wait 1h then update every h
        time.schedule(update_twitchhooks,1000*60,1000*60*5);      // wait 30 seconds then update every 5 minutes
        time.schedule(reset_apicallcounter, 1000*60, 1000*60);      // wait 1 minute then update every minute
    }

    private int user_count(){
        List<String> users = new ArrayList<>();
        for(Guild guild : jda.getGuilds()){
            for (Member member : guild.getMembers()){
                if(!users.contains(member.getId())){
                    users.add(member.getId());
                }
            }
        }
        return users.size();
    }
}
