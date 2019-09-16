package de.netbeacon.xeniadiscord.modulemanagement;

import de.netbeacon.xeniadiscord.modulemanagement.loader.ModuleLoader;
import de.netbeacon.xeniadiscord.util.log.Log;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.Map;

public class GuildModuleProcessor {

    private GuildMessageReceivedEvent event;
    private URLClassLoader urlcl;
    private Map<String, String> modules;
    private boolean isEnabled;

    public GuildModuleProcessor(GuildMessageReceivedEvent event){
        this.event = event;

        ModuleLoader moduleLoader = new ModuleLoader(true);
        urlcl = moduleLoader.getUrlcl();
        modules = moduleLoader.getModules();
        isEnabled = moduleLoader.isIsenabled();
    }

    public boolean handle(){
        boolean handled = false;
        if(isEnabled){
            for(Map.Entry<String, String> entry : modules.entrySet()) {
                try{
                    if(handled){
                        break;
                    }
                    Class<?> classToLoad = Class.forName(entry.getValue(), true, urlcl);
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
                }catch (Exception e){
                    new Log().addEntry("GMP", "An error occurred while handling module: "+entry.getKey()+" "+e.toString(), 3);
                    e.printStackTrace();
                }
            }
        }
        return handled;
    }

    public String listmodules(){    // not used
        //list modules
        String string = "";
        for(Map.Entry<String, String> entry : modules.entrySet()) {
            string += entry.getKey()+", ";
        }
        return string;
    }
}
