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
                    "I'm not sure how I can help you but you may want to try out one of the commands below for more information."+
                    "info - Provides some information about me :3\n";
            event.getChannel().sendMessage(msg).queue();
        }
        //info
        if(args[0].toLowerCase().equals("info")){
            String msg =    "------[ Info ]-----\n" +
                    "Running Xenia v "+ new Config().version()+"\n"+
                    "Ping: "+event.getJDA().getGatewayPing()+"\n"+
                    "Used by "+event.getJDA().getGuilds().size()+" guilds\n"+
                    "More information: https://xenia.netbeacon.de \n";
            event.getChannel().sendMessage(msg).queue();
        }
    }
}
