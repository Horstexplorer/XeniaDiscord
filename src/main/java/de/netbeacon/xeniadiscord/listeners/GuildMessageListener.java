package de.netbeacon.xeniadiscord.listeners;

import de.netbeacon.xeniadiscord.handler.GuildMessageHandler;
import de.netbeacon.xeniadiscord.util.BlackListUtility;
import de.netbeacon.xeniadiscord.util.Config;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildMessageListener extends ListenerAdapter {

    private Config config;

    public GuildMessageListener(){
        config = new Config();
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if(!event.getAuthor().isBot() && !event.getMessage().getContentRaw().startsWith(config.load("bot_command_indicator")) && !new BlackListUtility().isincluded(event.getChannel().getId())){   // modules should not interfere with default commands nor whould the channel be listen on the blacklist
            System.out.println("[INFO][GUILD][MSG] "+event.getAuthor()+" >> "+event.getMessage().getContentRaw());
            new Thread(new GuildMessageHandler(event)).start();
        }
    }
}
