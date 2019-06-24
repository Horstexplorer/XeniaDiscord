package de.netbeacon.xeniadiscord.modulemanagement;

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
    private Boolean active = false;

    public GuildCoreModuleProcessor(GuildMessageReceivedEvent event){
        this.event = event;
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
    }

    public boolean handle(){
        boolean handled = false;
        if(active){
            try{
                //Get main class from file
                JarFile jfile = new JarFile("./coremodule/coremodule.jar");
                Manifest mf = jfile.getManifest();
                Attributes atr = mf.getMainAttributes();
                String maincp = atr.getValue("Main-Class");

                URL[] clu = new URL[]{new URL("file:./coremodule/coremodule.jar")};
                URLClassLoader child = new URLClassLoader(clu, this.getClass().getClassLoader());
                Class<?> classToLoad = Class.forName(maincp, true, child);

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

                //close
                jfile.close();
                child.close();

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return handled;
    }

    public void startbackgroundtask(JDA jda){
        if(active) {
            try {
                //Get main class from file
                JarFile jfile = new JarFile("./coremodule/coremodule.jar");
                Manifest mf = jfile.getManifest();
                Attributes atr = mf.getMainAttributes();
                String maincp = atr.getValue("Main-Class");

                //Do magic
                URL[] clu = new URL[]{new URL("file:./coremodule/coremodule.jar")};
                URLClassLoader child = new URLClassLoader(clu, this.getClass().getClassLoader());
                Class<?> classToLoad = Class.forName(maincp, true, child);
                Method method = classToLoad.getDeclaredMethod("onstart", JDA.class);
                Object instance = classToLoad.getConstructor().newInstance();
                method.invoke(instance, jda);   //ignore result

                //close
                jfile.close();
                child.close();
            } catch (Exception e) {
                System.out.println("[ERROR] " + e);
            }
        }
    }
}
