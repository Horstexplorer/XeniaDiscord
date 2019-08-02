package de.netbeacon.xeniadiscord.util.webhooks.twitch;

public class TwitchHookObjekt {

    private String guildchannelid;
    private String channelname;
    private String channelid;
    private String status;
    private String title;
    private String thumbnailurl;

    TwitchHookObjekt(String guildchannelid, String channelname, String channelid){
        this.guildchannelid = guildchannelid;
        this.channelname = channelname;
        this.channelid = channelid;
        this.status = "live";   // init as live so that if we start the bot and the channel is live we wont send a message (useful for restarting the bot when streams are online so that we dont notify twice)
        this.title = "unknown";
        this.thumbnailurl = "unknown";
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

    String getTitle(){ return this.title; }
    void setTitle(String title){ this.title = title; }

    String getThumbnailurl(){ return this.thumbnailurl; }
    void setThumbnailurl(String url){ this.thumbnailurl = url; }
}
