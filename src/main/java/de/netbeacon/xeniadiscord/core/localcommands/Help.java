package de.netbeacon.xeniadiscord.core.localcommands;

import net.dv8tion.jda.api.JDA;

public class Help implements LocalCommands {
    @Override
    public void execute(JDA jda, String[] args) {

        if(args[0].toLowerCase().equals("help")){
            System.out.println(
                    "\n"+
                    "Commands: "+"\n"+
                    " help                                        || show available commands"+"\n"
            );
        }

    }
}
