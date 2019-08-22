package de.netbeacon.xeniadiscord.core.localcommands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;

import java.util.List;

public class Broadcast implements LocalCommands {

    @Override
    public void execute(JDA jda, String[] args) {
        if(args[0].toLowerCase().equals("broadcast") && args.length > 1){
            // get guild
            List<Guild> guildList = jda.getGuilds();
            // build string
            String message = "";
            for(int i = 1; i < args.length; i++){
                message += args[i];
            }
            if(!message.isEmpty()){
                System.out.println("Broadcasting: "+message);
                int success = 0;
                int failed = 0;
                for(Guild guild : guildList){
                    // check perm
                    try{
                        if(guild.getSelfMember().hasPermission(guild.getDefaultChannel(), Permission.MESSAGE_WRITE)){
                            guild.getDefaultChannel().sendMessage(message).queue();
                            success++;
                        }else{
                            failed++;
                        }
                    }catch (Exception ignore){}
                }
                System.out.println("Broadcast : [OK] "+success+" [FAILED] "+failed);
            }
        }
    }

}
