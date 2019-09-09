package de.netbeacon.xeniadiscord.core.localcommands;

import net.dv8tion.jda.api.JDA;

public class Help implements LocalCommands {
    @Override
    public void execute(JDA jda, String[] args) {

        if(args[0].toLowerCase().equals("help")){
            System.out.println(
                    "Commands: "+"\n"+
                    " broadcast <msg>                             || tries to send msg to all guilds"+"\n"+
                    " log <>                                      || "+"\n"+
                    "        list <errorlevel>                    || list all entrys equal or above giveb errorlevel"+"\n"+
                    "        export                               || export log to file"+"\n"+
                    "        reset                                || reset log"+"\n"+
                    " guild  <>                                   || "+"\n"+
                    "        list                                 || list all connected guilds"+"\n"+
                    "        leave <name/id>                      || leave specific guild"+"\n"+
                    " help                                        || show available commands"+"\n"+
                    " info                                        || display information about this bot"+"\n"+
                    " shutdown                                    || save all files and exit"+"\n"+
                    " status <>                                   || "+"\n"+
                    "        dnd                                  || set online status to do_not_disturb"+"\n"+
                    "        idle                                 || set online status to idle"+"\n"+
                    "        online                               || set online status to online"+"\n"+
                    " twitch <>                                   || "+"\n"+
                    "        listhooks                            || Lists all twitchhooks"+"\n"+
                    "        listgames                                || Lists all cached games"+"\n"+
                    ""
            );
        }

    }
}
