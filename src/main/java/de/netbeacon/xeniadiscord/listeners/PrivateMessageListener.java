package de.netbeacon.xeniadiscord.listeners;

import de.netbeacon.xeniadiscord.handler.PrivateMessageHandler;
import de.netbeacon.xeniadiscord.util.Config;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PrivateMessageListener extends ListenerAdapter {

    private Config config;

    public PrivateMessageListener(){
        config = new Config();
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        // see if bot is "online" or at least "idle" (: is not dnd)
        if(!event.getJDA().getPresence().getStatus().equals(OnlineStatus.DO_NOT_DISTURB)){
            // modules should not interfere with default commands
            if(!event.getAuthor().isBot() && !event.getMessage().getContentRaw().startsWith(config.load("bot_command_indicator"))){
                System.out.println("[INFO][PRIV][MSG] "+event.getAuthor()+" >> "+event.getMessage().getContentRaw());
                new Thread(new PrivateMessageHandler(event)).start();
            }
        }
    }
}
