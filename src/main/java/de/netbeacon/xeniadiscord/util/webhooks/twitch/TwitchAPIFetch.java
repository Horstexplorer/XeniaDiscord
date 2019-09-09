package de.netbeacon.xeniadiscord.util.webhooks.twitch;

import de.netbeacon.xeniadiscord.util.log.Log;
import de.netbeacon.xeniadiscord.util.twitchwrap.TwitchWrap;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class TwitchAPIFetch {

    TwitchAPIFetch(){

    }

    String getChannelid(String displayname){
        try{
            //parse json to new hashmap
            JSONObject json =  new TwitchWrap().get("https://api.twitch.tv/helix/users?login="+displayname);
            return json.getJSONArray("data").getJSONObject(0).getString("id");
        }catch (Exception e){
            //e.printStackTrace();
            new Log().addEntry("TAPIF", "An error occurred while executing getChannelid() in TwitchAPIFetch: "+e.toString(), 1);
            return null;
        }
    }

    HashMap<String, String> getStreamsAdvanced(HashMap<String, String> channelids, List<TwitchHookObjekt> twitchHookObjekts){
        try{
            // build string from keys
            String channelidsS = "";
            Iterator iterator = channelids.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry pair = (Map.Entry)iterator.next();
                channelidsS += pair.getKey(); // add value
                if(iterator.hasNext()){
                    channelidsS += "&user_id=";  // add delimiter
                }
            }

            // get values from api
            JSONObject json =  new TwitchWrap().get("https://api.twitch.tv/helix/streams?user_id="+channelidsS);
            for(int n = 0; n < json.getJSONArray("data").length(); n++){
                String userid = json.getJSONArray("data").getJSONObject(n).getString("user_id");
                String status = json.getJSONArray("data").getJSONObject(n).getString("type");
                channelids.put(userid, status);

                // get general information
                String title = json.getJSONArray("data").getJSONObject(n).getString("title");
                String turl = json.getJSONArray("data").getJSONObject(n).getString("thumbnail_url");
                turl = turl.replace("-{width}x{height}", "");
                String gameid = json.getJSONArray("data").getJSONObject(n).getString("game_id");
                for(TwitchHookObjekt tho : twitchHookObjekts){
                    if(tho.getChannelID().equals(userid)){
                        // update value
                        tho.setTitle(title);
                        tho.setThumbnailurl(turl);
                        tho.setGameid(gameid);
                    }
                }
            }
            return  channelids;
        }catch (Exception e){
            new Log().addEntry("TAPIF", "An error occurred while executing getStreamsAdvanced() in TwitchAPIFetch: "+e.toString(), 4);
            e.printStackTrace();
            return null;
        }
    }
}
