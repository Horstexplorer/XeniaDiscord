package de.netbeacon.xeniadiscord.modulemanagement;

import de.netbeacon.xeniadiscord.util.ErrorLog;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class GuildModuleProcessor {

    private GuildMessageReceivedEvent event;
    private static boolean modex = false;
    private static List<String> modules = new ArrayList<String>();
    //
    private static URLClassLoader urlcl;
    private static List<String> classname = new ArrayList<String>();

    public GuildModuleProcessor(GuildMessageReceivedEvent event){
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
                        new ErrorLog(4, "An error occurred while adding guild module: "+module+" : "+e.toString());
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
                    // execute permission() -> bool
                    Method method_permission = classToLoad.getDeclaredMethod("permission", Member.class); // Member to module
                    Object instance_permission = classToLoad.getConstructor().newInstance();
                    Object result_permission = method_permission.invoke(instance_permission, event.getMember());
                    // check if permission() -> true
                    if((Boolean) result_permission){
                        // execute module
                        Method method_exec = classToLoad.getDeclaredMethod("guild_execute", GuildMessageReceivedEvent.class, Member.class); // GuildMessageReceivedEvent event
                        Object instance_exec = classToLoad.getConstructor().newInstance();
                        Object result_exec = method_exec.invoke(instance_exec, event, event.getMember());

                        if(result_exec != null){
                            handled = (boolean) result_exec;
                        }
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
                new ErrorLog(4, "An error occurred while handling guild modules: "+e.toString());
            }
        }
        return handled;
    }

    public String listmodules(){    // not used
        //list modules
        return String.join(", ", modules);
    }
}
