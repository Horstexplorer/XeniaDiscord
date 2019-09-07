package de.netbeacon.xeniadiscord.util.twitchwrap;

import de.netbeacon.xeniadiscord.util.twitchwrap.request.TwitchRequest;
import de.netbeacon.xeniadiscord.util.twitchwrap.worker.TwitchWorker;
import org.json.JSONObject;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class TwitchWrap {

    private static BlockingQueue<TwitchRequest> requestQueue;
    private static Thread twitchworker;
    private static boolean blocked = false;

    public TwitchWrap(){
        // init queue
        if(requestQueue == null){
            System.out.println("[INFO] Init TwitchWrap");
            // init queue
            requestQueue = new ArrayBlockingQueue<TwitchRequest>(250000);
        }
        // init twitchworker
        if((twitchworker == null || twitchworker.getState().equals(Thread.State.TERMINATED)) && !blocked){
            blocked = true;
            twitchworker = new Thread(new TwitchWorker());
            twitchworker.start();
            blocked = false;
        }
    }

    public JSONObject get(String url_string){
        try{
            // create new TwitchRequest
            TwitchRequest twitchRequest = new TwitchRequest(url_string);
            // queue the request
            requestQueue.add(twitchRequest);
            // wait until finished
            synchronized (twitchRequest){twitchRequest.wait();}
            // return result
            return twitchRequest.getResult();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public BlockingQueue<TwitchRequest> getTasks(){
        return requestQueue;
    }

}
