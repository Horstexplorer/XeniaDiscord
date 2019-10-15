package de.netbeacon.xeniadiscord.listeners;

import de.netbeacon.xeniadiscord.handler.GuildCommandHandler;
import de.netbeacon.xeniadiscord.listeners.cooldown.CooldownManager;
import de.netbeacon.xeniadiscord.util.BlackListUtility;
import de.netbeacon.xeniadiscord.util.Config;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.TimeUnit;

public class GuildCommandListener extends ListenerAdapter {

    private Config config;

    public GuildCommandListener(){
        config = new Config();
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        // see if bot is "online"
        if(event.getJDA().getPresence().getStatus().equals(OnlineStatus.ONLINE)){
            // modules should not interfere with default commands nor should the channel be included on the blacklist
            if(!event.getAuthor().isBot() && event.getMessage().getContentRaw().startsWith(config.load("bot_command_indicator")) && !new BlackListUtility().isincluded(event.getChannel().getId())){
                // check for cooldown
                if(!new CooldownManager().cooldown_isactive(event.getAuthor().getId())){
                    // add cooldown & execute
                    new CooldownManager().cooldown_activate(event.getAuthor().getId(), 2000);

                    System.out.println("[INFO][GUILD][CMD] "+event.getAuthor()+" >> "+event.getMessage().getContentRaw());
                    Thread gch = new Thread(new GuildCommandHandler(event));
                    gch.setDaemon(true);
                    gch.start();
                }else{
                    event.getChannel().sendMessage("Please dont spam commands.").queue(message -> {message.delete().queueAfter(3, TimeUnit.SECONDS);});
                }
            }
        }
    }
}
