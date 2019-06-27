package de.netbeacon.xeniadiscord.util.webhooks.twitch;

import net.dv8tion.jda.api.entities.GuildChannel;

public class TwitchHookObjekt {

    private GuildChannel channel;
    private String channelname;
    private String channelid;
    private String status;

    TwitchHookObjekt(GuildChannel channel, String channelname, String channelid){
        this.channel = channel;
        this.channelname = channelname;
        this.channelid = channelid;
        this.status = "null";
    }

    String getChannelName(){
        return this.channelname;
    }

    String getChannelID(){
        return this.getChannelID();
    }

    String getStatus(){
        return this.getStatus();
    }

    void setStatus(String status){
        this.status = status;
    }
}
