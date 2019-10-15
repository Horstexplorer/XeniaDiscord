package de.netbeacon.xeniadiscord.listeners.cooldown;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

public class CooldownManager {

    private static AtomicReference<List<String>> userids = new AtomicReference<>();
    private static Timer timer = new Timer();

    public CooldownManager(){
        if(userids.get() == null){
            userids.set(new ArrayList<String>());
        }
    }

    public boolean cooldown_isactive(String userid){
        return userids.get().contains(userid);
    }

    public void cooldown_activate(String userid, int milliseconds){
        if(!userids.get().contains(userid)){
            userids.get().add(userid);
            // start timer
            TimerTask tt = new TimerTask() {
                @Override
                public void run() {
                    userids.get().remove(userid);
                    this.cancel();
                }
            };
            timer.schedule(tt, milliseconds);
        }
    }


}
