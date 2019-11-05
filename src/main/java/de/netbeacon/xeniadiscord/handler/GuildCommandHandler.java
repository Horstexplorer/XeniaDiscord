package de.netbeacon.xeniadiscord.handler;

import de.netbeacon.xeniadiscord.commands.guild.*;
import de.netbeacon.xeniadiscord.util.Config;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Arrays;
import java.util.HashMap;

public class GuildCommandHandler implements Runnable {

    private GuildMessageReceivedEvent event;
    private static HashMap<String, GuildCommand> commands = new HashMap<>();

    public GuildCommandHandler(GuildMessageReceivedEvent event){
        this.event = event;
        if(commands.isEmpty()){
            registercommands();
        }
    }

    @Override
    public void run() {
        // only process if bot could respond
        if(event.getGuild().getSelfMember().hasPermission(event.getChannel(), Permission.MESSAGE_WRITE)){
            // get message
            String message = event.getMessage().getContentRaw();
            // parse arguments from message
            String[] args = getargs(message);
            // get member of author
            Member member = event.getMember();
            // check if requested command exists
            if(commands.containsKey(args[0].toLowerCase())){
                // check permissions
                if(commands.get(args[0].toLowerCase()).bot_hasPermissions(event)){
                    //execute command
                    commands.get(args[0].toLowerCase()).execute(event,member,args);
                }else{
                    // missing permissions
                    event.getChannel().sendMessage("I am missing some permissions to execute a command from this command group. I need these permissions: "+Arrays.toString(commands.get(args[0].toLowerCase()).bot_getReqPermissions())).queue();
                }
            }else{
                // unknown command
                event.getChannel().sendMessage("Unknown command. \n Try "+new Config().load("bot_command_indicator")+"commands for a list of some commands.").queue();
            }
        }
    }

    private void registercommands(){
        // commands from commands.guild
        commands.put("help", new Help());
        commands.put("info", new Help());
        commands.put("commands", new Help());
        commands.put("permission", new Help());
        commands.put("guide", new Help());
        commands.put("music", new Music());
        commands.put("kick", new UserManagement());
        commands.put("ban", new UserManagement());
        commands.put("ghost", new Ghost());
        commands.put("blacklist", new Blacklist());
        commands.put("twitchhook", new TwitchHook());
        commands.put("extperm", new ExtPermission());
        commands.put("ping", new Ping());

    }

    public boolean containsCommand(String command){
        return commands.containsKey(command.toLowerCase());
    }

    private String[] getargs(String raw){
        // remove bot_command_indicator from string
        raw = raw.replace(new Config().load("bot_command_indicator"), "").trim();
        // split string to args
        return raw.split(" ");
    }
}
