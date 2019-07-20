package de.netbeacon.xeniadiscord.util.webhooks.twitch;

import de.netbeacon.xeniadiscord.util.Config;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TwitchAPIWrap {

    private String twitchid;

    public TwitchAPIWrap(){
        twitchid = new Config().load("app_twitch_clientid");
    }

    public String getChannelid(String displayname){
        try{
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
            return null;
        }
    }

    public HashMap<String, String> getChannelids(HashMap<String, String> displaynames){
        try{
            // build string from keys
            String usernames = "";
            Iterator iterator = displaynames.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry pair = (Map.Entry)iterator.next();
                usernames += pair.getKey(); // add value
                if(iterator.hasNext()){
                    usernames += "&login=";  // add delimiter
                }
            }

            // get values from api
            URL url = new URL("https://api.twitch.tv/helix/users?login="+usernames);
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
                String name = json.getJSONArray("data").getJSONObject(n).getString("display_name");
                String id = json.getJSONArray("data").getJSONObject(n).getString("id");
                displaynames.put(name, id);
            }

            // return hashmap
            return displaynames;
        }catch (Exception e){
            //e.printStackTrace();
            return null;
        }
    }

    public String getStreamStatus(String channelid){
        try{
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
            return null;
        }
    }

    public HashMap<String, String> getStreamsStatus(HashMap<String, String> channelids){
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
            return null;
        }
    }

}
