package de.netbeacon.xeniadiscord.core;

import de.netbeacon.xeniadiscord.util.BlackListUtility;
import de.netbeacon.xeniadiscord.util.Config;
import de.netbeacon.xeniadiscord.util.ErrorLog;
import de.netbeacon.xeniadiscord.util.twitchwrap.TwitchWrap;
import de.netbeacon.xeniadiscord.util.twitchwrap.auth.TwitchKey;
import de.netbeacon.xeniadiscord.util.twitchwrap.gamecache.TwitchGameCache;
import de.netbeacon.xeniadiscord.util.twitchwrap.worker.TwitchWorker;
import de.netbeacon.xeniadiscord.util.webhooks.twitch.TwitchHookManagement;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.*;

public class BTM implements Runnable{

    private JDA jda;

    private Thread twitchworker;

    BTM(JDA jda){
        this.jda = jda;
    }


    @Override
    public void run() {
        init();
        createtasks();

    }

    private void init(){
        // init blacklist
        new BlackListUtility();
        // init twitchwrap
        new TwitchWrap();
        // init twitchworker
        twitchworker = new Thread(new TwitchWorker());
        twitchworker.start();
        // init twitchgamecache
        new TwitchGameCache();
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
                new TwitchHookManagement(jda).writetofile();
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
        TimerTask check_threads = new TimerTask() {
            @Override
            public void run() {
                if(!twitchworker.isAlive()){
                    System.err.println("[WARNING] TwitchWorker is not running.");
                    new ErrorLog(3,"TwitchWorker is not running; Restarting...");
                    twitchworker = new Thread(new TwitchWorker());
                    twitchworker.start();
                    try{
                        Thread.sleep(2500);// sleep 2.5 seconds
                    }catch (Exception ignore){}
                    if(!twitchworker.isAlive()){
                        System.err.println("[ERROR] Restarting TwitchWorker failed.");
                        new ErrorLog(4,"Restarting TwitchWorker failed.");
                    }
                }
            }
        };
        // schedule tasks
        time.schedule(update_status,1000*60,1000*60);               // wait 1 minute then update every minute
        time.schedule(save_files, 1000*60*60, 1000*60*60);          // wait 1h then update every h
        time.schedule(update_twitchkey, 1000*60*10,1000*60*10);     // wait 10 minutes then update every 10 minutes
        time.schedule(update_twitchhooks, 1000*30, 1000*60*5);      // wait 30 seconds then update every 5 minutes
        time.schedule(check_threads, 1000*60, 1000*60);             // wait 1 minute then update every minute
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
