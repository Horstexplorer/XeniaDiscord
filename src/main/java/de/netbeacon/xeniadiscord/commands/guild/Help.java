package de.netbeacon.xeniadiscord.commands.guild;

import de.netbeacon.xeniadiscord.util.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Help implements GuildCommand {

    @Override
    public void execute(GuildMessageReceivedEvent event, Member member, String[] args) {
        //help
        if(args[0].toLowerCase().equals("help")){
            String msg = "Hey, I'm Xenia.\n"+
                    "I'm not sure how I can help you but you may want to try out one of the commands below for more information.\n"+
                    "info - Provides some information about me :3\n"+
                    "commands - Shows a list of known commands ( Modules not included )\n";
            event.getChannel().sendMessage(msg).queue();
        }
        //info
        if(args[0].toLowerCase().equals("info")){
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Xenia - Overview", null);
            eb.setColor(Color.RED);
            eb.setDescription("Version: "+ new Config().version());
            eb.addField("Ping:",event.getJDA().getGatewayPing()+"ms", false);
            eb.addField("Guilds:",event.getJDA().getGuilds().size()+" guilds", false);
            eb.addField("More information:","https://xenia.netbeacon.de" , false);

            event.getChannel().sendMessage(eb.build()).queue();
        }
        //commands
        if(args[0].toLowerCase().equals("commands")) {

            String default_commands = "**Default Commands:** \n" +
                    "Command                                                   // Permission                   // Description\n" +
                    "////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\n" +
                    "kick <user>                                               // membermanagement_kick        // Kicks the user from the server\n" +
                    "ban <user>                                                // membermanagement_ban         // Bans the user from the server\n" +
                    "music <command>                                           // -                            // See 'Music Commands'\n" +
                    "ghost <channel> <msg>                                     // ghost                        // Send <msg> as bot to <channel>\n"+
                    "blacklist <add/remove> <channel>                          // blacklist_manage             // Add <channel> to blacklist so that Xenia neither listen nor respond there\n"+
                    "twitchhook <list|add/remove> <username> <boolean> [msg]   // twitchhooks_manage           // Add a webhook for a specific twitch channel to your textchannel; <boolean> true or false - use @everyone; If [msg] is set it is used as alternative notification (supports placeholders)\n"+
                    "extperm <add/remove/list> <permission1> <permission2> ... // permission_manage            // Manage permissions of a given role\n";

            String music_commands = "**Music Commands:** \n" +
                    "Command                                     // Permission                   // Description\n" +
                    "////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\n" +
                    "play <url>                                  // music_play                   // Add the song to the queue\n" +
                    "stop                                        // music_stop                   // Stops the playback and deletes the queue\n" +
                    "list                                        // music_play                   // Display songs in queue\n" +
                    "queue <num>                                 // music_play                   // Same as list\n" +
                    "next                                        // music_manage_queue           // Play next song in queue\n" +
                    "skip                                        // music_manage_queue           // Same as next\n" +
                    "shuffle                                     // music_manage_queue           // Shuffle queue\n" +
                    "info                                        // music_play                   // Display information about the current song\n" +
                    "off                                         // music_manage_off             // Disconnect from voice channel\n";

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
