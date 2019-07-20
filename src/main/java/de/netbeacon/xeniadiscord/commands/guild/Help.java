package de.netbeacon.xeniadiscord.commands.guild;

import de.netbeacon.xeniadiscord.util.Config;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Help implements GuildCommand {
    @Override
    public boolean permission(Member member) {
        return member.hasPermission(Permission.MESSAGE_WRITE);
    }

    @Override
    public void execute(GuildMessageReceivedEvent event, Member member, String[] args) {
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
                    "More information: https://xenia.netbeacon.de \n";
            event.getChannel().sendMessage(msg).queue();
        }
        //commands
        if(args[0].toLowerCase().equals("commands")) {

            String default_commands = "**Default Commands:** \n" +
                    "Command                                     // Required permission          // Description\n" +
                    "////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\n" +
                    "kick <user>                                 // Permission.KICK_MEMBERS      // Kicks the user from the server\n" +
                    "ban <user>                                  // Permission.BAN_MEMBERS       // Bans the user from the server\n" +
                    "music <command>                             // Permission.VOICE_CONNECT     // See 'Music Commands'\n" +
                    "ghost <channel> <msg>                       // Permission.MANAGE_CHANNEL    // Send <msg> as bot to <channel>\n"+
                    "blacklist <add/remove> <channel>            // Permission.MANAGE_CHANNEL    // Add <channel> to blacklist so that Xenia neither listen nor respond there\n"+
                    "twitchhook <list|add/remove> <username>     || Permission.MANAGE_CHANNEL    || Add a webhook for a specific twitch channel to your textchannel\n";

            String music_commands = "**Music Commands:** \n" +
                    "Command                                     // Description\n" +
                    "////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\n" +
                    "play <url>                                  // Add the song to the queue\n" +
                    "stop                                        // Stops the playback and deletes the queue\n" +
                    "list                                        // Display songs in queue\n" +
                    "queue <num>                                 // Same as list\n" +
                    "next                                        // Play next song in queue\n" +
                    "skip                                        // Same as next\n" +
                    "shuffle                                     // Shuffle queue\n" +
                    "info                                        // Displaying information about the current song\n" +
                    "off                                         // Disconnect from voice channel\n";

            String external_commands = "";
            try {
                external_commands = new String(Files.readAllBytes(Paths.get("commands.txt")));
            } catch (Exception ignore) {
            }


            event.getChannel().sendMessage("```" + default_commands + "```").queue();
            event.getChannel().sendMessage("```" + music_commands + "```").queue();
            if (!external_commands.equals("")) {
                event.getChannel().sendMessage("```" + external_commands + "```").queue();
            }
        }
    }
}
