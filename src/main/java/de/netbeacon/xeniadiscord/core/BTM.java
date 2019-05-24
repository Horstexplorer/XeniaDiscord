package de.netbeacon.xeniadiscord.core;

import de.netbeacon.xeniadiscord.util.BlackListUtility;
import de.netbeacon.xeniadiscord.util.Config;
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
        // shedule tasks
        time.schedule(update_status,1000*60,1000*60);            // wait 1 minute then update every minute
        time.schedule(save_blacklist, 1000*60*60, 1000*60*60);    // wait 1h then update every h
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
