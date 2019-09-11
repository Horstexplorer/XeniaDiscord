package de.netbeacon.xeniadiscord.modulemanagement.loader;

import de.netbeacon.xeniadiscord.util.log.Log;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class ModuleLoader {

    private static URLClassLoader urlcl;
    private static Map<String, String> modules = new HashMap<String, String>(); // module - class
    private static boolean hasmodules = false;
    private static boolean isenabled = false;

    public ModuleLoader(boolean active){
        if(active){
            if(urlcl == null){
                System.out.println("[INFO] Init ModuleLoader (default)");
                new Log().addEntry("MLd", "Init ModuleLoader (default)", 0);
                if(getFiles()){
                    hasmodules = true;
                    buildcl();
                }
            }
            if(!isenabled){
                isenabled = enable();
            }
        }
    }

    private boolean getFiles(){
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
                    modules.put(f.getName(), "");
                }
            }
        }
        if(modules.size() > 0){
            return true;
        }
        return false;
    }
    private void buildcl(){
        List<URL> urllist = new ArrayList<URL>();

        for(Map.Entry<String, String> entry : modules.entrySet()) {
            try{
                //Get main class from file
                JarFile jfile = new JarFile("./modules/"+entry.getKey());
                Manifest mf = jfile.getManifest();
                Attributes atr = mf.getMainAttributes();
                String maincp = atr.getValue("Main-Class");
                jfile.close();
                //add to list
                modules.put(entry.getKey(), maincp);
                //get url
                urllist.add(new URL("file:./modules/"+entry.getKey()));
            }catch (Exception e){
                new Log().addEntry("MLd","An error occurred while adding module: "+entry.getKey()+" : "+e.toString(), 4);
                e.printStackTrace();
            }
        }
        //create urlclassloader
        URL[] urls = urllist.toArray(new URL[urllist.size()]);
        urlcl = new URLClassLoader(urls, this.getClass().getClassLoader());
    }

    public boolean enable(){
        // enable all modules (call onEnable)
        if(hasmodules){
            System.out.println("[INFO][MLd] Enabling modules");
            new Log().addEntry("MLd", "Enabling modules", 0);

            List<String> toDisable = new ArrayList<>();
            for(Map.Entry<String, String> entry : modules.entrySet()) {
                try{
                    Class<?> classToLoad = Class.forName(entry.getValue(), true, urlcl);
                    // execute onEnable function
                    Method method_onenable = classToLoad.getDeclaredMethod("onEnable");
                    Object instance_onenable = classToLoad.getConstructor().newInstance();
                    Object result_onenable = method_onenable.invoke(instance_onenable); // onEnable should return a boolean
                    // check result
                    if(!(Boolean) result_onenable){
                        System.out.println("[ERROR][MLd]"+"Module "+entry.getKey()+" could not be enabled successfully.");
                        new Log().addEntry("MLd", "Module "+entry.getKey()+" could not be enabled successfully.", 3);
                        // something went wrong, we "disable" this module by removing it from the list
                        toDisable.add(entry.getKey());
                    }
                }catch (Exception e){
                    System.out.println("[ERROR][MLd]"+"An error occurred while enabling module: "+entry.getKey()+": "+e.toString());
                    new Log().addEntry("MLd", "An error occurred while enabling module: "+entry.getKey()+": "+e.toString(), 4);
                    // something went wrong, we "disable" this module by removing it from the list
                    toDisable.add(entry.getKey());
                }
            }
            // remove modules which could not be loaded
            for(String module : toDisable){
                modules.remove(module);
            }
            // we dont need to be active if there are no modules left
            if(modules.isEmpty()){
                hasmodules = false;
                return false;
            }
            return true;
        }
        return false;
    }
    public void disable(){
        // disable all modules (call onDisable)
        if(isenabled){
            System.out.println("[INFO][MLd] Disabling modules");
            new Log().addEntry("MLd", "Disabling modules", 0);

            for(Map.Entry<String, String> entry : modules.entrySet()) {
                try{
                    Class<?> classToLoad = Class.forName(entry.getValue(), true, urlcl);
                    // execute onEnable function
                    Method method_ondisable = classToLoad.getDeclaredMethod("onDisable");
                    Object instance_ondisable = classToLoad.getConstructor().newInstance();
                    Object result_ondisable = method_ondisable.invoke(instance_ondisable); // onDisable should return a boolean
                    // check result
                    if(!(Boolean) result_ondisable){
                        new Log().addEntry("MLd", "Module "+entry.getKey()+" could not be disabled successfully.", 3);
                    }
                }catch (Exception e){
                    new Log().addEntry("MLd", "An error occurred while disabling module: "+entry.getValue()+": "+e.toString(), 4);
                    e.printStackTrace();
                }
            }
            // set isenabled to false
            isenabled = false;
        }
    }

    public URLClassLoader getUrlcl(){ return urlcl; }
    public Map<String, String> getModules(){ return modules; }
    public boolean isIsenabled(){ return isenabled; }
}
