package de.netbeacon.xeniadiscord.listeners;

import de.netbeacon.xeniadiscord.handler.PrivateMessageHandler;
import de.netbeacon.xeniadiscord.util.Config;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PrivateMessageListener extends ListenerAdapter {

    private Config config;

    public PrivateMessageListener(){
        config = new Config();
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if(!event.getAuthor().isBot() && !event.getMessage().getContentRaw().startsWith(config.load("bot_command_indicator"))){   // modules should not interfere with default commands
            System.out.println("[INFO][PRIV][CMD] "+event.getAuthor()+" >> "+event.getMessage().getContentRaw());
            new Thread(new PrivateMessageHandler(event)).start();
        }
    }
}
