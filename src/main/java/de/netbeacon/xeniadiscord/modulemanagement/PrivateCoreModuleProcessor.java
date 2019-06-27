package de.netbeacon.xeniadiscord.modulemanagement;

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
    private static Boolean active = false;
    //
    private static String mainclass;
    private static URLClassLoader urlcl;

    public PrivateCoreModuleProcessor(PrivateMessageReceivedEvent event){
        this.event = event;

        if(urlcl == null){
            //Check if dir exists
            File dir = new File("./coremodule/");
            if(!dir.exists()){
                dir.mkdirs();
            }
            //Check if cmod exists
            File cmod = new File("./coremodule/coremodule.jar");
            if (cmod.exists()) {
                active = true;
            }
            // create urlcl
            if(active){
                try{
                    //Get main class from file
                    JarFile jfile = new JarFile("./coremodule/coremodule.jar");
                    Manifest mf = jfile.getManifest();
                    Attributes atr = mf.getMainAttributes();
                    mainclass = atr.getValue("Main-Class");
                    jfile.close();

                    urlcl = new URLClassLoader(new URL[]{new URL("file:./coremodule/coremodule.jar")}, this.getClass().getClassLoader());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean handle(){
        boolean handled = false;
        if(active){
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
                e.printStackTrace();
            }
        }
        return handled;
    }
}
