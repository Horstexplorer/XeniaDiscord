package de.netbeacon.xeniadiscord.core.localcommands;

import de.netbeacon.xeniadiscord.util.ErrorLog;
import net.dv8tion.jda.api.JDA;

public class Errors implements LocalCommands{

    @Override
    public void execute(JDA jda, String[] args) {

        if(args[0].toLowerCase().equals("errorlog") && args.length > 1){

            if(args[1].toLowerCase().equals("list")){
                System.out.println("Errors:");
                for(String error : new ErrorLog(0, "").getErrors()){
                    System.out.println(" "+error);
                }
            }

            if(args[1].toLowerCase().equals("export")){
                if(new ErrorLog(0 , "").export()){
                    System.out.println("Export successful!");
                }else{
                    System.out.println("Export failed!");
                }
            }

            if(args[1].toLowerCase().equals("reset")){
                new ErrorLog(0 , "").reset();
                System.out.println("Error list cleared");
            }
        }
    }
}
