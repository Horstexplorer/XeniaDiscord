package de.netbeacon.xeniadiscord.modulemanagement;

import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Pattern;

public class PrivateModuleProcessor {

    private PrivateMessageReceivedEvent event;
    private String[] modules;
    private boolean modex = false;

    public PrivateModuleProcessor(PrivateMessageReceivedEvent event){
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
                if(f.getName().endsWith(".jar")){  // just find .jar files
                    x++;
                }
            }
            if(x != 0){
                modex = true;
                int y = 0;
                modules = new String[x];
                for (File f: listOfFiles){
                    if(f.getName().endsWith(".jar")){
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

                    // execute module
                    Method method_exec = classToLoad.getDeclaredMethod("private_execute", PrivateMessageReceivedEvent.class); // MessageReceivedEvent event, int currentpermission
                    Object instance_exec = classToLoad.getConstructor().newInstance();
                    Object result_exec = method_exec.invoke(instance_exec, event);

                    if(result_exec != null){
                        handled = (boolean) result_exec;
                    }

                    //Increase for next turn
                    x++;

                    //close
                    jfile.close();
                    child.close();
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
