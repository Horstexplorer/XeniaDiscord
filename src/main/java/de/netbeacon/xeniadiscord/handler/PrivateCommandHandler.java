package de.netbeacon.xeniadiscord.handler;

import de.netbeacon.xeniadiscord.commands.privat.Admin;
import de.netbeacon.xeniadiscord.commands.privat.Help;
import de.netbeacon.xeniadiscord.commands.privat.Ping;
import de.netbeacon.xeniadiscord.commands.privat.PrivateCommand;
import de.netbeacon.xeniadiscord.util.Config;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.util.HashMap;

public class PrivateCommandHandler implements Runnable {

    private PrivateMessageReceivedEvent event;
    private static HashMap<String, PrivateCommand> commands = new HashMap<>();

    public PrivateCommandHandler(PrivateMessageReceivedEvent event){
        this.event = event;
        if(commands.isEmpty()){
            registercommands();
        }
    }

    @Override
    public void run() {
        // get message
        String message = event.getMessage().getContentRaw();
        // parse arguments from message
        String[] args = getargs(message);
        // check if requested command exists
        if(commands.containsKey(args[0])){
            //execute command
            commands.get(args[0]).execute(event,args);
        }else{
            // show error
            event.getChannel().sendMessage("Unknown command.").queue();
        }
    }

    private void registercommands(){
        // commands from commands.privat
        commands.put("help", new Help());
        commands.put("info", new Help());
        commands.put("commands", new Help());
        commands.put("admin", new Admin());
        commands.put("ping", new Ping());

    }

    public boolean containsCommand(String command){
        return commands.containsKey(command);
    }

    private String[] getargs(String raw){
        // remove bot_command_indicator from string
        raw = raw.replace(new Config().load("bot_command_indicator"), "").trim();
        // split string to args
        return raw.split(" ");
    }
}
