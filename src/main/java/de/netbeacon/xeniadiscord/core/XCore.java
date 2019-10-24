package de.netbeacon.xeniadiscord.core;

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import de.netbeacon.xeniadiscord.listeners.*;
import de.netbeacon.xeniadiscord.util.Config;
import de.netbeacon.xeniadiscord.util.log.Log;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
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
        new Log().addEntry("XCore", "Preparing JDA", 0);
        try{
            jdaBuilder = new JDABuilder(AccountType.BOT);
            jdaBuilder.setToken(config.load("bot_token"));
            jdaBuilder.setActivity(Activity.of(Activity.ActivityType.DEFAULT, config.load("bot_status")));
            jdaBuilder.setAutoReconnect(true);
            jdaBuilder.setStatus(OnlineStatus.DO_NOT_DISTURB); // is set to online after BTM finished init of components
            jdaBuilder.setAudioSendFactory(new NativeAudioSendFactory());
            addListeners();
            jda = jdaBuilder.build();
            jda.awaitReady();
        }catch (Exception e){
            new Log().addEntry("XCore", "An error occurred preparing JDA: "+e.toString(), 5);
            e.printStackTrace();
            System.exit(-1);
        }finally {
            new Log().addEntry("XCore", "Bot started", 0);
            System.out.println("[INFO] >> Bot started");
        }

        //start backgroundtaskmanager
        startBTM();

        // listen to local commands
        startLCL();
    }

    private void addListeners(){
        jdaBuilder.addEventListeners(new PrivateMessageListener());
        jdaBuilder.addEventListeners(new PrivateCommandListener());
        jdaBuilder.addEventListeners(new GuildMessageListener());
        jdaBuilder.addEventListeners(new GuildCommandListener());
        jdaBuilder.addEventListeners(new GuildMemberJoinListener());
    }

    private void startBTM(){
        Thread btm = new Thread(new BTM(jda));
        btm.setDaemon(true);
        btm.start();
    }

    private void startLCL(){
        Thread lcl = new Thread(new LCL(jda));
        lcl.setDaemon(true);
        lcl.start();
    }
}
