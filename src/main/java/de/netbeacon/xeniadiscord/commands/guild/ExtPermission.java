package de.netbeacon.xeniadiscord.commands.guild;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ExtPermission implements GuildCommand {
    @Override
    public boolean permission(Member member) {
        return false;
    }

    @Override
    public void execute(GuildMessageReceivedEvent event, Member member, String[] args) {

    }
}
