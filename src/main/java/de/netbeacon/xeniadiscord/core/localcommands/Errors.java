package de.netbeacon.xeniadiscord.core.localcommands;

import de.netbeacon.xeniadiscord.util.ErrorLog;
import net.dv8tion.jda.api.JDA;

public class Errors implements LocalCommands{

    @Override
    public void execute(JDA jda, String[] args) {

        if(args[0].toLowerCase().equals("errors") && args.length > 1){

            if(args[1].toLowerCase().equals("list")){
                System.out.println("\n Errors:");
                for(String error : new ErrorLog(0, "").getErrors()){
                    System.out.println(" "+error);
                }
                System.out.println("");
            }

            if(args[1].toLowerCase().equals("export")){
                if(new ErrorLog(0 , "").export()){
                    System.out.println("\nExport successful!\n");
                }else{
                    System.out.println("\nExport failed!\n");
                }
            }

            if(args[1].toLowerCase().equals("reset")){
                new ErrorLog(0 , "").reset();
                System.out.println("\nError list cleared\n");
            }
        }
    }
}
