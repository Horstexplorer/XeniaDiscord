package de.netbeacon.xeniadiscord.listeners;

import de.netbeacon.xeniadiscord.handler.GuildCommandHandler;
import de.netbeacon.xeniadiscord.handler.GuildMessageHandler;
import de.netbeacon.xeniadiscord.listeners.cooldown.CooldownManager;
import de.netbeacon.xeniadiscord.util.BlackListUtility;
import de.netbeacon.xeniadiscord.util.Config;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.TimeUnit;

public class GuildMessageListener extends ListenerAdapter {

    private Config config;

    public GuildMessageListener(){
        config = new Config();
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        // see if bot is "online"
        if(event.getJDA().getPresence().getStatus().equals(OnlineStatus.ONLINE)){
            // modules should not interfere with default commands nor whould the channel be listen on the blacklist
            if(!event.getAuthor().isBot() && !event.getMessage().getContentRaw().startsWith(config.load("bot_command_indicator")) && !new BlackListUtility().isincluded(event.getChannel().getId())){
                // check for cooldown
                if(!new CooldownManager().cooldown_isactive(event.getAuthor().getId())){
                    // add cooldown & execute
                    new CooldownManager().cooldown_activate(event.getAuthor().getId(),1000);

                    System.out.println("[INFO][GUILD][MSG] "+event.getAuthor()+" >> "+event.getMessage().getContentRaw());
                    Thread gmh = new Thread(new GuildMessageHandler(event));
                    gmh.setDaemon(true);
                    gmh.start();
                }
                // ignore since the request was probably not for the bot
            }
        }
    }
}
