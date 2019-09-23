package de.netbeacon.xeniadiscord.util.twitchwrap.worker;

import de.netbeacon.xeniadiscord.util.log.Log;
import de.netbeacon.xeniadiscord.util.twitchwrap.TwitchWrap;
import de.netbeacon.xeniadiscord.util.twitchwrap.auth.TwitchKey;
import de.netbeacon.xeniadiscord.util.twitchwrap.request.TwitchRequest;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class TwitchWorker implements Runnable{

    // api
    private static int ratelimitremaining = 800; // placeholder
    // priv. prop.
    private static BlockingQueue<TwitchRequest> requestqueue;
    private static TwitchKey twitchKey;

    public TwitchWorker(){
        if(requestqueue == null || twitchKey == null){
            System.out.println("[INFO] Init TwitchWorker");
            // get queue
            requestqueue = new TwitchWrap().getTasks();
            // get key
            twitchKey = new TwitchKey();
        }
    }

    @Override
    public void run() {
        System.out.println("[INFO] Starting TwitchWorker");
        new Log().addEntry("TW", "Starting TwitchWorker", 0);
        try{
            boolean force = false;
            // prepare
            while(true){
                if(ratelimitremaining > 15 || force){
                    force = false;
                    TwitchRequest twitchRequest = requestqueue.take();
                    process(twitchRequest);
                }else{
                    // Wait for 1 seconds (should refill ~13 requests)
                    TimeUnit.SECONDS.sleep(1);
                    force = true;
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void process(TwitchRequest twitchRequest){
        try{
            if(!twitchKey.isvalid()){ // if the key is not valid - if this dont exit our application here, it should be valid or not
                twitchKey.update(); // try updating it - if this dont exit our application here, it could be renewed successfully
            }
            // get values from api
            URL url = new URL(twitchRequest.getRequest());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Bearer "+twitchKey.getToken());

            ratelimitremaining = Integer.parseInt(con.getHeaderField("ratelimit-remaining"));

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
            //parse json to new hashmap
            twitchRequest.setResult(new JSONObject(content.toString()));
            // set finished
            twitchRequest.setFinished();
        }catch (Exception e){
            new Log().addEntry("TW", "An error occured while processing request:  "+e.toString(), 4);
            twitchRequest.setResult(new JSONObject("{}"));
            twitchRequest.setFinished();
        }
    }

    public int getApilimit(){
        return ratelimitremaining;
    }

    public String nextestkeychange(){
        return new java.util.Date(((twitchKey.isvaliduntil()-86400)*1000))+"";
    }
}
