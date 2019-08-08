package de.netbeacon.xeniadiscord.util.twitchwrap.worker;

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
        try{
            boolean force = false;
            // prepare
            while(true){
                if(ratelimitremaining > 15 || force){
                    force = false;
                    // check if key isvalid
                    if(!twitchKey.isvalid()){
                        twitchKey.update();
                    }
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
            // get values from api
            URL url = new URL(twitchRequest.getRequest());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Bearer "+twitchKey.getToken());
            ratelimitremaining = Integer.parseInt(con.getHeaderField("ratelimit-remaining"));
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            //parse json to new hashmap
            twitchRequest.setResult(new JSONObject(content.toString()));
            // set finished
            twitchRequest.setFinished();

        }catch (Exception ignore){
            twitchRequest.setResult(new JSONObject("{}"));
            twitchRequest.setFinished();
        }
    }

    public int getApilimit(){
        return ratelimitremaining;
    }
}
