package de.netbeacon.xeniadiscord.modulemanagement;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class GuildModuleProcessor {

    private GuildMessageReceivedEvent event;
    private String[] modules;
    private boolean modex = false;

    public GuildModuleProcessor(GuildMessageReceivedEvent event){
        this.event = event;
        //Check if dir exists
        File directory = new File("./modules/");
        if (!directory.exists()) {
            directory.mkdir();
        }
        //Check if module exist, if load into array
        File[] listOfFiles = directory.listFiles();
        if(listOfFiles != null && listOfFiles.length > 0){
            int x = 0;
            for (File f: listOfFiles){
                if(f.getName().matches(".*jar$")){  // just find .jar files
                    x++;
                }
            }
            if(x != 0){
                modex = true;
                int y = 0;
                modules = new String[x];
                for (File f: listOfFiles){
                    if(f.getName().matches(".*jar$")){
                        modules[y] = f.getName();
                        y++;
                    }
                }
            }
        }
    }

    public boolean handle(){
        boolean handled = false;
        if(modex){
            try{
                int x = 0;
                while(x < modules.length && !handled){

                    //Get main class from file
                    JarFile jfile = new JarFile("./modules/"+modules[x]);
                    Manifest mf = jfile.getManifest();
                    Attributes atr = mf.getMainAttributes();
                    String maincp = atr.getValue("Main-Class");

                    URL[] clu = new URL[]{new URL("file:./modules/"+modules[x])};
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
                    //Increase for next turn
                    x++;
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return handled;
    }

    public String listmodules(){
        //list modules
        return Arrays.toString(modules);
    }
}
