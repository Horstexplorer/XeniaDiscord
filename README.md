# XeniaDiscord
#### Chat- and Music-Bot for Discord
> Current Version: 1.0.4.4

> Using  
> - net.dv8tion JDA -  4.BETA.0_24
> - lavaplayer - 1.3.17
> - slf4j-simple - 1.7.26

### Configuration
All settings are stored in the file sys.config. This file will be created automatically at first start.
```
activated=false                             // to disable everything, makes sure the configuration file has been noticed
bot_token=<token>                           // bot token
bot_command_indicator=x!                    // indicator for commands
bot_activate_modules=false                  // use modules
bot_activate_coremodule=false               // use priority module
bot_activate_coremodule_onstart=false       // execute onstartup() in coremodule once after starting the bot
bot_status=<status>                         // activity to display
bot_admin_id=<id>                           // userid for bot-admin features
bot_sayhellotonew=true                      // bot welcomes every user who joins guild (private chat)
```

### Commands
Commands start with an indicator configurable via config as 'bot_command_indicator' (default: x!).  
This list contains only the commands included by default and their required permissions.
```
Command                                     || Required permission          || Description

kick <user>                                 || Permission.KICK_MEMBERS      || Kicks the user from the server
ban <user>                                  || Permission.BAN_MEMBERS       || Bans the user from the server
music <command>                             || Permission.VOICE_CONNECT     || See 'Music Commands
ghost <channel> <msg>                       || Permission.MANAGE_CHANNEL    || Send <msg> as bot to <channel>
blacklist <add/remove> <channel>            || Permission.MANAGE_CHANNEL    || Add <channel> to blacklist so that Xenia neither listen nor respond there
twitchhook <add/remove/list> <username>     || Permission.MANAGE_CHANNEL    || Add a webhook for a specific twitch channel to your textchannel
                    
```
Commands to control music functions:
```
play <url>                                  || Add the song to the queue
stop                                        || Stops the playback and deletes the queue
list                                        || Display songs in queue
queue                                       || Same as list
next                                        || Play next song in queue
skip                                        || Same as next
shuffle                                     || Shuffle queue
info                                        || Displaying information about the current song
off                                         || Disconnect from voice channel
```
Commands limited to admin user (bot_admin_id)
```
admin status                                || advanced status
admin shutdown                              || shutdown bot
admin onlinestatus <idle/on>                || set onlinestatus to dnd, idle or online
admin blacklistforcesave                    || force saving the blacklist
admin twitchhookforcesave                   || force saving the twitchhooks
admin updateconfig <prop> <val>             || update config property to value
admin errorlog <list/export/reset>          || list last 10 errors; export all of them; reset cached errors
```

### Modules
The functionality of the bot can be extended with modules. These can react to any interactions as long as they do not start with "bot_command_indicator"  
There are two types of modules that can be used: core modules and default modules. Both are built the same way, but core modules have another function that can be executed after bots are started.


##### Create your own module:
Its very simple.  
You need to create a class containing three functions: permission(Member), guild_execute(GuildMessageReceivedEvent, Member) and private_execute(PrivateMessageReceivedEvent). The permission() function should return a boolean if the given permission (member.haspermission(x)) is enough to use the module. Note that it only gets called if the request isn't from a private chat.
If the permission is sufficient the guild_execute() function is called with the current GuildMessageReceivedEvent and the Member. If the message was sent via private chat private_execute() gets called instead of permission() and guild_execute() with a given PrivateMessageReceivedEvent. If the module has triggered an action guild/private_execute should return true.
The module should then be packed as jar where via the manifest file Main-Class points to the class with those functions.  
The jar file of the module can be named as you like and should be stored in ./modules/.  
The main class could look something like this:
```
public class YourModule {

    public boolean permission(Member member){
        return member.hasPermission(Permission);    // return true if has permission
    }
    public boolean guild_execute(GuildMessageReceivedEvent event, Member member){
        // process the event
        return <boolean>;                                                         // response to the bot if the module has handled the request. (it doesnt need to try other modules)
    }
    public boolean private_execute(PrivateMessageReceivedEvent event){
        // process the event
        return <boolean>;                                                         // response to the bot if the module has handled the request. (it doesnt need to try other modules)
    }
}
```


##### Create your own core-module:
This works the same way as with the normal modules but with the additional function onstart(JDA).  
This can be used to start a thread for background tasks after starting the bot. The return of onstart() is ignored by the bot, so it should be set to void.
The guild/private_execute functions may, but does not have to return true when the event has been processed. This can be used for example so that other modules may be executed afterwards.
Make sure the file is named coremodule.jar and is placed in./coremodule/ to work. In contrast to normal modules, there can only be one module installed.
The main class could look something like this:
```
public class YourCoreModule {

   public boolean permission(Member member){
        return member.hasPermission(Permission);    // return true if has permission
   }
   public boolean guild_execute(GuildMessageReceivedEvent event, Member member){
        // process the event   
        return <boolean>;                                                         // response to the bot if the module has handled the request. (it doesnt need to try other modules)
   }
   public boolean private_execute(PrivateMessageReceivedEvent event){
        // process the event
        return <boolean>;                                                         // response to the bot if the module has handled the request. (it doesnt need to try other modules)
   }
       
   public void onstart(JDA jda){
       new Thread(new Runnable() {
                   @Override
                   public void run() {
                       // process
                   }
       }).start();  
   } 
}
```

### Changelog
##### 1.0.4.5
```
- Twitchhooks are now only saved when updating them is finished
```
##### 1.0.4.4
```
- fixed minor bug in TwitchHooks
- moved TwitchApi call counter to TwitchAPIWrap class
- limited errorlog list command to only 10 entrys (2000 char limit)
- minor changes
```
##### 1.0.4.3
```
- fixed TwitchHook update schedule (every 5 minutes)
- wont send notifications after restarting the bot when a channel is live
- first update cycle now after 30 seconds instead of 5 minutes
```
##### 1.0.4.2
```
- fixed some spelling mistakes
- optimized handling of the twitch-api limit
```
##### 1.0.4.1
```
- minor improvements
```
##### 1.0.4.0
```
- improved messages
- added error log
- fixed PrivateModuleProcessor
- some other bugs fixed
```
##### 1.0.3.2
```
- improved notifications for twitch webhooks
- twitch api limitations should now be considered
```
##### 1.0.3.1
```
- online status changes behaviour of the bot
    | online - everything should work
    | idle   - ignore guild chats
    | dnd    - ignore everything

```
##### 1.0.3.0
```
- Added webhooks for twitch
- switched to JDA 4.BETA.0_24
- various minor changes
- improved hints for faulty commands
```
##### 1.0.2.5
```
- switched to JDA 4.BETA.0_10
- minor changes
- fixed CoreModuleProcessors
```
##### 1.0.2.4
```
- switched to 4.ALPHA.0_108
- minor improvements
- improved module processors
```
##### 1.0.2.3
```
- bot should only process the message from guild chat if it has the permission to respond
    (modules must check this individually)
- updated dependencies
- added another activity
```
##### 1.0.2.2
```
- minor improvements
- fixed admin command
- added broadcast command to admin
```
##### 1.0.2.1
```
- fixed a bug where creating the configuration file did not work
```
##### 1.0.2.0
```
- switched to 4.ALPHA.0_82 
- fixed several bugs
- admin commands now start with admin
- sys properties can now be updated via admin command
- config adds missing entries
- added more welcome messages
```
##### 1.0.1.4
```
- bot may now greet every user who joins a guild
```
##### 1.0.1.3
```
- minor changes
```
##### 1.0.1.2
```
- fixed GuildCoreModuleProcessor
```
##### 1.0.1.1
```
- config query process optimized
- minor improvements
```
##### 1.0.1.0
```
- added blacklist feature
- minor improvements
```
##### 1.0.0.0
```
- no changes
```