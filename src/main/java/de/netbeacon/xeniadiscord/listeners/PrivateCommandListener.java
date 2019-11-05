package de.netbeacon.xeniadiscord.listeners;

import de.netbeacon.xeniadiscord.handler.PrivateCommandHandler;
import de.netbeacon.xeniadiscord.listeners.cooldown.CooldownManager;
import de.netbeacon.xeniadiscord.util.Config;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.TimeUnit;

public class PrivateCommandListener extends ListenerAdapter {

    private Config config;

    public PrivateCommandListener(){
        config = new Config();
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        // see if bot is "online" or at least "idle" (: is not dnd)
        if(!event.getJDA().getPresence().getStatus().equals(OnlineStatus.DO_NOT_DISTURB)){
            // modules should not interfere with default commands
            if(!event.getAuthor().isBot() && new PrivateCommandHandler(null).containsCommand(event.getMessage().getContentRaw().split(" ")[0].replace(new Config().load("bot_command_indicator"),""))){
                // check for cooldown
                if(!new CooldownManager().cooldown_isactive(event.getAuthor().getId())){
                    // add cooldown & execute
                    new CooldownManager().cooldown_activate(event.getAuthor().getId(),2000);

                    System.out.println("[INFO][PRIV][CMD] "+event.getAuthor()+" >> "+event.getMessage().getContentRaw());
                    Thread pch = new Thread(new PrivateCommandHandler(event));
                    pch.setDaemon(true);
                    pch.start();
                }else{
                    event.getChannel().sendMessage("Please dont spam commands.").queue(message -> {message.delete().queueAfter(3, TimeUnit.SECONDS);});
                }
            }
        }
    }
}
