package de.netbeacon.xeniadiscord.modulemanagement;

import de.netbeacon.xeniadiscord.modulemanagement.loader.CoreModuleLoader;
import de.netbeacon.xeniadiscord.util.log.Log;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class PrivateCoreModuleProcessor {

    private PrivateMessageReceivedEvent event;
    private Boolean isEnabled;
    private String mainclass;
    private URLClassLoader urlcl;

    public PrivateCoreModuleProcessor(PrivateMessageReceivedEvent event){
        this.event = event;

        CoreModuleLoader coreModuleLoader = new CoreModuleLoader(true);
        this.mainclass = coreModuleLoader.getModuleClass();
        this.urlcl = coreModuleLoader.getUrlcl();
        this.isEnabled = coreModuleLoader.isIsenabled();

    }

    public boolean handle(){
        boolean handled = false;
        if(isEnabled){
            try{
                Class<?> classToLoad = Class.forName(mainclass, true, urlcl);
                // execute module
                Method method_exec = classToLoad.getDeclaredMethod("private_execute", PrivateMessageReceivedEvent.class); // MessageReceivedEvent event, int currentpermission
                Object instance_exec = classToLoad.getConstructor().newInstance();
                Object result_exec = method_exec.invoke(instance_exec, event);

                if(result_exec != null){
                    handled = (boolean) result_exec;
                }

            }catch (Exception e){
                new Log().addEntry("PCMP", "An error occurred while handling private core module: "+e.toString(), 4);
                e.printStackTrace();
            }
        }
        return handled;
    }
}
