package de.netbeacon.xeniadiscord.commands.guild;

import de.netbeacon.xeniadiscord.util.webhooks.twitch.TwitchHookManagement;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class TwitchHook implements GuildCommand {

    @Override
    public boolean permission(Member member) {
        return member.hasPermission(Permission.MANAGE_CHANNEL);
    }

    @Override
    public void execute(GuildMessageReceivedEvent event, Member member, String[] args) {
        if(args[0].toLowerCase().equals("twitchhook")){
            if(args.length > 1){
                // list
                if(args[1].toLowerCase().equals("list")){
                    // return list of hooks in this channel
                    event.getChannel().sendMessage(new TwitchHookManagement(event.getJDA()).list(event.getChannel().getId())).queue();
                }else if(args.length > 3){
                    // add
                    if(args[1].toLowerCase().equals("add")){
                        String notification = "";

                        if(args.length > 4){ // more than [0][1][2][3]
                            // build notification if exist
                            for(int i = 3; i < args.length; i++){
                                notification += args[i]+" ";
                            }

                        }else{
                            // set default
                            notification = "Hey everyone! \\n %uname% is now live on twitch playing %game%! \\n Let's drop in! \\n";
                        }


                        // add hook to this channel
                        if(!new TwitchHookManagement(event.getJDA()).add(event.getChannel().getId(),args[2], notification, Boolean.parseBoolean(args[3]))){
                            event.getChannel().sendMessage("Failed to add! This error may result due to an incorrect username (or us reaching the rate limit - try again in a few moments).").queue();
                        }else{
                            event.getChannel().sendMessage("Successfully added!").queue();
                        }
                    }
                    // remove
                    if(args[1].toLowerCase().equals("remove")){
                        // remove hook from this channel
                        if(!new TwitchHookManagement(event.getJDA()).remove(event.getChannel().getId(),args[2])){
                            event.getChannel().sendMessage("Failed to remove! This error may result due to an incorrect user name.").queue();
                        }else{
                            event.getChannel().sendMessage("Successfully removed!").queue();
                        }
                    }
                }else {
                    event.getChannel().sendMessage("Command requires 3 arguments (<add> <twitchchannelname> <boolean for @everyone> [notification] OR <remove> <twitchchannelname>)").queue();
                }
            }else {
                event.getChannel().sendMessage("Command requires more arguments (<add/remove/list>)").queue();
            }
        }
    }
}
