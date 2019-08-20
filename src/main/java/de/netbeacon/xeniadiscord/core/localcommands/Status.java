package de.netbeacon.xeniadiscord.core.localcommands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;

public class Status implements LocalCommands{
    @Override
    public void execute(JDA jda, String[] args) {
        if(args[0].toLowerCase().equals("status") && args.length > 1){
            switch (args[2].toLowerCase()){
                case "dnd":
                    jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
                    break;
                case "idle":
                    jda.getPresence().setStatus(OnlineStatus.IDLE);
                    break;
                default:
                    jda.getPresence().setStatus(OnlineStatus.ONLINE);
                    break;
            }
            System.out.println("Updated online status to "+jda.getPresence().getStatus());
        }
    }
}
