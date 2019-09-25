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
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class TwitchWorker implements Runnable{

    private int minratelimitremaining = 20;

    private static BlockingQueue<TwitchRequest> requestqueue;
    private static TwitchKey twitchKey;
    private boolean ismaster;
    private String id;

    private static AtomicBoolean keylock= new AtomicBoolean(false);
    private static AtomicBoolean ratelimitlock = new AtomicBoolean(false);

    public TwitchWorker(){}

    public TwitchWorker(boolean ismaster, String id){
        if(requestqueue == null || twitchKey == null){
            // get queue
            requestqueue = new TwitchWrap().getTasks();
            // get key
            twitchKey = new TwitchKey();
        }
        this.ismaster = ismaster;
        this.id = id;
    }

    @Override
    public void run() {
        new Log().addEntry("TW", "Starting TwitchWorker #"+id, 0);

        while(true){

            try{

                TwitchRequest twitchRequest = requestqueue.take();

                checkKey();
                while((keylock.get() || ratelimitlock.get()) && !ismaster){TimeUnit.SECONDS.sleep(1);}
                process(twitchRequest);

                if(ismaster && ratelimitlock.get()){
                    TimeUnit.MILLISECONDS.sleep(new Random().nextInt(500)+1000);
                }


            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    private void checkKey(){
        if(!twitchKey.isvalid()){ // if the key is not valid - if this dont exit our application here, it should be valid or not
            if(!keylock.get()){
                System.out.println("[INFO][TW] TwitchWorker #"+id+" created a lock: key_isinvalid");
                new Log().addEntry("TW", "[INFO][TW] TwitchWorker #"+id+" created a lock: key_isinvalid", 1);
                lock(1);
            }
            if(ismaster){
                twitchKey.update(); // try updating it - if this dont exit our application here, it could be renewed successfully
                System.out.println("[INFO][TW] TwitchWorker #"+id+" removed a lock: key_renewed");
                new Log().addEntry("TW", "[INFO][TW] TwitchWorker #"+id+" removed a lock: key_renewed", 0);
                unlock(1);
            }
        }
    }

    private void process(TwitchRequest twitchRequest){
        try{
            // get values from api
            URL url = new URL(twitchRequest.getRequest());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Bearer "+twitchKey.getToken());

            int ratelimitremaining = (Integer.parseInt(con.getHeaderField("ratelimit-remaining")));

            if((ratelimitremaining < minratelimitremaining) && !ratelimitlock.get()){
                System.out.println("[INFO][TW] TwitchWorker #"+id+" created a lock: ratelimit_reached");
                new Log().addEntry("TW", "[INFO][TW] TwitchWorker #"+id+" created a lock: ratelimit_reached", 1);
                lock(0);
            }
            if((ratelimitremaining > minratelimitremaining*2) && ismaster && ratelimitlock.get()){
                System.out.println("[INFO][TW] TwitchWorker #"+id+" removed a lock: ratelimit_reached");
                new Log().addEntry("TW", "[INFO][TW] TwitchWorker #"+id+" removed a lock: ratelimit_reached", 1);
                unlock(0);
            }

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


    public String nextestkeychange(){
        return new java.util.Date(((twitchKey.isvaliduntil()-86400)*1000))+"";
    }

    private void lock(int type){
        switch(type){
            case 0:
                ratelimitlock.set(true);
                break;
            case 1:
                keylock.set(true);
                break;
        }
    }

    private void unlock(int type){
        switch(type){
            case 0:
                ratelimitlock.set(false);
                break;
            case 1:
                keylock.set(false);
                break;
        }
    }

}
