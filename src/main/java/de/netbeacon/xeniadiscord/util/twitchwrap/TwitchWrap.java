package de.netbeacon.xeniadiscord.util.twitchwrap;

import de.netbeacon.xeniadiscord.util.twitchwrap.auth.TwitchKey;
import de.netbeacon.xeniadiscord.util.twitchwrap.request.TwitchRequest;
import org.json.JSONObject;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class TwitchWrap {

    private static BlockingQueue<TwitchRequest> requestQueue;

    public TwitchWrap(){
        if(requestQueue == null){
            System.out.println("[INFO] Init TwitchWrap");
            requestQueue = new ArrayBlockingQueue<TwitchRequest>(250000);
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
