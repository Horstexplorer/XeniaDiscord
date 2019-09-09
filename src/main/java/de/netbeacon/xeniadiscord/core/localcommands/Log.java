package de.netbeacon.xeniadiscord.core.localcommands;

import de.netbeacon.xeniadiscord.util.log.LogEntry;
import net.dv8tion.jda.api.JDA;

public class Log implements LocalCommands{
    @Override
    public void execute(JDA jda, String[] args) {

        if(args[0].toLowerCase().equals("log") && args.length > 1){

            if(args[1].toLowerCase().equals("list")){
                int errorlevel = 0;
                if(args.length > 2){
                    errorlevel = Integer.parseInt(args[2]);
                }
                System.out.println("Log: ("+new de.netbeacon.xeniadiscord.util.log.Log().count(errorlevel)+" entrys)");
                for(LogEntry logEntry : new de.netbeacon.xeniadiscord.util.log.Log().getEntrys(errorlevel)){
                    System.out.println(logEntry.getDate()+"|"+logEntry.getErrorlevel()+"|"+logEntry.getName()+"|"+logEntry.getDescription());
                }
            }

            if(args[1].toLowerCase().equals("export")){
                if(new de.netbeacon.xeniadiscord.util.log.Log().export()){
                    System.out.println("Export successful!");
                }else{
                    System.out.println("Export failed!");
                }
            }

            if(args[1].toLowerCase().equals("reset")){
                new de.netbeacon.xeniadiscord.util.log.Log().resetLog();
                System.out.println("Log cleared");
            }
        }
    }
}
