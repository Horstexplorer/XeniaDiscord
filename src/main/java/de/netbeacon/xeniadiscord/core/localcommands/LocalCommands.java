package de.netbeacon.xeniadiscord.core.localcommands;

import net.dv8tion.jda.api.JDA;

public interface LocalCommands {

    void execute(JDA jda, String[] args);

}
