package de.netbeacon.xeniadiscord.util.twitchwrap.request;

import org.json.JSONObject;

public class TwitchRequest {

    private String request;
    private JSONObject result;

    public TwitchRequest(String request){
        this.request = request;
    }

    public void setResult(JSONObject result){
        this.result = result;
    }

    public synchronized void setFinished(){
        notify();
    }

    public String getRequest(){
        return request;
    }

    public JSONObject getResult(){
        return result;
    }
}
