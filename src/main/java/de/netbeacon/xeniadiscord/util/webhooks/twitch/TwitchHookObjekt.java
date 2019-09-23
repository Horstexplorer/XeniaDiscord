package de.netbeacon.xeniadiscord.util.webhooks.twitch;

public class TwitchHookObjekt {

    private String guildchannelid;
    private String channelname;
    private String channelid;
    private String status;
    private String title;
    private String thumbnailurl;
    private String gameid;
    private String customnotification;
    private boolean notifyeveryone;

    TwitchHookObjekt(String channelname, String channelid, String guildchannelid, String customnotification, boolean notifyeveryone){
        this.guildchannelid = guildchannelid;
        this.channelname = channelname;
        this.channelid = channelid;
        this.customnotification = customnotification;
        this.notifyeveryone = notifyeveryone;
        this.status = "live";   // init as live so that if we start the bot and the channel is live we wont send a message (useful for restarting the bot when streams are online so that we dont notify twice)
        this.title = "unknown";
        this.thumbnailurl = "unknown";
        this.gameid = "unknown";
    }

    public String getGuildChannel(){return this.guildchannelid;}

    public String getChannelName(){
        return this.channelname;
    }

    String getChannelID(){
        return this.channelid;
    }

    String getNotification(){ return this.customnotification; }

    boolean allownotifyeveryone(){ return this.notifyeveryone; }

    public String getStatus(){
        return this.status;
    }
    void setStatus(String status){
        this.status = status;
    }

    String getTitle(){ return this.title; }
    void setTitle(String title){ this.title = title; }

    String getThumbnailurl(){ return this.thumbnailurl; }
    void setThumbnailurl(String url){ this.thumbnailurl = url; }

    String getGameid(){ return this.gameid; }
    void setGameid(String gameid){ this.gameid = gameid;}

    String toJSONString(){
        return "{\"twitchchannelname\":\""+this.channelname+",\"twitchchannelid\":\""+this.channelid+",\"guildchannelid\":\""+this.guildchannelid+"\",\"customnotification\":\""+this.customnotification+"\",\"notifyeveryone\":"+this.notifyeveryone+"}";
    }
}
