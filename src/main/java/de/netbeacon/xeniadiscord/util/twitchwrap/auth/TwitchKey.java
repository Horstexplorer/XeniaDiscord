package de.netbeacon.xeniadiscord.util.twitchwrap.auth;

import de.netbeacon.xeniadiscord.util.log.Log;
import de.netbeacon.xeniadiscord.util.twitchwrap.config.TwitchConfig;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TwitchKey {

    private static TwitchConfig twitchConfig;
    private static String bearer_token;
    private static Long valid_until;

    public TwitchKey(){
       if(twitchConfig == null){
           twitchConfig = new TwitchConfig();
       }
       if(!twitchConfig.get("twitch_client_id").isEmpty() || !twitchConfig.get("twitch_client_secret").isEmpty()){
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
           update(); // try updating the key

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
            con.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            // parse json
            JSONObject json =  new JSONObject(content.toString());
            bearer_token = json.getString("access_token");
            valid_until = ((System.currentTimeMillis()/1000L)+json.getInt("expires_in"));
            // update values
            twitchConfig.update("twitch_bearer_token", bearer_token);
            twitchConfig.update("twitch_bearer_token_validuntil", valid_until+"");

            new Log().addEntry("TK", "Requested bearer token successfully", 0);
            return true;
        }catch (Exception e){
            new Log().addEntry("TK", "Requesting bearer token failed: "+e.toString(), 5);
            e.printStackTrace();
            return false;
        }
    }

    private void revokebearer(){
        // try up to 5 times to revoke the bearer
        boolean revoked = false;
        for(int i = 1; i <= 10; i++){
            try{
                URL url = new URL("https://id.twitch.tv/oauth2/revoke?client_id="+twitchConfig.get("twitch_client_id")+"&token="+bearer_token);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.connect();
                // get status code
                int responseCode = con.getResponseCode();
                if(responseCode == 200 || responseCode == 400){
                    // key is now invalid OR key already invalid
                    con.disconnect();
                    // cool and good
                    new Log().addEntry("TK", "Successfully revoked bearer token", 0);
                    revoked = true;
                    break;
                }
                // else
                new Log().addEntry("TK", "An error occured revoking the bearer token: "+responseCode+" - Attempt "+i, 3);
                con.disconnect();
            }catch (Exception e){
                new Log().addEntry("TK", "An error occured revoking the bearer token: "+e.toString()+" - Attempt "+i, 4);
            }
            try{
                // sleep for some time if check failed
                TimeUnit.MILLISECONDS.sleep(1000+new Random().nextInt(1000));
            }catch (Exception ignore){}
        }
        // exit if we couldn't revoke the token
        if(!revoked){
            new Log().addEntry("TK", "Revoking bearer token failed", 5);
            System.exit(0);// exit will prevent undefined errors
        }
    }

    public Boolean isvalid(){ // takes ~600ms :o
        // try checking up to 5 times whether the bearer is valid or not
        for(int i = 0; i <=4; i++){
            try{
                // build connection
                URL url = new URL("https://id.twitch.tv/oauth2/validate");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Authorization", "OAuth "+bearer_token);
                con.connect();
                // get status code
                int responseCode = con.getResponseCode();
                if(responseCode == 200){
                    // key is valid
                    con.disconnect();
                    return true;
                }
                if(responseCode == 401){
                    // key is invalid
                    con.disconnect();
                    return false;
                }

                // else
                new Log().addEntry("TK", "An error occured checking if key is valid: "+responseCode+" - Attempt "+i, 3);
                con.disconnect();
            }catch (Exception e){
                new Log().addEntry("TK", "An error occured checking if key is valid: "+e.toString()+" - Attempt "+i, 4);
            }
        }
        new Log().addEntry("TK", "Failed to check whether the bearer is valid or not.", 5);
        return null; // not able to check the key
    }

    public void update(){
        if((((System.currentTimeMillis()/1000L)+86400) > valid_until) || (isvalid() != null && !isvalid())){   // update if it will be invalid in the next 24h or if we KNOW it is invalid
            new Log().addEntry("TK", "Updating bearer token", 0);
            revokebearer(); // it may still be valid so we try to revoke it
            if(requestbearer()){ // request a new one
                System.out.println("[INFO] Requested new bearer token successfully");
                // cool and good
            }else{
                System.out.println("[ERROR] Requesting new bearer token failed");
                System.exit(0); // exit will prevent undefined errors
            }
        }
    }

    public String getToken(){
        return bearer_token;
    }

    public Long isvaliduntil(){
        return valid_until;
    }

}
