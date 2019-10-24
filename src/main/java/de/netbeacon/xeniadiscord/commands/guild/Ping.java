package de.netbeacon.xeniadiscord.commands.guild;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.time.temporal.ChronoUnit;

public class Ping implements GuildCommand {
    @Override
    public void execute(GuildMessageReceivedEvent event, Member member, String[] args) {
        if(args[0].toLowerCase().equals("ping")){
            EmbedBuilder ebx = new EmbedBuilder();
            ebx.setColor(getColorByPing(0));
            ebx.setDescription("Pong! "+0+"ms");
            event.getChannel().sendMessage(ebx.build()).queue(m -> {
                long ping = event.getMessage().getTimeCreated().until(m.getTimeCreated(), ChronoUnit.MILLIS);
                EmbedBuilder eb = new EmbedBuilder();
                eb.setColor(getColorByPing(ping));
                eb.setDescription("Pong! "+ping+"ms");
                m.editMessage(eb.build()).queue();
            });
        }
    }

    private Color getColorByPing(long ping) {
        if (ping < 25)
            return new Color( 0,  239,  255);
        else if (ping < 50)
            return new Color( 0,  255,  200);
        else if (ping < 75)
            return new Color( 0,  255,  128);
        else if (ping < 100)
            return new Color( 0,  255,  0);
        else if (ping < 125)
            return new Color( 128,  255,  0);
        else if (ping < 150)
            return new Color( 180,  255,  0);
        else if (ping < 175)
            return new Color( 255,  255,  0);
        else if (ping < 200)
            return new Color( 255,  200,  0);
        else if (ping < 250)
            return new Color( 255,  175,  0);
        else if (ping < 300)
            return new Color( 255,  150,  0);
        else if (ping < 500)
            return new Color( 255,  100,  0);

        return new Color( 255,  0,  0);
    }
}
