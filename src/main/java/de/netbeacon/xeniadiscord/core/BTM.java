package de.netbeacon.xeniadiscord.core;

import de.netbeacon.xeniadiscord.util.BlackListUtility;
import de.netbeacon.xeniadiscord.util.Config;
import de.netbeacon.xeniadiscord.util.twitchwrap.TwitchWrap;
import de.netbeacon.xeniadiscord.util.twitchwrap.auth.TwitchKey;
import de.netbeacon.xeniadiscord.util.twitchwrap.worker.TwitchWorker;
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
        init();
        createtasks();

    }

    private void init(){
        // init twitchwrap
        new TwitchWrap();
        // init twitchworker
        new Thread(new TwitchWorker()).start();
        // init twitchhooks
        new TwitchHookManagement(jda);
    }

    private void createtasks(){
        Timer time = new Timer();
        // create tasks
        TimerTask update_status = new TimerTask() {
            @Override
            public void run() {
                String[] activity = {new Config().load("bot_status"),"on "+jda.getGuilds().size()+" servers", "with "+user_count()+" users"};
                jda.getPresence().setActivity(Activity.playing(activity[new Random().nextInt(activity.length)]));
            }
        };
        TimerTask save_files = new TimerTask() {
            @Override
            public void run() {
                new BlackListUtility().writetofile();
            }
        };
        TimerTask update_twitchkey = new TimerTask() {
            @Override
            public void run() {
                new TwitchKey().update();
            }
        };
        TimerTask update_twitchhooks = new TimerTask() {
            @Override
            public void run() {
                new TwitchHookManagement(jda).update();
            }
        };
        // schedule tasks
        time.schedule(update_status,1000*60,1000*60);               // wait 1 minute then update every minute
        time.schedule(save_files, 1000*60*60, 1000*60*60);          // wait 1h then update every h
        time.schedule(update_twitchkey, 1000*60*10,1000*60*10);     // wait 10 minutes then update every 10 minutes
        time.schedule(update_twitchhooks, 1000*30, 1000*60*5);      // wait 30 seconds then update every 5 minutes
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
