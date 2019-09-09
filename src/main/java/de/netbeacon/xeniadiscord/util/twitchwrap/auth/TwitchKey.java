package de.netbeacon.xeniadiscord.util.twitchwrap.auth;

import de.netbeacon.xeniadiscord.util.log.Log;
import de.netbeacon.xeniadiscord.util.twitchwrap.config.TwitchConfig;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class TwitchKey {

    private static TwitchConfig twitchConfig;
    private static String bearer_token;
    private static Long valid_until;

    public TwitchKey(){
       if(twitchConfig == null){
           twitchConfig = new TwitchConfig();
       }
       if(!twitchConfig.get("twitch_client_id").isEmpty() || !twitchConfig.get("twitch_client_secret").isEmpty()){
           new Log().addEntry("TK", "Init TwitchKey", 0);
           if(twitchConfig.get("twitch_bearer_token").isEmpty()){
               System.out.println("[INFO] Requesting bearer token...");
               if(requestbearer()){
                   System.out.println("[INFO] Requested bearer token successfully");
               }else{
                   System.out.println("[ERROR] Requesting bearer token failed");
                   System.exit(0);// exit will prevent undefined errors
               }
           }
           if(bearer_token == null || valid_until == null){
               bearer_token = twitchConfig.get("twitch_bearer_token");
               valid_until = Long.parseLong(twitchConfig.get("twitch_bearer_token_validuntil"));
           }
           if(update()){    // try updating the key
               System.out.println("[INFO] Updated bearer token ");
           }
       }else{
           System.out.println("[ERROR] Twitch user id and or secret missing");
           System.exit(0);  // exit will prevent undefined errors
       }
    }

    private boolean requestbearer(){
        try{
            URL url = new URL("https://id.twitch.tv/oauth2/token?client_id="+twitchConfig.get("twitch_client_id")+"&client_secret="+twitchConfig.get("twitch_client_secret")+"&grant_type=client_credentials");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
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
            bearer_token = json.getString("access_token");
            valid_until = ((System.currentTimeMillis()/1000L)+json.getInt("expires_in"));

            twitchConfig.update("twitch_bearer_token", bearer_token);
            twitchConfig.update("twitch_bearer_token_validuntil", valid_until+"");
            return true;
        }catch (Exception e){
            new Log().addEntry("TK", "Requesting bearer token failed: "+e.toString(), 5);
            e.printStackTrace();
            revokebearer(); // revoke if something went wrong, we want no unknown keys around
            return false;
        }
    }

    private boolean revokebearer(){
        try{
            URL url = new URL("https://id.twitch.tv/oauth2/revoke?client_id="+twitchConfig.get("twitch_client_id")+"&token="+bearer_token);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
            new Log().addEntry("TK", "Revoked bearer token", 0);
            return true;
        }catch (Exception e){
            new Log().addEntry("TK", "Revoking bearer token failed: "+e.toString(), 1);
            return false;
        }
    }

    public boolean isvalid(){
        try{
            URL url = new URL("https://id.twitch.tv/oauth2/validate");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "OAuth "+bearer_token);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
            return true;
        }catch (Exception e){
            new Log().addEntry("TK", "Could not check if key is valid: "+e.toString(), 4);
            return false;
        }
    }

    public boolean update(){
        if((((System.currentTimeMillis()/1000L)+86400) > valid_until) || !isvalid()){   // revoke if it will be invalid in the next 24h or if it is invalid
            new Log().addEntry("TK", "Updating bearer token", 0);
            revokebearer(); // it may still be valid so we try to revoke it
            if(requestbearer()){ // request a new one
                System.out.println("[INFO] Requested new bearer token successfully");
            }else{
                System.out.println("[ERROR] Requesting new bearer token failed");
                System.exit(0);// exit will prevent undefined errors
            }
            return true;
        }
        return false;
    }

    public String getToken(){
        return bearer_token;
    }

    public Long isvaliduntil(){
        return valid_until;
    }

}
