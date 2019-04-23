package de.netbeacon.xeniadiscord.commands.guild;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public interface GuildCommand {

    boolean permission(Member member);
    void execute(GuildMessageReceivedEvent event, Member member, String[] args);

}
