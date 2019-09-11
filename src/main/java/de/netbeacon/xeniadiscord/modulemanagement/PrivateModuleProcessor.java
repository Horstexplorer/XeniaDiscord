package de.netbeacon.xeniadiscord.modulemanagement;

import de.netbeacon.xeniadiscord.modulemanagement.loader.ModuleLoader;
import de.netbeacon.xeniadiscord.util.log.Log;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.Map;

public class PrivateModuleProcessor {

    private PrivateMessageReceivedEvent event;
    private URLClassLoader urlcl;
    private Map<String, String> modules;
    private boolean isEnabled;

    public PrivateModuleProcessor(PrivateMessageReceivedEvent event){
        this.event = event;

        ModuleLoader moduleLoader = new ModuleLoader(true);
        urlcl = moduleLoader.getUrlcl();
        modules = moduleLoader.getModules();
        isEnabled = moduleLoader.isIsenabled();
    }

    public boolean handle(){
        boolean handled = false;
        if(isEnabled){
            for(Map.Entry<String, String> entry : modules.entrySet()) {
                try{
                    if(handled){
                        break;
                    }
                    Class<?> classToLoad = Class.forName(entry.getValue(), true, urlcl);
                    // execute module
                    Method method_exec = classToLoad.getDeclaredMethod("private_execute", PrivateMessageReceivedEvent.class); // MessageReceivedEvent event
                    Object instance_exec = classToLoad.getConstructor().newInstance();
                    Object result_exec = method_exec.invoke(instance_exec, event);

                    if(result_exec != null){
                        handled = (boolean) result_exec;
                    }
                }catch (Exception e){
                    new Log().addEntry("PMP", "An error occurred while handling module: "+entry.getKey()+" "+e.toString(), 3);
                    e.printStackTrace();
                }
            }
        }
        return handled;
    }
}
