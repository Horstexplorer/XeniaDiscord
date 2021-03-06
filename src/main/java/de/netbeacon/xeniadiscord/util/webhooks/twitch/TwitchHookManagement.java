package de.netbeacon.xeniadiscord.util.webhooks.twitch;

import de.netbeacon.xeniadiscord.util.log.Log;
import de.netbeacon.xeniadiscord.util.twitchwrap.gamecache.TwitchGameCache;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.json.JSONObject;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class TwitchHookManagement {

    private static TwitchAPIFetch twitchAPIFetch;
    private static AtomicReference<List<TwitchHookObjekt>> twitchHookObjekts = new AtomicReference<>();
    private static boolean update_isrunning;
    private static JDA jda;

    public TwitchHookManagement(JDA jda_){
        if(jda == null){
            jda = jda_;
        }
        if(twitchHookObjekts.get() == null){
            System.out.println("[INFO] Init TwitchHooks");
            new Log().addEntry("THM", "Init TwitchHooks", 0);
            if(!loadfromfile()){
                System.out.println("[ERROR] Init TwitchHooks failed");
            }else{
                System.out.println(">> "+twitchHookObjekts.get().size()+" entrys found.");
                twitchAPIFetch = new TwitchAPIFetch();
            }
        }
    }

    private boolean loadfromfile(){
        try{
            File path = new File("./data/storage/");
            if(!path.exists()){
                path.mkdirs();
            }
            // check if file exists
            File twitchhookfile = new File("./data/storage/twitchhooks.storage");
            if (!twitchhookfile.exists()) {
                //Create the file
                twitchhookfile.createNewFile();
            }
            // init list
            twitchHookObjekts.set(new ArrayList<TwitchHookObjekt>());
            // read file
            BufferedReader br = new BufferedReader(new FileReader(twitchhookfile));
            String line;
            while((line = br.readLine()) != null){
                if(!line.isEmpty()){
                    try{
                        // parse to json
                        JSONObject jsonObject = new JSONObject(line);
                        String name = jsonObject.getString("twitchchannelname");
                        String tid = jsonObject.getString("twitchchannelid");
                        String gid = jsonObject.getString("guildchannelid");
                        String cn = jsonObject.getString("customnotification");
                        boolean ne = jsonObject.getBoolean("notifyeveryone");

                        boolean exists = false;
                        for(TwitchHookObjekt tho : twitchHookObjekts.get()){
                            if(tho.getGuildChannel().equals(gid) && tho.getChannelID().equals(tid)){
                                exists = true;
                                break;
                            }
                        }
                        if(!exists){
                            TwitchHookObjekt tho = new TwitchHookObjekt(name, tid, gid, cn, ne);
                            twitchHookObjekts.get().add(tho);
                        }
                    }catch (Exception e){
                        new Log().addEntry("THM", "Could not load TwitchHook from file: "+e.toString(), 3);
                    }
                }
            }
            br.close();
            return true;
        }catch (Exception e){
            new Log().addEntry("THM", "Could not load TwitchHooks from file: "+e.toString(), 5);
            e.printStackTrace();
            return false;
        }
    }

    public boolean writetofile(){
        try{
            File path = new File("./data/storage/");
            if(!path.exists()){
                path.mkdirs();
            }
            // write new content
            BufferedWriter writer = new BufferedWriter(new FileWriter("./data/storage/twitchhooks.storage"));
            for(TwitchHookObjekt tho : twitchHookObjekts.get()){
                writer.write(tho.toJSONString());
                writer.newLine();
            }
            writer.flush();
            writer.close();

            return true;
        }catch (Exception e){
            new Log().addEntry("THM", "Could not write TwitchHooks to file: "+e.toString(), 5);
            e.printStackTrace();
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
                for(TwitchHookObjekt tho : twitchHookObjekts.get()){
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
                        // copy hashmap
                        HashMap<String, String> newHashMap = new HashMap<String,String>();
                        for(Map.Entry<String, String> entry : hashMap.entrySet()) {
                            newHashMap.put(entry.getKey()+"",entry.getValue()+"");
                        }
                        // process
                        execute(newHashMap);
                        // clear
                        hashMap.clear();
                    }
                }
            }catch (Exception e){
                new Log().addEntry("THM", "An error occurred while updating TwitchHooks: "+e.toString(), 4);
                e.printStackTrace();
            }
            // update file
            writetofile();
            // we finished updating
            update_isrunning = false;
        }
    }

    public boolean add(String guildchannelid, String twitchname, String customnotification, boolean notifyeveryone){
        for(TwitchHookObjekt tho : twitchHookObjekts.get()){
            if(tho.getGuildChannel().equals(guildchannelid) && tho.getChannelName().equals(twitchname)){
                return false;
            }
        }
        try{
            String channelid = twitchAPIFetch.getChannelid(twitchname);
            if(channelid != null){
                twitchHookObjekts.get().add(new TwitchHookObjekt(twitchname, channelid, guildchannelid, customnotification, notifyeveryone));
            }else{
                return false;
            }
        }catch (Exception e){
            new Log().addEntry("THM", "An error occurred while adding TwitchHook: Originated from "+guildchannelid+" : "+e.toString(), 4);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean remove(String guildchannelid, String twitchname){
        for(TwitchHookObjekt tho : twitchHookObjekts.get()){
            if(tho.getGuildChannel().equals(guildchannelid) && tho.getChannelName().equals(twitchname)){
                twitchHookObjekts.get().remove(tho);
                return true;
            }
        }
        return false;
    }

    public boolean updatevalues(String guildchannelid, String twitchname, String setting, String newvalue){
        for(TwitchHookObjekt tho : twitchHookObjekts.get()){
            if(tho.getGuildChannel().equals(guildchannelid) && tho.getChannelName().equals(twitchname)){
                switch(setting){
                    case "custom_message":
                        tho.setCustomNotification(newvalue);
                        return true;
                    case "notify_everyone":
                        tho.setNotifyEveryone(Boolean.parseBoolean(newvalue));
                        return true;
                    default:
                        return false;
                }
            }
        }
        return false;
    }

    public List<TwitchHookObjekt> list(String guildchannelid){
        List<TwitchHookObjekt> list = new ArrayList<>();
        for(TwitchHookObjekt tho : twitchHookObjekts.get()){
            if(tho.getGuildChannel().equals(guildchannelid)){
                list.add(tho);
            }
        }
        return list;
    }

    public int count(){
        return twitchHookObjekts.get().size();
    }

    public List<TwitchHookObjekt> getAll(){
        return twitchHookObjekts.get();
    }

    private void execute(HashMap<String, String> hashMap){
        class ExecuteTask implements Runnable{
            private HashMap<String, String> hashMap;

            private ExecuteTask(HashMap<String, String> hashMap){
                this.hashMap = hashMap;
            }
            @Override
            public void run() {
                // get online/offline results & update stream information
                hashMap = twitchAPIFetch.getStreamsAdvanced(hashMap, twitchHookObjekts.get()); // not using getStreamsStatus()
                // process online/offline results
                for(Map.Entry<String, String> result : hashMap.entrySet()){

                    // if status == offline (nothing changed) we need to set the status from every object with the matching id to offline
                    // do it with not tolowercase live, so we can be sure everything will work
                    if(!result.getValue().toLowerCase().equals("live")){
                        // find each twitchhookobject with the channelid and set status to offline
                        for(TwitchHookObjekt thos : twitchHookObjekts.get()){
                            if(result.getKey().equals(thos.getChannelID())){
                                thos.setStatus("offline");
                            }
                        }
                    }

                    // if status == live we need to check if it was live before (we do nothing) or if it wasnt ( we need to check & send a notification )
                    if(result.getValue().toLowerCase().equals("live")){
                        List<TwitchHookObjekt> toremove = new ArrayList<TwitchHookObjekt>();
                        // find each twitchhookobject with the channelid
                        for(TwitchHookObjekt thos : twitchHookObjekts.get()){
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
                                            try{
                                                EmbedBuilder eb = new EmbedBuilder();
                                                eb.setTitle(thos.getChannelName().substring(0, 1).toUpperCase() + thos.getChannelName().substring(1), null);    //username
                                                eb.setColor(Color.MAGENTA);
                                                // build custom message
                                                String message = thos.getCustomNotification();
                                                message = message.replace("%uname%", thos.getChannelName().substring(0, 1).toUpperCase() + thos.getChannelName().substring(1));
                                                message = message.replace("%lname%", thos.getChannelName());
                                                message = message.replace("%game%", game);
                                                message = message.replace("%title%", thos.getTitle());
                                                message = message.replace("%n", "\n");
                                                message += "["+thos.getTitle()+"](https://twitch.tv/"+thos.getChannelName()+")"; // add link to stream
                                                eb.setDescription(message);
                                                eb.setImage(thos.getThumbnailurl());

                                                if(thos.notifyEveryone()){
                                                    jda.getTextChannelById(textChannel.getId()).sendMessage("@everyone").queue();
                                                }
                                                jda.getTextChannelById(textChannel.getId()).sendMessage(eb.build()).queue();
                                            }catch (Exception e){
                                                new Log().addEntry("THM", "An error occurred while sending notification for TwitchHooks: "+e.toString(), 4);
                                                toremove.add(thos);
                                                jda.getTextChannelById(textChannel.getId()).sendMessage("An unknown error occured sending stream notification for user "+thos.getChannelName()+"; Removing notifications for this user.").queue();
                                            }
                                        }else{
                                            toremove.add(thos);
                                        }
                                    }catch (Exception e){
                                        new Log().addEntry("THM", "An error occurred while sending notification for TwitchHooks: "+e, 3);
                                        toremove.add(thos);
                                    }
                                }// nothing to do
                            }
                        }
                        twitchHookObjekts.get().removeAll(toremove);
                        toremove.clear();
                    }
                }
                hashMap.clear();
            }
        }
        Thread t = new Thread(new ExecuteTask(hashMap));
        t.setDaemon(true);
        t.start();
    }
}

