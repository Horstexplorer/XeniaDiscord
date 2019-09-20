package de.netbeacon.xeniadiscord.modulemanagement.loader;

import de.netbeacon.xeniadiscord.util.Config;
import de.netbeacon.xeniadiscord.util.log.Log;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class CoreModuleLoader {

    private static URLClassLoader urlcl;
    private static String classname; // module - class
    private static Boolean hasvalidmodule = null; // module exists, which is valid (module valid until failing enable()) -> true | invalid or no module -> false | not set -> null
    private static boolean isenabled = false;

    public CoreModuleLoader(boolean active){
        if(active){
            if(urlcl == null && hasvalidmodule == null){
                System.out.println("[INFO] Init ModuleLoader (core)");
                new Log().addEntry("MLc", "Init ModuleLoader (core)", 0);
                if((hasvalidmodule = getFile())){
                    buildcl();
                }else{
                    // deactivate if no file found
                    new Config().updateproperties("bot_activate_coremodule","false");
                }
            }
            if(!isenabled && hasvalidmodule){
                isenabled = enable();
            }
        }
    }

    private boolean getFile(){
        //Check if dir exists
        File dir = new File("./coremodule/");
        if(!dir.exists()){
            dir.mkdirs();
        }
        //Check if cmod exists
        File cmod = new File("./coremodule/coremodule.jar");
        if (cmod.exists()) {
            return true;
        }
        return false;
    }
    private void buildcl(){
        try{
            //Get main class from file
            JarFile jfile = new JarFile("./coremodule/coremodule.jar");
            Manifest mf = jfile.getManifest();
            Attributes atr = mf.getMainAttributes();
            classname = atr.getValue("Main-Class");
            jfile.close();

            urlcl = new URLClassLoader(new URL[]{new URL("file:./coremodule/coremodule.jar")}, this.getClass().getClassLoader());
        }catch (Exception e){
            new Log().addEntry("MLc","An error occurred while adding coremodule: "+e.toString(), 4);
            e.printStackTrace();
        }
    }

    public boolean enable(){
        // enable module (call onEnable)
        if(hasvalidmodule){
            System.out.println("[INFO][MLc] Enabling CoreModule");
            new Log().addEntry("MLc", "Enabling CoreModule", 0);

            try{
                Class<?> classToLoad = Class.forName(classname, true, urlcl);
                // check if methods exist [onEnable(void), onDisable(void), permission(Member), guild_execute(GuildMessageReceivedEvent,Member), private_execute(PrivateMessageReceivedEvent)]
                // should throw an error when not found
                classToLoad.getDeclaredMethod("onEnable");
                classToLoad.getDeclaredMethod("onDisable");
                classToLoad.getDeclaredMethod("permission", Member.class);
                classToLoad.getDeclaredMethod("guild_execute", GuildMessageReceivedEvent.class, Member.class);
                classToLoad.getDeclaredMethod("private_execute", PrivateMessageReceivedEvent.class);
                // execute onEnable function
                Method method_onenable = classToLoad.getDeclaredMethod("onEnable");
                Object instance_onenable = classToLoad.getConstructor().newInstance();
                Object result_onenable = method_onenable.invoke(instance_onenable); // onEnable should return a boolean
                // check result
                isenabled = (Boolean) result_onenable;
                if(!isenabled){
                    hasvalidmodule = false; // we dont need to be active if the module aint workin
                    System.out.println("[ERROR][MLc]"+"CoreModule could not be enabled successfully.");
                    new Log().addEntry("MLc", "CoreModule could not be enabled successfully.", 3);
                    return false;
                }
            }catch (Exception e){
                hasvalidmodule = false; // we dont need to be active if the module aint workin
                System.out.println("[ERROR][MLc]"+"An error occurred while enabling CoreModule: "+e.toString());
                new Log().addEntry("MLc", "An error occurred while enabling CoreModule: "+e.toString(), 4);
                return false;
            }
            return true;
        }
        return false;
    }
    public void disable(){
        System.out.println("[INFO][MLc] Disabling module");
        new Log().addEntry("MLc", "Disabling module", 0);
        // disable all modules (call onDisable)
        if(isenabled){
            try{
                Class<?> classToLoad = Class.forName(classname, true, urlcl);
                // execute onEnable function
                Method method_ondisable = classToLoad.getDeclaredMethod("onDisable");
                Object instance_ondisable = classToLoad.getConstructor().newInstance();
                Object result_ondisable = method_ondisable.invoke(instance_ondisable); // onDisable should return a boolean
                // check result
                if(!(Boolean) result_ondisable){
                    new Log().addEntry("MLc", "Module could not be disabled successfully.", 3);
                }
            }catch (Exception e){
                new Log().addEntry("MLc", "An error occurred while disabling module: "+e.toString(), 4);
                e.printStackTrace();
            }
            // set isenabled to false
            isenabled = false;
        }
    }

    public URLClassLoader getUrlcl(){ return urlcl; }
    public String getModuleClass(){ return classname; }
    public boolean isIsenabled(){ return isenabled; }

}
