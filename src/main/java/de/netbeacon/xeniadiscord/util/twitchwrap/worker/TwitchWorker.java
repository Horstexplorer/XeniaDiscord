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
import java.util.concurrent.atomic.AtomicReference;

public class TwitchWorker implements Runnable{

    private int minRateLimitRemaining = 20;

    private static BlockingQueue<TwitchRequest> requestQueue;
    private boolean isMaster;
    private String id;

    private static AtomicReference<TwitchKey> twitchKey = new AtomicReference<>();
    private static AtomicBoolean keyLock= new AtomicBoolean(false);
    private static AtomicBoolean rateLimitLock = new AtomicBoolean(false);

    public TwitchWorker(){}

    public TwitchWorker(boolean isMaster, String id){
        if(requestQueue == null || twitchKey.get() == null){
            // get queue
            requestQueue = new TwitchWrap().getTasks();
            // get & set
            twitchKey.set(new TwitchKey());
        }
        this.isMaster = isMaster;
        this.id = id;
    }

    @Override
    public void run() {
        new Log().addEntry("TW", "Starting TwitchWorker #"+id, 0);

        while(true){

            try{

                TwitchRequest twitchRequest = requestQueue.take();

                checkKey();
                while((keyLock.get() || rateLimitLock.get()) && !isMaster){TimeUnit.MILLISECONDS.sleep(new Random().nextInt(21)+190);}
                process(twitchRequest);

                if(isMaster && rateLimitLock.get()){
                    TimeUnit.MILLISECONDS.sleep(new Random().nextInt(500)+1000);
                }


            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    private void checkKey(){

        if(twitchKey.get().isvalid() != null){ // we should be able to check the key, if not - ignore; request may fail but that's fine
            if(!twitchKey.get().isvalid()){ // if the key is not valid - if this dont exit our application here, it should be valid or not
                if(!keyLock.get()){
                    System.out.println("[INFO][TW] TwitchWorker #"+id+" created a lock: key_isinvalid");
                    new Log().addEntry("TW", "[INFO][TW] TwitchWorker #"+id+" created a lock: key_isinvalid", 1);
                    lock(1);
                }
                if(isMaster){
                    twitchKey.get().update(); // try updating it - if this dont exit our application here, it could be renewed successfully
                    System.out.println("[INFO][TW] TwitchWorker #"+id+" removed a lock: key_renewed");
                    new Log().addEntry("TW", "[INFO][TW] TwitchWorker #"+id+" removed a lock: key_renewed", 0);
                    unlock(1);
                }
            }
        }
    }

    private void process(TwitchRequest twitchRequest){
        try{
            // get values from api
            URL url = new URL(twitchRequest.getRequest());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Bearer "+twitchKey.get().getToken());

            int rateLimitRemaining = (Integer.parseInt(con.getHeaderField("ratelimit-remaining")));

            if((rateLimitRemaining < minRateLimitRemaining) && !rateLimitLock.get()){
                System.out.println("[INFO][TW] TwitchWorker #"+id+" created a lock: ratelimit_reached");
                new Log().addEntry("TW", "[INFO][TW] TwitchWorker #"+id+" created a lock: ratelimit_reached", 1);
                lock(0);
            }
            if((rateLimitRemaining > minRateLimitRemaining*2) && isMaster && rateLimitLock.get()){
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


    public String nextEstKeyChange(){
        return new java.util.Date(((twitchKey.get().isvaliduntil()-86400)*1000))+"";
    }

    private void lock(int type){
        switch(type){
            case 0:
                rateLimitLock.set(true);
                break;
            case 1:
                keyLock.set(true);
                break;
        }
    }

    private void unlock(int type){
        switch(type){
            case 0:
                rateLimitLock.set(false);
                break;
            case 1:
                keyLock.set(false);
                break;
        }
    }

}
