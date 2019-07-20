package de.netbeacon.xeniadiscord.util.webhooks.twitch;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TwitchHookManagement {

    private static List<TwitchHookObjekt> twitchHookObjekts;
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
            return false;
        }
    }

    public void update(){

        try{

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
                    // contact twitch
                    hashMap = new TwitchAPIWrap().getStreamsStatus(hashMap);
                    // process results
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

                                        boolean haspermission = false;
                                        // check permissions
                                        try{
                                            Guild guild = jda.getGuildChannelById(thos.getGuildChannel()).getGuild();
                                            TextChannel textChannel = guild.getTextChannelById(thos.getGuildChannel());
                                            haspermission = guild.getSelfMember().hasPermission(textChannel, Permission.MESSAGE_WRITE);
                                            if(haspermission){
                                                jda.getTextChannelById(textChannel.getId()).sendMessage(
                                                        "Hey @everyone !"+"\n"+
                                                                thos.getChannelName().substring(0, 1).toUpperCase() + thos.getChannelName().substring(1)+ " is now live on twitch!" +"\n"+
                                                                "Let's drop in!"+"\n"+
                                                                "https://twitch.tv/"+thos.getChannelName()
                                                ).queue();
                                            }else{
                                                toremove.add(thos);
                                            }
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }// nothing to do
                                }
                            }
                            twitchHookObjekts.removeAll(toremove);
                        }
                    }
                    hashMap.clear();
                }
                // update file
                writetofile();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public boolean add(String guildchannelid, String twitchname){
        for(TwitchHookObjekt tho : twitchHookObjekts){
            if(tho.getGuildChannel().equals(guildchannelid) && tho.getChannelName().equals(twitchname)){
                return false;
            }
        }
        try{
            String channelid = new TwitchAPIWrap().getChannelid(twitchname);
            if(channelid != null){
                twitchHookObjekts.add(new TwitchHookObjekt(guildchannelid, twitchname, channelid));
            }else{
                return false;
            }
        }catch (Exception e){
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
}

