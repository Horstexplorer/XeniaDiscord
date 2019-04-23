package de.netbeacon.xeniadiscord.commands.privat;

import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

public interface PrivateCommand {

    void execute(PrivateMessageReceivedEvent event, String[] args);

}
