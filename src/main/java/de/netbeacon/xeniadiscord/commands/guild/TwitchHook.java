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
                    //new TwitchHookHandler().list(event.getChannel());
                }
                if(args.length > 2){
                    // add
                    if(args[1].toLowerCase().equals("add")){
                        // add hook to this channel
                        if(!new TwitchHookManagement(event.getJDA()).add(event.getChannel().getId(),args[2])){
                            event.getChannel().sendMessage("Failed to add").queue();
                        }
                    }
                    // remove
                    if(args[1].toLowerCase().equals("list")){
                        // remove hook from this channel
                        if(!new TwitchHookManagement(event.getJDA()).remove(event.getChannel().getId(),args[2])){
                            event.getChannel().sendMessage("Failed to remove").queue();
                        }
                    }
                }else {
                    event.getChannel().sendMessage("Command requires 2 arguments (<add/remove> , twitchchannelname)").queue();
                }
            }else {
                event.getChannel().sendMessage("Command requires more arguments (<add/remove/list>)").queue();
            }
        }
    }
}
