package de.netbeacon.xeniadiscord.commands.guild;

import de.netbeacon.xeniadiscord.util.extperm.ExtPermManager;
import de.netbeacon.xeniadiscord.util.extperm.permission.ExtPerm;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ExtPermission implements GuildCommand {

    @Override
    public void execute(GuildMessageReceivedEvent event, Member member, String[] args) {
        if(args[0].toLowerCase().equals("extperm") && (new ExtPermManager().hasPermission(member, new ExtPerm[]{ExtPerm.admin, ExtPerm.permission_manage}) || member.hasPermission(Permission.MANAGE_PERMISSIONS) || member.hasPermission(Permission.ADMINISTRATOR))){

            if(args.length > 3){
                if(event.getMessage().getMentionedRoles().size() > 0){
                    args[2] = event.getMessage().getMentionedRoles().get(0).getId();
                }
                if(args[1].toLowerCase().equals("add")){
                    JDA jda = event.getJDA();
                    Role role = null;
                    try{
                        role = jda.getRoleById(args[2]);
                    }catch(Exception e){
                        // role not found
                        event.getChannel().sendMessage("Invalid role").queue();
                    }
                    if(role != null){
                        // check permission (same guild, admin or manage_perm permission in this guild)
                        if((role.getGuild().getId().equals(member.getGuild().getId()))&&(member.hasPermission(Permission.MANAGE_PERMISSIONS) || member.hasPermission(Permission.ADMINISTRATOR))){
                            // add values to role
                            ExtPerm[] epa = new ExtPerm[args.length-3];
                            for(int i = 0; i < args.length-3; i++){
                                epa[i] = new ExtPermManager().getPermission(args[i+3]);
                            }
                            new ExtPermManager().addPermission(role,epa);
                            event.getChannel().sendMessage("Permissions for role "+role.getName()+" : "+new ExtPermManager().listPermission(role)).queue();
                        }else{
                            // invalid request
                            event.getChannel().sendMessage("Invalid request and / or missing permission.").queue();
                        }
                    }
                }
                if(args[1].toLowerCase().equals("remove")){
                    JDA jda = event.getJDA();
                    Role role = null;
                    try{
                        role = jda.getRoleById(args[2]);
                    }catch(Exception e){
                        // role not found
                        event.getChannel().sendMessage("Invalid role").queue();
                    }
                    if(role != null){
                        // check permission (same guild, admin or manage_perm permission in this guild)
                        if((role.getGuild().getId().equals(member.getGuild().getId()))&&(member.hasPermission(Permission.MANAGE_PERMISSIONS) || member.hasPermission(Permission.ADMINISTRATOR))){
                            // add remove values from role
                            ExtPerm[] epa = new ExtPerm[args.length-3];
                            for(int i = 0; i < args.length-3; i++){
                                epa[i] = new ExtPermManager().getPermission(args[i+3]);
                            }
                            new ExtPermManager().removePermission(role,epa);
                            event.getChannel().sendMessage("Permissions for role "+role.getName()+" : "+new ExtPermManager().listPermission(role)).queue();
                        }else{
                            // invalid request
                            event.getChannel().sendMessage("Invalid request and / or missing permission.").queue();
                        }
                    }
                }

            }
            if(args.length <= 3){
                if(event.getMessage().getMentionedRoles().size() > 0){
                    args[2] = event.getMessage().getMentionedRoles().get(0).getId();
                }
                if(args[1].toLowerCase().equals("list")){
                    JDA jda = event.getJDA();
                    Role role = null;
                    try{
                        role = jda.getRoleById(args[2]);
                    }catch(Exception e){
                        // role not found
                        event.getChannel().sendMessage("Invalid role").queue();
                    }
                    if(role != null){
                        // check permission (same guild, admin or manage_perm permission in this guild)
                        if((role.getGuild().getId().equals(member.getGuild().getId()))&&(member.hasPermission(Permission.MANAGE_PERMISSIONS) || member.hasPermission(Permission.ADMINISTRATOR))){
                            // list permission
                            event.getChannel().sendMessage("Permissions for role "+role.getName()+" : "+new ExtPermManager().listPermission(role)).queue();
                        }else{
                            // invalid request
                            event.getChannel().sendMessage("Invalid request and / or missing permission.").queue();
                        }
                    }
                }
            }
        }
    }
}
