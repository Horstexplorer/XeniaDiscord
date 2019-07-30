package de.netbeacon.xeniadiscord.modulemanagement;

import de.netbeacon.xeniadiscord.util.ErrorLog;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Pattern;

public class PrivateModuleProcessor {

    private PrivateMessageReceivedEvent event;
    private static boolean modex = false;
    private List<String> modules = new ArrayList<String>();
    //
    private static URLClassLoader urlcl;
    private static List<String> classname = new ArrayList<String>();

    public PrivateModuleProcessor(PrivateMessageReceivedEvent event){
        this.event = event;

        // check if urlcl is not null
        if(urlcl == null){
            //get files
            //Check if dir exists
            File directory = new File("./modules/");
            if (!directory.exists()) {
                directory.mkdir();
            }
            //Check if module exist, if load into array
            File[] listOfFiles = directory.listFiles();
            if(listOfFiles != null && listOfFiles.length > 0){
                modex = true;
                int x = 0;
                for (File f: listOfFiles){
                    if(f.getName().endsWith(".jar")){  // just find .jar files
                        modules.add(f.getName());
                    }
                }
            }
            // create array of urls
            if(modex){
                List<URL> urllist = new ArrayList<URL>();
                for(String module : modules){
                    try{
                        //Get main class from file
                        JarFile jfile = new JarFile("./modules/"+module);
                        Manifest mf = jfile.getManifest();
                        Attributes atr = mf.getMainAttributes();
                        String maincp = atr.getValue("Main-Class");
                        jfile.close();
                        //add to list
                        classname.add(maincp);
                        //get url
                        urllist.add(new URL("file:./modules/"+module));
                    }catch (Exception e){
                        new ErrorLog(4, "An error occurred while adding private module: "+module+" : "+e.toString());
                    }
                }
                //create urlclassloader
                URL[] urls = urllist.toArray(new URL[urllist.size()]);
                urlcl = new URLClassLoader(urls, this.getClass().getClassLoader());
            }
        }
    }

    public boolean handle(){
        boolean handled = false;
        if(modex){
            try{
                for(String name : classname){
                    if(handled){
                        break;
                    }
                    Class<?> classToLoad = Class.forName(name, true, urlcl);
                    // execute module
                    Method method_exec = classToLoad.getDeclaredMethod("private_execute", PrivateMessageReceivedEvent.class); // MessageReceivedEvent event
                    Object instance_exec = classToLoad.getConstructor().newInstance();
                    Object result_exec = method_exec.invoke(instance_exec, event);

                    if(result_exec != null){
                        handled = (boolean) result_exec;
                    }
                }
            }catch(Exception e){
                new ErrorLog(4, "An error occurred while handling private modules: "+e.toString());
                e.printStackTrace();
            }
        }
        return handled;
    }
}
