package de.netbeacon.xeniadiscord.commands.guild;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public interface GuildCommand {

    Permission[] bot_getReqPermissions();

    boolean bot_hasPermissions(GuildMessageReceivedEvent event);

    void execute(GuildMessageReceivedEvent event, Member member, String[] args);

}
