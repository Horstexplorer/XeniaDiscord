package de.netbeacon.xeniadiscord.core;

import de.netbeacon.xeniadiscord.listeners.*;
import de.netbeacon.xeniadiscord.modulemanagement.GuildCoreModuleProcessor;
import de.netbeacon.xeniadiscord.util.Config;
import de.netbeacon.xeniadiscord.util.webhooks.twitch.TwitchHookManagement;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

public class XCore implements Runnable{

    private JDA jda;
    private JDABuilder jdaBuilder;
    private Config config;

    XCore(){
        config = new Config();
    }

    @Override
    public void run() {
        try{
            jdaBuilder = new JDABuilder(AccountType.BOT);
            jdaBuilder.setToken(config.load("bot_token"));
            jdaBuilder.setActivity(Activity.of(Activity.ActivityType.DEFAULT, config.load("bot_status")));
            jdaBuilder.setAutoReconnect(true);
            addListeners();
            jda = jdaBuilder.build();
            jda.awaitReady();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            System.out.println("[INFO] >> Bot started");
        }

        //start backgroundtaskmanager
        startBTM();

        //start coremodulepreloader (onstart function)
        if(Boolean.parseBoolean(config.load("bot_activate_coremodule_backgroundtask"))){
            startcoremodulepreloader();
        }
    }

    private void addListeners(){
        jdaBuilder.addEventListeners(new PrivateMessageListener());
        jdaBuilder.addEventListeners(new PrivateCommandListener());
        jdaBuilder.addEventListeners(new GuildMessageListener());
        jdaBuilder.addEventListeners(new GuildCommandListener());
        jdaBuilder.addEventListeners(new GuildMemberJoinListener());
    }

    private void startBTM(){
        new Thread(new BTM(jda)).start();
    }

    private void startcoremodulepreloader(){
        new GuildCoreModuleProcessor(null).startbackgroundtask(jda);
    }
}
