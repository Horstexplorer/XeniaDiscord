package de.netbeacon.xeniadiscord.modulemanagement;

import de.netbeacon.xeniadiscord.util.log.Log;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class GuildCoreModuleProcessor {

    private GuildMessageReceivedEvent event;
    private static Boolean active = false;
    //
    private static String mainclass;
    private static URLClassLoader urlcl;

    public GuildCoreModuleProcessor(GuildMessageReceivedEvent event){
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
                    new Log().addEntry("GCMP","An error occurred while adding guild core module: "+e.toString(), 4);
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

                // execute permission() -> bool
                Method method_permission = classToLoad.getDeclaredMethod("permission", Member.class); // Permission lvl to module
                Object instance_permission = classToLoad.getConstructor().newInstance();
                Object result_permission = method_permission.invoke(instance_permission, event.getMember());

                // check if permission() -> true
                if((Boolean) result_permission){
                    // execute module
                    Method method_exec = classToLoad.getDeclaredMethod("guild_execute", GuildMessageReceivedEvent.class, Member.class); // MessageReceivedEvent event, int currentpermission
                    Object instance_exec = classToLoad.getConstructor().newInstance();
                    Object result_exec = method_exec.invoke(instance_exec, event, event.getMember());

                    if(result_exec != null){
                        handled = (boolean) result_exec;
                    }
                }
            }catch (Exception e){
                new Log().addEntry("GCMP","An error occurred while handling guild core module: "+e.toString(), 4);
                e.printStackTrace();
            }
        }
        return handled;
    }

    public void startbackgroundtask(JDA jda){
        if(active) {
            new Log().addEntry("GCMP","Starting the background task for core module", 0);
            try {
                //Do magic
                Class<?> classToLoad = Class.forName(mainclass, true, urlcl);
                Method method = classToLoad.getDeclaredMethod("onstart", JDA.class);
                Object instance = classToLoad.getConstructor().newInstance();
                method.invoke(instance, jda);   //ignore result

            } catch (Exception e) {
                new Log().addEntry("GCMP","An error occurred while starting the background task for core module: "+e.toString(), 4);
                e.printStackTrace();
            }
        }
    }
}
