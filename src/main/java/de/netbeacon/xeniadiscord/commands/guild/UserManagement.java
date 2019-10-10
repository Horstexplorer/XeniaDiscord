package de.netbeacon.xeniadiscord.commands.guild;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class UserManagement implements GuildCommand {

    @Override
    public void execute(GuildMessageReceivedEvent event, Member member, String[] args) {
        if(args[0].toLowerCase().equals("kick") && member.hasPermission(Permission.KICK_MEMBERS)){  // user needs permission
            if(args.length > 1){
                String userid = args[1].replaceAll("[^0-9]", "").trim();
                if(event.getGuild().getSelfMember().hasPermission(Permission.KICK_MEMBERS)){    // bot needs permission
                    event.getGuild().kick(userid).queue();
                }else{
                    event.getChannel().sendMessage("I can't do that.");
                }
            }else{
                event.getChannel().sendMessage("Command requires 1 argument (user)").queue();
            }
        }

        if(args[0].toLowerCase().equals("ban") && member.hasPermission(Permission.BAN_MEMBERS)){
            if(args.length > 2){
                String userid = args[1].replaceAll("[^0-9]", "").trim();
                int deldays = 0;
                try{
                    deldays = Integer.parseInt(args[2]);
                }catch (Exception ignore){}
                if(event.getGuild().getSelfMember().hasPermission(Permission.BAN_MEMBERS)){
                    event.getGuild().ban(userid, deldays).queue();
                }else{
                    event.getChannel().sendMessage("I can't do that.");
                }
            }else{
                event.getChannel().sendMessage("Command requires 2 arguments (user, deletetime)").queue();
            }
        }
    }
}
