package de.netbeacon.xeniadiscord.commands.guild;

import de.netbeacon.xeniadiscord.util.BlackListUtility;
import de.netbeacon.xeniadiscord.util.extperm.ExtPermManager;
import de.netbeacon.xeniadiscord.util.extperm.permission.ExtPerm;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Blacklist implements GuildCommand{

    @Override
    public void execute(GuildMessageReceivedEvent event, Member member, String[] args) {
        if(args[0].toLowerCase().equals("blacklist") && new ExtPermManager().hasPermission(member, new ExtPerm[]{ExtPerm.admin, ExtPerm.blacklist_manage})){
            if(args.length > 2){
                // get channel id
                String channel = args[2].replaceAll("[^0-9]", "").trim();
                // get action
                switch(args[1].toLowerCase()){
                    case "remove":
                        // check if user is allowed to modify this channel (and is currently in the specific guild)
                        if(member.hasPermission(member.getGuild().getTextChannelById(channel), Permission.MANAGE_CHANNEL)){
                            // remove channel from blacklist
                            if(new BlackListUtility().remove(channel)){
                                event.getChannel().sendMessage("Channel removed from blacklist.").queue();
                            }else{
                                event.getChannel().sendMessage("Channel could not be removed from blacklist.").queue();
                            }
                        }
                        break;
                    case  "add":
                        // check if user is allowed to modify this channel (and is currently in the specific guild)
                        if(member.hasPermission(member.getGuild().getTextChannelById(channel), Permission.MANAGE_CHANNEL)){
                            // add channel to blacklist
                            if(new BlackListUtility().add(channel)){
                                event.getChannel().sendMessage("Channel added to blacklist.").queue();
                            }else{
                                event.getChannel().sendMessage("Channel could not be added to blacklist.").queue();
                            }
                        }
                        break;
                }
            }else{
                event.getChannel().sendMessage("Command requires 2 arguments (action , channel)").queue();
            }
        }
    }
}
