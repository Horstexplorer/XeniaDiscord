package de.netbeacon.xeniadiscord.handler;

import de.netbeacon.xeniadiscord.modulemanagement.PrivateCoreModuleProcessor;
import de.netbeacon.xeniadiscord.modulemanagement.PrivateModuleProcessor;
import de.netbeacon.xeniadiscord.util.Config;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

public class PrivateMessageHandler implements Runnable {

    private PrivateMessageReceivedEvent event;
    private Config config;

    public PrivateMessageHandler(PrivateMessageReceivedEvent event){
        this.event = event;
        this.config = new Config();
    }

    @Override
    public void run() {
        boolean finished = false;
        // check if coremodule is activated
        if(Boolean.parseBoolean(config.load("bot_activate_coremodule"))){
            // try to handle with core module
            PrivateCoreModuleProcessor cmp = new PrivateCoreModuleProcessor(event);
            finished = cmp.handle();
        }
        // if coremodule didnt handle, try all other modules (if modules are activated)
        if(Boolean.parseBoolean(config.load("bot_activate_modules")) && !finished){
            PrivateModuleProcessor mp = new PrivateModuleProcessor(event);
            finished = mp.handle();
        }
        // if still nothing happend, try nothing else
    }
}
