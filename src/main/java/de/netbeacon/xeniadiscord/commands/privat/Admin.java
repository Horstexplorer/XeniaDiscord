package de.netbeacon.xeniadiscord.commands.privat;

import de.netbeacon.xeniadiscord.util.BlackListUtility;
import de.netbeacon.xeniadiscord.util.Config;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

public class Admin implements PrivateCommand {
    @Override
    public void execute(PrivateMessageReceivedEvent event, String[] args) {
        if(event.getAuthor().getId().equals(new Config().load("bot_admin_id"))){
            // shutdown bot
            if(args[0].toLowerCase().equals("shutdown")){
                event.getJDA().shutdownNow();
                System.exit(0);
            }
            // setstatus
            if(args[0].toLowerCase().equals("onlinestatus")){
                if (args.length>1){
                    switch (args[1].toLowerCase()){
                        case "dnd":
                            event.getJDA().getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
                            break;

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
            if(args[0].toLowerCase().equals("blacklistforcesave")){
                if (args.length>1){
                    if(!new BlackListUtility().writetofile()){
                        event.getChannel().sendMessage("Saving blacklist failed.").queue();
                    }else{
                        event.getChannel().sendMessage("Blacklist saved sucessfully").queue();
                    }
                }
            }
            // update config property values
            // force blacklist save
            if(args[0].toLowerCase().equals("config")){
                if (args.length>2){
                    if(new Config().updateproperties(args[1].toLowerCase(), args[2])){
                        event.getChannel().sendMessage("Value updated successfull.").queue();
                    }else{
                        event.getChannel().sendMessage("Update failed. Please check property name").queue();
                    }
                }
            }
        }
    }
}
