package de.netbeacon.xeniadiscord.core.localcommands;

import net.dv8tion.jda.api.JDA;

public class Help implements LocalCommands {
    @Override
    public void execute(JDA jda, String[] args) {

        if(args[0].toLowerCase().equals("help")){
            System.out.println(
                    "\n"+ "Commands: "+"\n"+
                    " help                                        || show available commands"+"\n"+
                    " guild  <>                                   || "+"\n"+
                    "        list                                 || list all connected guilds"+"\n"+
                    "        leave <name/id>                      || leave specific guild"+"\n"+
                    " status <>                                   || "+"\n"+
                    "        dnd                                  || set online status to do_not_disturb"+"\n"+
                    "        idle                                 || set online status to idle"+"\n"+
                    "        online                               || set online status to online"+"\n"+
                    " info                                        || display information about this bot"+"\n"+
                    " errors <>                                   || "+"\n"+
                    "        list                                 || list all errors"+"\n"+
                    "        export                               || export errors to file"+"\n"+
                    "        reset                                || reset errors"+"\n"+
                    "\n"
            );
        }

    }
}
