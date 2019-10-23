package de.netbeacon.xeniadiscord.commands.privat;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.awt.*;

public class Ping implements PrivateCommand {
    @Override
    public void execute(PrivateMessageReceivedEvent event, String[] args) {
        if(args[0].toLowerCase().equals("ping")){
            long ping = event.getJDA().getGatewayPing();
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(getColorByPing(ping));
            eb.setDescription("Pong! "+ping+"ms");
            event.getChannel().sendMessage(eb.build()).queue();
        }
    }

    private Color getColorByPing(long ping) {
        if (ping < 100)
            return Color.cyan;
        else if (ping < 200)
            return Color.green;
        else if (ping < 500)
            return Color.yellow;
        else if (ping < 1000)
            return Color.orange;
        return Color.red;
    }
}
