package de.netbeacon.xeniadiscord.core.localcommands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

public class Guilds implements LocalCommands {
    @Override
    public void execute(JDA jda, String[] args) {
        if(args[0].toLowerCase().contains("guild") && args.length > 1){

            if(args[1].toLowerCase().equals("list")){
                System.out.println("Guilds:");
                for(Guild g: jda.getGuilds()){
                    System.out.println(" "+g.getName()+"  "+g.getId()+"   "+g.getDescription());
                }
            }

            if(args[1].toLowerCase().equals("leave") && args.length > 2){
                boolean d = false;
                for(Guild g: jda.getGuilds()){
                    if(g.getId().equals(args[2]) || g.getName().equals(args[2])){
                        g.leave().queue();
                        d = true;
                    }
                }
                System.out.println("Leaving guild: "+args[2]+" Status: "+d);
            }
        }
    }

}
