package de.netbeacon.xeniadiscord.util.webhooks.twitch;

import de.netbeacon.xeniadiscord.util.Config;
import de.netbeacon.xeniadiscord.util.ErrorLog;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TwitchAPIWrap {

    private String twitchid;
    private static int apicalls = 0;

    public TwitchAPIWrap(){
        twitchid = new Config().load("app_twitch_clientid");
    }

    public void resetapicalls(){
        apicalls = 0;
    }

    public int getapicalls(){
        return apicalls;
    }

    public String getChannelid(String displayname){
        try{
            apicalls++;
            // get values from api
            URL url = new URL("https://api.twitch.tv/helix/users?login="+displayname);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Client-ID", twitchid);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            //parse json to new hashmap
            JSONObject json =  new JSONObject(content.toString());
            return json.getJSONArray("data").getJSONObject(0).getString("id");
        }catch (Exception e){
            //e.printStackTrace();
            new ErrorLog(1, "An error occurred while executing getChannelid() in TwitchAPIWrap: "+e.toString()); // lvl 2 ; displayname may just dont exist -> json is empty...
            return null;
        }
    }

    public String getStreamStatus(String channelid){
        try{
            apicalls++;
            // get values from api
            URL url = new URL("https://api.twitch.tv/helix/streams?user_id="+channelid);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Client-ID", twitchid);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            //parse json to new hashmap
            JSONObject json =  new JSONObject(content.toString());
            return json.getJSONArray("data").getJSONObject(0).getString("type");
        }catch (Exception e){
            //e.printStackTrace();
            new ErrorLog(4, "An error occurred while executing getStreamStatus() in TwitchAPIWrap: "+e.toString());
            return null;
        }
    }

    public HashMap<String, String> getStreamsStatus(HashMap<String, String> channelids){
        try{
            apicalls++;
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
            URL url = new URL("https://api.twitch.tv/helix/streams?user_id="+channelidsS);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Client-ID", twitchid);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            //parse json to new hashmap
            JSONObject json =  new JSONObject(content.toString());
            for(int n = 0; n < json.getJSONArray("data").length(); n++){
                String userid = json.getJSONArray("data").getJSONObject(n).getString("user_id");
                String status = json.getJSONArray("data").getJSONObject(n).getString("type");
                channelids.put(userid, status);
            }
            return  channelids;
        }catch (Exception e){
            //e.printStackTrace();
            new ErrorLog(4, "An error occurred while executing getStreamsStatus() in TwitchAPIWrap: "+e.toString());
            return null;
        }
    }

    public HashMap<String, String> getStreamsAdvanced(HashMap<String, String> channelids, List<TwitchHookObjekt> twitchHookObjekts){
        try{
            apicalls++;
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
            URL url = new URL("https://api.twitch.tv/helix/streams?user_id="+channelidsS);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Client-ID", twitchid);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            // parse json to new hashmap & update twitch objects with basic information
            JSONObject json =  new JSONObject(content.toString());
            for(int n = 0; n < json.getJSONArray("data").length(); n++){
                String userid = json.getJSONArray("data").getJSONObject(n).getString("user_id");
                String status = json.getJSONArray("data").getJSONObject(n).getString("type");
                channelids.put(userid, status);

                // get general information
                String title = json.getJSONArray("data").getJSONObject(n).getString("title");
                String turl = json.getJSONArray("data").getJSONObject(n).getString("thumbnail_url");
                turl = turl.replace("-{width}x{height}", "");
                for(TwitchHookObjekt tho : twitchHookObjekts){
                    if(tho.getChannelID().equals(userid)){
                        // update value
                        tho.setTitle(title);
                        tho.setThumbnailurl(turl);
                    }
                }
            }
            return  channelids;
        }catch (Exception e){
            //e.printStackTrace();
            new ErrorLog(4, "An error occurred while executing getStreamsAdvanced() in TwitchAPIWrap: "+e.toString());
            return null;
        }
    }

}
