package de.netbeacon.xeniadiscord.util.webhooks.twitch;

import de.netbeacon.xeniadiscord.util.Config;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TwitchAPIWrap {

    private String twitchid;

    public TwitchAPIWrap(){
        twitchid = new Config().load("app_twitch_clientid");
    }

    public String getChannelid(String name){
        try{
            // get values from api
            URL url = new URL("https://api.twitch.tv/helix/users?login="+name);
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
            //parse json
            JSONObject json =  new JSONObject(content.toString());
            String id = json.getJSONArray("data").getJSONObject(0).getString("id");

            return id;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public String getChannelname(String id){
        try{

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return "";
    }

    public String getStatus(String id){

        return "";
    }
}
