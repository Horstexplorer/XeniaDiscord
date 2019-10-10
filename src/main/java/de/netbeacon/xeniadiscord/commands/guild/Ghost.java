package de.netbeacon.xeniadiscord.commands.guild;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Ghost implements GuildCommand {

    @Override
    public void execute(GuildMessageReceivedEvent event, Member member, String[] args) {
        if(args[0].toLowerCase().equals("ghost")){
            if(args.length > 2){
                // get channel id
                String channelid = args[1].replaceAll("[^0-9]", "").trim();
                // get message
                // we get the message from the event again, otherwise we had to build it from multiple args[] which would be more work
                String msg = event.getMessage().getContentRaw();
                msg = msg.substring(msg.indexOf(channelid)+channelid.length()).trim(); // just take the part after the channel id which contains the msg
                // check if user has permission in this channel (user shouldnt be able to send messages to other servers where they wouldnt have enough permission)
                try{
                    if(member.hasPermission(member.getJDA().getTextChannelById(channelid), Permission.MANAGE_CHANNEL)){
                        event.getJDA().getTextChannelById(channelid).sendMessage(msg).queue();
                    }
                }catch (Exception ignore){}
            }else {
                event.getChannel().sendMessage("Command requires 2 arguments (channelid , msg)").queue();
            }
        }
    }
}
