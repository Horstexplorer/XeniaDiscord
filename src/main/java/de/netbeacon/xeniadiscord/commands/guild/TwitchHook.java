package de.netbeacon.xeniadiscord.commands.guild;

import de.netbeacon.xeniadiscord.util.extperm.ExtPermManager;
import de.netbeacon.xeniadiscord.util.extperm.permission.ExtPerm;
import de.netbeacon.xeniadiscord.util.webhooks.twitch.TwitchHookManagement;
import de.netbeacon.xeniadiscord.util.webhooks.twitch.TwitchHookObjekt;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class TwitchHook implements GuildCommand {

    @Override
    public Permission[] bot_getReqPermissions() {
        return new Permission[]{Permission.MESSAGE_WRITE};
    }

    @Override
    public void execute(GuildMessageReceivedEvent event, Member member, String[] args) {
        if(new ExtPermManager().hasPermission(member, new ExtPerm[]{ExtPerm.admin, ExtPerm.twitchhooks_manage})){
            if(args.length > 1){
                // list
                if(args[1].toLowerCase().equals("list")){
                    // return list of hooks in this channel
                    int i = 1;
                    String msg = "";
                    for(TwitchHookObjekt tho : new TwitchHookManagement(event.getJDA()).list(event.getChannel().getId())){
                        i++;
                        msg += "> "+tho.getChannelName().substring(0, 1).toUpperCase() + tho.getChannelName().substring(1)+"\n"+"  "+"notification: "+tho.notifyEveryone()+" msg: "+tho.getCustomNotification().replaceAll("\n", "\\\\n")+"\n";
                        if(i <= 5){
                            event.getChannel().sendMessage(msg.substring(0,Math.min(msg.length(),1999))).queue();
                            i = 1;
                            msg = "";
                        }
                    }
                    }else if(args.length > 2){
                    // add
                    if(args[1].toLowerCase().equals("add")){
                        String notification = "Hey everyone! %n %uname% is now live on twitch playing %game%! %n Let's drop in! %n";
                        // add hook to this channel
                        if(!new TwitchHookManagement(event.getJDA()).add(event.getChannel().getId(),args[2].toLowerCase(), notification, true)){
                            event.getChannel().sendMessage("Failed to add! This error may result due to an incorrect username (or us reaching the rate limit - try again in a few moments).").queue();
                        }else{
                            event.getChannel().sendMessage("Successfully added! Use <update> <twitchchannelname> <setting> <newvalue> to change/edit notification settings").queue();
                        }
                    }
                    // remove
                    if(args[1].toLowerCase().equals("remove")){
                        // remove hook from this channel
                        if(!new TwitchHookManagement(event.getJDA()).remove(event.getChannel().getId(),args[2].toLowerCase())){
                            event.getChannel().sendMessage("Failed to remove! This error may result due to an incorrect user name.").queue();
                        }else{
                            event.getChannel().sendMessage("Successfully removed!").queue();
                        }
                    }
                    // update
                    if(args[1].toLowerCase().equals("update")){
                        if(args.length > 4){
                            if(args.length > 5){
                                for(int i = 4; i < args.length; i++){
                                    args[4] += args[i];
                                    if(i < args.length-1){
                                        args[4] += " ";
                                    }
                                }
                                // replace \ to remove escape sequences
                                args[4] = args[4].replaceAll("[\\\\]", "");
                            }
                            if(!new TwitchHookManagement(event.getJDA()).updatevalues(event.getChannel().getId(),args[2].toLowerCase(), args[3], args[4])){
                                event.getChannel().sendMessage("Failed to update! This error may result due to an incorrect username, invalid setting or invalid value. Avaible settings: notify_everyone, custom_message").queue();
                            }else{
                                event.getChannel().sendMessage("Setting changed successfully!").queue();
                            }
                        }else{
                            event.getChannel().sendMessage("Command requires 3 arguments (update <twitchchannelname> <setting> <newvalue>)").queue();
                        }
                    }
                }else {
                    event.getChannel().sendMessage("Command requires 2 arguments (<add> <twitchchannelname> OR <remove> <twitchchannelname>)").queue();
                }
            }else {
                event.getChannel().sendMessage("Command requires more arguments (<add/remove/list>)").queue();
            }
        }
    }
}
