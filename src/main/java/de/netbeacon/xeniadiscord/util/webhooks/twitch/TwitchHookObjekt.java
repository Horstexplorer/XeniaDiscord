package de.netbeacon.xeniadiscord.util.webhooks.twitch;

public class TwitchHookObjekt {

    private String guildchannelid;
    private String channelname;
    private String channelid;
    private String status;

    TwitchHookObjekt(String guildchannelid, String channelname, String channelid){
        this.guildchannelid = guildchannelid;
        this.channelname = channelname;
        this.channelid = channelid;
        this.status = "offline";
    }

    String getGuildChannel(){return this.guildchannelid;}

    String getChannelName(){
        return this.channelname;
    }

    String getChannelID(){
        return this.channelid;
    }

    String getStatus(){
        return this.status;
    }

    void setStatus(String status){
        this.status = status;
    }
}
