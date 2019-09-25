package de.netbeacon.xeniadiscord.util.twitchwrap;

import de.netbeacon.xeniadiscord.util.twitchwrap.config.TwitchConfig;
import de.netbeacon.xeniadiscord.util.twitchwrap.request.TwitchRequest;
import de.netbeacon.xeniadiscord.util.twitchwrap.worker.TwitchWorker;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class TwitchWrap {


    private static BlockingQueue<TwitchRequest> requestQueue;
    private static List<Thread> workers = new ArrayList<Thread>();;
    private static boolean blocked = false;

    public TwitchWrap(){
        // init queue
        if(requestQueue == null){
            System.out.println("[INFO] Init TwitchWrap");
            // init queue
            requestQueue = new ArrayBlockingQueue<TwitchRequest>(250000);
        }
        // init twitchworker
        if(workers.isEmpty() && !blocked){
            blocked = true;

            // get config
            int maxworker = 0;
            try{
                maxworker = Integer.parseInt(new TwitchConfig().get("twitch_worker_max"));
            }catch (Exception ignore){}

            // create master
            Thread mt = new Thread(new TwitchWorker(true, "master"));
            mt.setDaemon(true);
            workers.add(mt);
            // create other
            for(int i = 1; i <= maxworker+1; i++){
                Thread t = new Thread(new TwitchWorker(false, i+""));
                t.setDaemon(true);
                workers.add(t);
            }
            // start
            for(Thread worker : workers){
                worker.start();
            }
            try{TimeUnit.MILLISECONDS.sleep(new Random().nextInt(201)+900);}catch(Exception ignore){}

            System.out.println("[INFO] Started "+(maxworker+1)+" TwitchWorker");

            blocked = false;
        }
        if(!workers.isEmpty() && !blocked){
            for(int i = 0; i < workers.size(); i++){
                Thread worker = workers.get(i);
                if(worker.getState().equals(Thread.State.TERMINATED)){
                    if(i == 0){
                        Thread t = new Thread(new TwitchWorker(true, "master"));
                        t.setDaemon(true);
                        workers.set(i, t);
                        workers.get(i).start();
                    }else{
                        Thread t = new Thread(new TwitchWorker(false, i+""));
                        t.setDaemon(true);
                        workers.set(i, t);
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

    public int getRemainingCapacity(){ return requestQueue.remainingCapacity(); }

}
