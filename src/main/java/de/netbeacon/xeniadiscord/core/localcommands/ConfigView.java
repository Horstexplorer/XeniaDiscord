package de.netbeacon.xeniadiscord.core.localcommands;

import de.netbeacon.xeniadiscord.util.Config;
import net.dv8tion.jda.api.JDA;

public class ConfigView implements LocalCommands {
    @Override
    public void execute(JDA jda, String[] args) {

        if(args[0].toLowerCase().equals("config")){
            if(args.length > 3){
                if(args[1].toLowerCase().equals("update")){
                    if(new Config().updateproperties(args[2], args[3])){
                        System.out.println("Updated property "+args[2]);
                    }else{
                        System.out.println("Could not updated property "+args[2]);
                    }
                }
            }
        }

    }
}
