package de.netbeacon.xeniadiscord.commands.privat;

import de.netbeacon.xeniadiscord.modulemanagement.GuildModuleProcessor;
import de.netbeacon.xeniadiscord.util.BlackListUtility;
import de.netbeacon.xeniadiscord.util.Config;
import de.netbeacon.xeniadiscord.util.log.Log;
import de.netbeacon.xeniadiscord.util.log.LogEntry;
import de.netbeacon.xeniadiscord.util.twitchwrap.gamecache.TwitchGameCache;
import de.netbeacon.xeniadiscord.util.twitchwrap.worker.TwitchWorker;
import de.netbeacon.xeniadiscord.util.webhooks.twitch.TwitchHookManagement;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.awt.*;
import java.util.List;

public class Admin implements PrivateCommand {
    @Override
    public void execute(PrivateMessageReceivedEvent event, String[] args) {
        if(event.getAuthor().getId().equals(new Config().load("bot_admin_id"))){
            if(args[0].toLowerCase().equals("admin") && args.length > 1){
                // shutdown bot
                if(args[1].toLowerCase().equals("shutdown")){
                    new BlackListUtility().writetofile();
                    new TwitchHookManagement(event.getJDA()).writetofile();
                    event.getJDA().shutdownNow();
                    System.exit(0);
                }
                // setstatus
                if(args[1].toLowerCase().equals("onlinestatus")){
                    if (args.length>2){
                        switch (args[2].toLowerCase()){
                            case "idle":
                                event.getJDA().getPresence().setStatus(OnlineStatus.IDLE);
                                break;

                            default:
                                event.getJDA().getPresence().setStatus(OnlineStatus.ONLINE);
                                break;
                        }
                    }
                }
                // force blacklist save
                if(args[1].toLowerCase().equals("blacklistforcesave")){
                    if(!new BlackListUtility().writetofile()){
                        event.getChannel().sendMessage("Saving blacklist failed.").queue();
                    }else{
                        event.getChannel().sendMessage("Blacklist saved successfully.").queue();
                    }
                }
                // force webhooks save
                if(args[1].toLowerCase().equals("twitchhookforcesave")){
                    if(!new TwitchHookManagement(event.getJDA()).writetofile()){
                        event.getChannel().sendMessage("Saving TwitchHooks failed.").queue();
                    }else{
                        event.getChannel().sendMessage("TwitchHooks saved successfully.").queue();
                    }
                }
                // update config property values
                if(args[1].toLowerCase().equals("config")){
                    if (args.length>3){
                        if(new Config().updateproperties(args[2].toLowerCase(), args[3])){
                            event.getChannel().sendMessage("Value updated successful.").queue();
                        }else{
                            event.getChannel().sendMessage("Update failed. Please check property name").queue();
                        }
                    }
                }
                // broadcast
                if(args[1].toLowerCase().equals("broadcast")){
                   if (args.length>2){
                       // get guild
                       List<Guild> guildList = event.getJDA().getGuilds();
                       // remove everything after broadcast
                       String message = event.getMessage().getContentRaw();
                       message = message.substring(message.indexOf(args[1])+args[1].length());
                       int success = 0;
                       int failed = 0;
                       for(Guild guild : guildList){
                           // check perm
                           if(guild.getSelfMember().hasPermission(guild.getDefaultChannel(), Permission.MESSAGE_WRITE)){
                               guild.getDefaultChannel().sendMessage(message).queue();
                               success++;
                           }else{
                               failed++;
                           }
                       }
                       event.getChannel().sendMessage("Broadcast : [OK] "+success+" [FAILED] "+failed).queue();
                   }
                }
                // status
                if(args[1].toLowerCase().equals("status")){
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle("Xenia - Overview", null);
                    eb.setColor(Color.RED);
                    eb.setDescription("Version: "+new Config().version());
                    eb.addField("Ping:",event.getJDA().getGatewayPing()+"ms", false);
                    eb.addField("Guilds:",event.getJDA().getGuilds().size()+" guilds", false);
                    eb.addField("Blacklisted channels:",new BlackListUtility().count()+" channels", false);
                    eb.addField("TwitchWrap:", "Current api-calls: "+(800-new TwitchWorker().getApilimit())/800+"% \n"+"Next scheduled key change: "+new TwitchWorker().nextestkeychange()+"\n"+new TwitchHookManagement(event.getJDA()).count()+" TwitchHooks registered\n"+new TwitchGameCache().count()+" games cached\n", false);
                    eb.addField("Modules:",new GuildModuleProcessor(null).listmodules(),false);
                    eb.addField("Errors:", new Log().count(2) +" errors recorded \n", false);
                    eb.addField("Memory:", "Used: "+((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1000000)+" / "+((Runtime.getRuntime().totalMemory())/1000000)+" MB", false);
                    event.getChannel().sendMessage(eb.build()).queue();
                }
                // errorlog
                if(args[1].toLowerCase().equals("log")){
                    if (args.length>2){
                        if(args[2].equals("listerrors")){
                            int pos = 0;
                            int max = new Log().count(3);
                            String result = "";
                            for(LogEntry logEntry : new Log().getEntrys(3)){
                                pos++;
                                if(pos +10 >= max){
                                    result += logEntry.getDate()+"|"+logEntry.getErrorlevel()+"|"+logEntry.getName()+"|"+logEntry.getDescription()+"\n";
                                }
                            }
                            if(result.length() > 2000){
                                result = result.substring(0,1999);
                            }
                            if(result.isEmpty()){
                                result = "No errors logged";
                            }
                            event.getChannel().sendMessage("Errors:").queue();
                            event.getChannel().sendMessage(result).queue();
                        }
                        if(args[2].equals("export")){
                            if(new Log().export()){
                                event.getChannel().sendMessage("Export successful!").queue();
                            }else{
                                event.getChannel().sendMessage("Export failed!").queue();
                            }
                        }
                        if(args[2].equals("reset")){
                            new Log().resetLog();
                            event.getChannel().sendMessage("Log cleared").queue();
                        }
                    }
                }
            }else{
                event.getChannel().sendMessage("View the README.md file for available admin commands.").queue();
            }
        }
    }
}
