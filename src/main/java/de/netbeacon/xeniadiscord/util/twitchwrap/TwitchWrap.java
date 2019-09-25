package de.netbeacon.xeniadiscord.util.twitchwrap;

import de.netbeacon.xeniadiscord.util.twitchwrap.config.TwitchConfig;
import de.netbeacon.xeniadiscord.util.twitchwrap.request.TwitchRequest;
import de.netbeacon.xeniadiscord.util.twitchwrap.worker.TwitchWorker;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class TwitchWrap {


    private static BlockingQueue<TwitchRequest> requestQueue;
    private static List<Thread> workers;
    private static boolean blocked = false;

    public TwitchWrap(){
        // init queue
        if(requestQueue == null){
            System.out.println("[INFO] Init TwitchWrap");
            // init queue
            requestQueue = new ArrayBlockingQueue<TwitchRequest>(250000);
        }
        // init twitchworker
        if(workers == null && !blocked){
            blocked = true;

            // get config
            int maxworker = 0;
            try{
                maxworker = Integer.parseInt(new TwitchConfig().get("twitch_worker_max"));
            }catch (Exception ignore){}
            workers = new ArrayList<Thread>();
            // create master
            workers.add(new Thread(new TwitchWorker(true, "master")));
            // create other
            for(int i = 1; i <= maxworker+1; i++){
                workers.add(new Thread(new TwitchWorker(false, i+"")));
            }
            // start
            for(Thread worker : workers){
                worker.start();
            }
            try{TimeUnit.SECONDS.sleep(1);}catch(Exception ignore){}

            System.out.println("[INFO] Started "+(maxworker+1)+" TwitchWorker");

            blocked = false;
        }
        if(workers != null && !blocked){
            for(int i = 0; i < workers.size(); i++){
                Thread worker = workers.get(i);
                if(worker.getState().equals(Thread.State.TERMINATED)){
                    if(i == 0){
                        workers.set(i, new Thread(new TwitchWorker(true, "master")));
                        workers.get(i).start();
                    }else{
                        workers.set(i, new Thread(new TwitchWorker(false, i+"")));
                        workers.get(i).start();
                    }
                }
            }
        }
    }

    public JSONObject get(String url_string){
        try{
            // create new TwitchRequest
            TwitchRequest twitchRequest = new TwitchRequest(url_string);
            if(requestQueue.remainingCapacity()-1 >= 0){
                // queue the request
                requestQueue.add(twitchRequest);
                // wait until finished
                synchronized (twitchRequest){twitchRequest.wait();}
                // return result
                return twitchRequest.getResult();
            }else{
                return new JSONObject("{}");
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public BlockingQueue<TwitchRequest> getTasks(){
        return requestQueue;
    }

    public int getremainingcapacity(){ return requestQueue.remainingCapacity(); }

}
