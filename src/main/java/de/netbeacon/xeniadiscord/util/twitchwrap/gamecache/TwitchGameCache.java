package de.netbeacon.xeniadiscord.util.twitchwrap.gamecache;

import de.netbeacon.xeniadiscord.util.ErrorLog;
import de.netbeacon.xeniadiscord.util.twitchwrap.TwitchWrap;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

public class TwitchGameCache {

    private static Map<String, String> gamecache;

    public TwitchGameCache(){
        System.out.println("[INFO] Init TwitchGameCache");
        if(gamecache == null){
            // init gamecache
            if(!init()){
                System.out.println("[ERROR] Init TwitchGameCache failed");
            }
        }
    }

    private boolean init(){
        try{
            // check if twitchgamecache.storage exists
            File gcfile = new File("twitchgamecache.storage");
            if (!gcfile.exists()) {
                //Create the file
                gcfile.createNewFile();
            }
            // init array list
            gamecache = new HashMap<String, String>();
            // read file & add to list
            BufferedReader br = new BufferedReader(new FileReader(gcfile));
            String line;
            while((line = br.readLine()) != null){
                String[] args = line.split(" ");
                gamecache.put(args[0], line.replace(args[0], "").trim());    // args[id] this is the game name
            }
            br.close();
        }catch (Exception e){
            new ErrorLog(2, "Could not read twitchgamecache from file:"+e);
            e.printStackTrace();
            return false;
        }
        try{
            // update all game ids
            List<String> gameids = new ArrayList<>();
            int processed = 0;
            for(Map.Entry<String, String> entry : gamecache.entrySet()){
                // check if the size would be over 100 (then we have already processed them)
                if(gameids.size()+1 > 100){
                    gameids.clear();
                }
                // add game id
                gameids.add(entry.getKey());
                processed++;
                // process if we have 100 game ids or if we have no more game ids
                if(gameids.size() == 100 || processed == gamecache.size()){
                    // build string
                    String request = "";
                    Iterator iterator = gameids.iterator();
                    while(iterator.hasNext()){
                        request += iterator.next(); // add value
                        if(iterator.hasNext()){
                            request += "&id=";  // add delimiter
                        }
                    }
                    //parse json to new hashmap
                    JSONObject json =  new TwitchWrap().get("https://api.twitch.tv/helix/games?id="+request);
                    for(int n = 0; n < json.getJSONArray("data").length(); n++){
                        String id = json.getJSONArray("data").getJSONObject(n).getString("id");
                        String game = json.getJSONArray("data").getJSONObject(n).getString("name");
                        gamecache.put(id, game);
                    }
                }
            }
            // save updated games
            writetofile();
        }catch (Exception e){
            new ErrorLog(2, "Could not update twitchgamecache: "+e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean writetofile(){
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter("twitchgamecache.storage"));
            for(Map.Entry<String, String> entry : gamecache.entrySet()){
                writer.write(entry.getKey()+" "+entry.getValue());
                writer.newLine();
            }
            writer.flush();
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
            new ErrorLog(3, "Could not write twitchgamecache to file: "+e);
            return false;
        }
        return true;
    }

    public String get(String id){
        if(gamecache.containsKey(id)){
            // return result
            return gamecache.get(id);
        }else{
            // fetch result
            String result = "";
            try{
                JSONObject json =  new TwitchWrap().get("https://api.twitch.tv/helix/games?id="+id);
                result = json.getJSONArray("data").getJSONObject(0).getString("name");
            }catch (Exception e){
                return null;
            }
            // add to cache
            if(result == null || result.isEmpty()){
                return null;
            }
            gamecache.put(id, result);
            // save
            writetofile();
            // return result
            return gamecache.get(id);
        }
    }
}