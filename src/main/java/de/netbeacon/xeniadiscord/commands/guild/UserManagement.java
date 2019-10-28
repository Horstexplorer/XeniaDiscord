package de.netbeacon.xeniadiscord.commands.guild;

import de.netbeacon.xeniadiscord.util.extperm.ExtPermManager;
import de.netbeacon.xeniadiscord.util.extperm.permission.ExtPerm;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class UserManagement implements GuildCommand {

    @Override
    public Permission[] bot_getReqPermissions() {
        return new Permission[]{Permission.KICK_MEMBERS, Permission.BAN_MEMBERS};
    }

    @Override
    public boolean bot_hasPermissions(GuildMessageReceivedEvent event) {
        return event.getGuild().getSelfMember().hasPermission(bot_getReqPermissions());
    }

    @Override
    public void execute(GuildMessageReceivedEvent event, Member member, String[] args) {
        if(args[0].toLowerCase().equals("kick") && new ExtPermManager().hasPermission(member, new ExtPerm[]{ExtPerm.admin, ExtPerm.membermanagement_all, ExtPerm.membermanagement_kick})){  // user needs one of those permission
            if(args.length > 1){
                if(event.getMessage().getMentionedUsers().size() > 0){
                    args[1] = event.getMessage().getMentionedUsers().get(0).getId();
                }
                event.getGuild().kick(args[1]).queue();
            }else{
                event.getChannel().sendMessage("Command requires 1 argument (user)").queue();
            }
        }

        if(args[0].toLowerCase().equals("ban") && new ExtPermManager().hasPermission(member, new ExtPerm[]{ExtPerm.admin, ExtPerm.membermanagement_all, ExtPerm.membermanagement_ban})){
            if(args.length > 2){
                if(event.getMessage().getMentionedUsers().size() > 0){
                    args[1] = event.getMessage().getMentionedUsers().get(0).getId();
                }
                int deldays = 0;
                try{
                    deldays = Integer.parseInt(args[2]);
                }catch (Exception ignore){}
                event.getGuild().ban(args[1], deldays).queue();
            }else{
                event.getChannel().sendMessage("Command requires 2 arguments (user, deletetime)").queue();
            }
        }
    }
}
