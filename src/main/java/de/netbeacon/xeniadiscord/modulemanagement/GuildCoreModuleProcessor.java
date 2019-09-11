package de.netbeacon.xeniadiscord.modulemanagement;

import de.netbeacon.xeniadiscord.modulemanagement.loader.CoreModuleLoader;
import de.netbeacon.xeniadiscord.util.log.Log;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.lang.reflect.Method;
import java.net.URLClassLoader;

public class GuildCoreModuleProcessor {

    private GuildMessageReceivedEvent event;
    private Boolean isEnabled;
    private String mainclass;
    private URLClassLoader urlcl;

    public GuildCoreModuleProcessor(GuildMessageReceivedEvent event){
        this.event = event;

        CoreModuleLoader coreModuleLoader = new CoreModuleLoader(true);
        this.mainclass = coreModuleLoader.getModuleClass();
        this.urlcl = coreModuleLoader.getUrlcl();
        this.isEnabled = coreModuleLoader.isIsenabled();
    }

    public boolean handle(){
        boolean handled = false;
        if(isEnabled){
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
}
