package de.netbeacon.xeniadiscord.commands.privat;

import de.netbeacon.xeniadiscord.util.Config;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Help implements PrivateCommand {

    @Override
    public void execute(PrivateMessageReceivedEvent event, String[] args) {
        //help
        if(args[0].toLowerCase().equals("help")){
            String msg = "Hey, I'm Xenia.\n"+
                    "info - Provides information about me\n"+
                    "commands - Shows a list of known commands ( Modules not included )\n";
            event.getChannel().sendMessage(msg).queue();
        }
        //info
        if(args[0].toLowerCase().equals("info")){
            String msg =    "------[ Info ]-----\n" +
                    "Running Xenia v "+ new Config().version()+"\n"+
                    "Ping: "+event.getJDA().getGatewayPing()+"\n"+
                    "Used by "+event.getJDA().getGuilds().size()+" guilds\n"+
                    "Add to your guild: https://discordapp.com/oauth2/authorize?client_id=509065864763408385&scope=bot \n";
            event.getChannel().sendMessage(msg).queue();
        }
        //commands
        if(args[0].toLowerCase().equals("commands")) {
            event.getChannel().sendMessage("Sorry, commands are not avaible ").queue();
        }
    }
}
