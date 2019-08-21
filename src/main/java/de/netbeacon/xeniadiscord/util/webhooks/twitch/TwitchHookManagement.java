package de.netbeacon.xeniadiscord.util.webhooks.twitch;

import de.netbeacon.xeniadiscord.util.ErrorLog;
import de.netbeacon.xeniadiscord.util.twitchwrap.gamecache.TwitchGameCache;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TwitchHookManagement {

    private static TwitchAPIFetch twitchAPIFetch;
    private static List<TwitchHookObjekt> twitchHookObjekts;
    private static boolean update_isrunning;
    private static JDA jda;

    public TwitchHookManagement(JDA jda_){
        if(jda == null){
            jda = jda_;
        }
        if(twitchHookObjekts == null){
            System.out.println("[INFO] Init TwitchHooks");
            if(!loadfromfile()){
                System.out.println("[ERROR] Init TwitchHooks failed");
            }
            twitchAPIFetch = new TwitchAPIFetch();
        }
    }

    private boolean loadfromfile(){
        try{
            // check if file exists
            File twitchhookfile = new File("twitchhooks.storage");
            if (!twitchhookfile.exists()) {
                //Create the file
                twitchhookfile.createNewFile();
            }
            // init list
            twitchHookObjekts = new ArrayList<TwitchHookObjekt>();
            // read file
            BufferedReader br = new BufferedReader(new FileReader(twitchhookfile));
            String line;
            // format be like:
            // discord_channel channelname channelid
            while((line = br.readLine()) != null){
                // parse args
                String args[] = line.split(" ");
                if(args.length == 3){
                    // create new object if no other with same userid exists in that channel
                    boolean exists = false;
                    for(TwitchHookObjekt tho : twitchHookObjekts){
                        if(tho.getGuildChannel().equals(args[0]) && tho.getChannelID().equals(args[2])){
                            exists = true;
                            break;
                        }
                    }
                    if(!exists){
                        TwitchHookObjekt tho = new TwitchHookObjekt(args[0], args[1], args[2]);
                        twitchHookObjekts.add(tho);
                    }
                }
            }
            br.close();
            return true;
        }catch (Exception ignore){
            new ErrorLog(3, "Could not load TwitchHooks from file");
            return false;
        }
    }

    public boolean writetofile(){
        try{
            File twitchhookfile = new File("twitchhooks.storage");
            // delete to clear
            twitchhookfile.delete();
            // create new
            twitchhookfile.createNewFile();
            // write new content
            BufferedWriter writer = new BufferedWriter(new FileWriter("twitchhooks.storage"));
            for(TwitchHookObjekt tho : twitchHookObjekts){
                String line = tho.getGuildChannel()+" "+tho.getChannelName()+" "+tho.getChannelID();
                writer.write(line);
                writer.newLine();
            }
            writer.flush();
            writer.close();

            return true;
        }catch (Exception e){
            new ErrorLog(3, "Could not write TwitchHooks to file");
            return false;
        }
    }

    public void update(){
        // check if we are currently running an update so that we wont run in any api limits during our update
        if(!update_isrunning){
            try{
                // set update_isrunning to true
                update_isrunning = true;
                // create list with every UNIQUE channelid from our hooks
                List<String> uids = new ArrayList<String>();
                for(TwitchHookObjekt tho : twitchHookObjekts){
                    if(!uids.contains(tho.getChannelID())){
                        uids.add(tho.getChannelID());
                    }
                }

                // now we want to create batches of 100 uids or smaller than 100 when we have less left
                // using hashmaps because they are more convenient
                HashMap<String, String> hashMap = new HashMap<String, String>();
                int processed = 0;
                for(String uid : uids){
                    processed++;
                    // if our hashmap is "full", we clear it as we should have processed it before
                    if(hashMap.size()+1 > 100){
                        hashMap.clear();
                    }
                    // add to hashmap
                    hashMap.put(uid, "offline");
                    // if our hashmap contains 100 elements or this is the last element we have, we process it
                    if((hashMap.size() == 100) || (uids.size()==processed)){
                        // get online/offline results & update stream information
                        hashMap = twitchAPIFetch.getStreamsAdvanced(hashMap, twitchHookObjekts); // not using getStreamsStatus()
                        // process online/offline results
                        for(Map.Entry<String, String> result : hashMap.entrySet()){

                            // if status == offline (nothing changed) we need to set the status from every object with the matching id to offline
                            // do it with not tolowercase live, so we can be sure everything will work
                            if(!result.getValue().toLowerCase().equals("live")){
                                // find each twitchhookobject with the channelid and set status to offline
                                for(TwitchHookObjekt thos : twitchHookObjekts){
                                    if(result.getKey().equals(thos.getChannelID())){
                                        thos.setStatus("offline");
                                    }
                                }
                            }

                            // if status == live we need to check if it was live before (we do nothing) or if it wasnt ( we need to check & send a notification )
                            if(result.getValue().toLowerCase().equals("live")){
                                List<TwitchHookObjekt> toremove = new ArrayList<TwitchHookObjekt>();
                                // find each twitchhookobject with the channelid
                                for(TwitchHookObjekt thos : twitchHookObjekts){
                                    if(result.getKey().equals(thos.getChannelID())){
                                        // was online?
                                        if(!thos.getStatus().equals("live")){
                                            thos.setStatus("live");
                                            // get game
                                            String game = "unknown";
                                            if(!thos.getGameid().equals("unknown")){
                                                game = new TwitchGameCache().get(thos.getGameid());
                                            }
                                            boolean haspermission = false;
                                            // check permissions
                                            try{
                                                Guild guild = jda.getGuildChannelById(thos.getGuildChannel()).getGuild();
                                                TextChannel textChannel = guild.getTextChannelById(thos.getGuildChannel());
                                                haspermission = guild.getSelfMember().hasPermission(textChannel, Permission.MESSAGE_WRITE);
                                                if(haspermission){
                                                    jda.getTextChannelById(textChannel.getId()).sendMessage("@everyone").queue();

                                                    EmbedBuilder eb = new EmbedBuilder();
                                                    eb.setTitle(thos.getChannelName().substring(0, 1).toUpperCase() + thos.getChannelName().substring(1), null);    //username
                                                    eb.setColor(Color.MAGENTA);
                                                    eb.setDescription("Hey everyone! \n"+thos.getChannelName().substring(0, 1).toUpperCase() + thos.getChannelName().substring(1)+ " is now live on twitch playing "+game+"!\n Let's drop in! \n \n"+"["+thos.getTitle()+"](https://twitch.tv/"+thos.getChannelName()+")");
                                                    eb.setImage(thos.getThumbnailurl());
                                                    jda.getTextChannelById(textChannel.getId()).sendMessage(eb.build()).queue();
                                                }else{
                                                    toremove.add(thos);
                                                }
                                            }catch (Exception e){
                                                new ErrorLog(3, "An error occurred while sending notification for TwitchHooks: "+e.toString());
                                            }
                                        }// nothing to do
                                    }
                                }
                                twitchHookObjekts.removeAll(toremove);
                                toremove.clear();
                            }
                        }
                        hashMap.clear();

                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                new ErrorLog(4, "An error occurred while updating TwitchHooks:"+e.toString());
            }
            // update file
            writetofile();
            // we finished updating
            update_isrunning = false;
        }
    }

    public boolean add(String guildchannelid, String twitchname){
        for(TwitchHookObjekt tho : twitchHookObjekts){
            if(tho.getGuildChannel().equals(guildchannelid) && tho.getChannelName().equals(twitchname)){
                return false;
            }
        }
        try{
            String channelid = twitchAPIFetch.getChannelid(twitchname);
            if(channelid != null){
                twitchHookObjekts.add(new TwitchHookObjekt(guildchannelid, twitchname, channelid));
            }else{
                return false;
            }
        }catch (Exception e){
            new ErrorLog(4, "An error occurred while adding TwitchHook: Originated from "+guildchannelid+" : "+e.toString());
            return false;
        }
        return true;
    }

    public boolean remove(String guildchannelid, String twitchname){
        for(TwitchHookObjekt tho : twitchHookObjekts){
            if(tho.getGuildChannel().equals(guildchannelid) && tho.getChannelName().equals(twitchname)){
                twitchHookObjekts.remove(tho);
                return true;
            }
        }
        return false;
    }

    public String list(String guildchannelid){
        String list = "";
        for(TwitchHookObjekt tho : twitchHookObjekts){
            if(tho.getGuildChannel().equals(guildchannelid)){
                list += "> "+tho.getChannelName().substring(0, 1).toUpperCase() + tho.getChannelName().substring(1)+"\n";
            }
        }
        if(list.equals("")){
            list = "No webhooks found here!";
        }
        return list;
    }

    public int count(){
        return twitchHookObjekts.size();
    }
}

