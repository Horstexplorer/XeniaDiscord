package de.netbeacon.xeniadiscord.core;

import de.netbeacon.xeniadiscord.modulemanagement.loader.CoreModuleLoader;
import de.netbeacon.xeniadiscord.modulemanagement.loader.ModuleLoader;
import de.netbeacon.xeniadiscord.util.BlackListUtility;
import de.netbeacon.xeniadiscord.util.Config;
import de.netbeacon.xeniadiscord.util.extperm.ExtPermManager;
import de.netbeacon.xeniadiscord.util.log.Log;
import de.netbeacon.xeniadiscord.util.twitchwrap.TwitchWrap;
import de.netbeacon.xeniadiscord.util.twitchwrap.auth.TwitchKey;
import de.netbeacon.xeniadiscord.util.twitchwrap.gamecache.TwitchGameCache;
import de.netbeacon.xeniadiscord.util.webhooks.twitch.TwitchHookManagement;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
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
        // set status to dnd (not needed as it should be dnd by default)
        jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
        System.out.println("[INFO] Set status to "+jda.getPresence().getStatus()+" | Starting init");

        // init CoreModuleLoader
        if(Boolean.parseBoolean(new Config().load("bot_activate_coremodule"))){
            new CoreModuleLoader(true);
        }
        // init ModuleLoader
        if(Boolean.parseBoolean(new Config().load("bot_activate_modules"))){
            new ModuleLoader(true);
        }
        // init ExtPermManager
        new ExtPermManager(jda);
        // init blacklist
        new BlackListUtility();
        // init twitchwrap
        new TwitchWrap();
        // init twitchgamecache
        new TwitchGameCache();
        // init twitchhooks
        new TwitchHookManagement(jda);

        // set status to online
        jda.getPresence().setStatus(OnlineStatus.ONLINE);
        System.out.println("[INFO] Set status to "+jda.getPresence().getStatus()+" | Finished init");
    }

    private void createtasks(){
        new Log().addEntry("BTM", "Creating tasks", 0);
        Timer time = new Timer();
        // create tasks
        TimerTask update_status = new TimerTask() {
            @Override
            public void run() {
                String[] activity = {new Config().load("bot_status"),"on "+jda.getGuilds().size()+" guilds", "with "+user_count()+" users"};
                jda.getPresence().setActivity(Activity.playing(activity[new Random().nextInt(activity.length)]));
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
        TimerTask update_twitchgamecache = new TimerTask() {
            @Override
            public void run() {
                new TwitchGameCache().update();
            }
        };
        // schedule tasks
        time.schedule(update_status,1000*60,1000*60);               // wait 1 minute then update every minute
        time.schedule(update_twitchkey, 1000*60*10,1000*60*10);     // wait 10 minutes then update every 10 minutes
        time.schedule(update_twitchhooks, 1000*30, 1000*60*5);      // wait 30 seconds then update every 5 minutes
        time.schedule(update_twitchgamecache, 1000*60*60*24, 1000*60*60*24); // update every day
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
