package de.netbeacon.xeniadiscord.commands.guild;

import com.sedmelluq.discord.lavaplayer.tools.PlayerLibrary;
import de.netbeacon.xeniadiscord.util.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Help implements GuildCommand {

    @Override
    public Permission[] bot_getReqPermissions() {
        return new Permission[]{Permission.MESSAGE_WRITE};
    }

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
            eb.addField("JDA:",JDAInfo.VERSION, false);
            eb.addField("LavaPlayer:",PlayerLibrary.VERSION, false);
            eb.addField("More information:","https://xenia.netbeacon.de" , false);

            event.getChannel().sendMessage(eb.build()).queue();
        }
        //commands
        if(args[0].toLowerCase().equals("commands")) {

            String default_commands = "**Default Commands:** \n" +
                    "Command                                                   // Description\n" +
                    "////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\n" +
                    "guide                                                     // Introduction to functions \n"+
                    "permission                                                // Get information about permissions\n"+
                    "ping                                                      // Pong! \n"+
                    "kick <user>                                               // Kicks the user from the server\n" +
                    "ban <user>                                                // Bans the user from the server\n" +
                    "music <command>                                           // See 'Music Commands'\n" +
                    "ghost <channel> <msg>                                     // Send <msg> as bot to <channel>\n"+
                    "blacklist <add/remove> <channel>                          // Add <channel> to blacklist so that Xenia neither listen nor respond there\n";
            String default_commands_2 =
                    "twitchhook <>                                             // \n"+
                    "           list                                           // Shows all twitchhooks in the current channel \n"+
                    "           add <username>                                 // Add twitchhook for channel <username> to current channel \n"+
                    "           remove <username>                              // Remove twitchhook for <username> from the current channel \n"+
                    "           update <username> <setting> <value>            // Update <setting> to <value> for a specific twitchhook [setting value combinations: custom_message <String> | notify_everyone <boolean>]\n"+
                    "extperm <>                                                // \n"+
                    "        add <@role/roleid> <permission1> <permission2> .. // Add permissions to specified role \n"+
                    "        remove <@role/roleid> <permission1> <permi..   .. // Remove permissions from specified role \n"+
                    "        list <@role/roleid>                               // List all permissions given to role\n";

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
                    "volume <0-100>                              // Adjust the volume\n" +
                    "info                                        // Display information about the current song\n" +
                    "connect <channelid>                         // Connect to your current voice channel or the channel specified by id\n" +
                    "disconnect                                  // Disconnect from voice channel\n";

            String external_commands = "";
            try {
                external_commands = new String(Files.readAllBytes(Paths.get("commands.txt")));
            } catch (Exception ignore) {
            }


            event.getChannel().sendMessage("```" + default_commands + "```").queue();
            event.getChannel().sendMessage("```" + default_commands_2 + "```").queue();
            event.getChannel().sendMessage("```" + music_commands + "```").queue();
            if (!external_commands.equals("")) {
                event.getChannel().sendMessage("```" + external_commands + "```").queue();
            }
        }
        //permission
        if(args[0].toLowerCase().equals("permission")) {
            event.getChannel().sendMessage("An overview of the required permissions for the commands can be found here: https://github.com/Horstexplorer/XeniaDiscord#commands").queue();
        }
        //setup
        if(args[0].toLowerCase().equals("guide") && (member.hasPermission(Permission.ADMINISTRATOR) || member.hasPermission(Permission.MANAGE_SERVER))){
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Xenia - Instruction");
            embedBuilder.setDescription("Quick guide to features of Xenia");
            embedBuilder.addField("Features","Xenia is a chat and music bot which can be extended via plugins",false);
            embedBuilder.addField("Commands","The bot is controlled by commands. An overview can be found with the command "+new Config().load("bot_command_indicator")+"commands or under -further information-",false);
            embedBuilder.addField("Permission","Every command requires certain permissions a user must have in order to be able to use it. These can be assigned individually for each role. Nn overview of all permissions can be found under -further information-",false);
            embedBuilder.addField("Further information","Check out the git: https://github.com/Horstexplorer/XeniaDiscord",false);

            event.getChannel().sendMessage(embedBuilder.build()).queue();
        }
    }
}
