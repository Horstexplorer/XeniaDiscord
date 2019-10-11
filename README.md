# XeniaDiscord
#### Chat- and Music-Bot for Discord
> Current Version: 1.1.4.1

> Using  
> - net.dv8tion JDA -  4.0.0_51
> - lavaplayer - 1.3.22
> - slf4j-simple - 1.7.26


### Table of Contents
- Configuration
- Commands
    - Discord
    - Local
- Permissions
- Modules
- ToDo
- Changelog


### Configuration
All basic settings are stored in the file sys.config. This file will be created automatically at first start. You then need to add your Discord bot token and your discord id. 
A new application must be created for the bot token (bot_token): https://discordapp.com/developers/applications/ The token can then be found under the page "bot".
You can get your Discord user id (bot_admin_id) by right-clicking your username on your discord guild and select "copy id".
Finally (activated) has to be set to true, otherwise the bot won't start.
```
activated=false                             // to disable everything, makes sure the configuration file has been noticed
bot_token=<token>                           // bot token
bot_command_indicator=x!                    // indicator for commands
bot_activate_modules=false                  // use modules
bot_activate_coremodule=false               // use priority module
bot_status=<status>                         // activity to display
bot_admin_id=<id>                           // userid for bot-admin features
bot_sayhellotonew=true                      // bot welcomes every user who joins guild (private chat)
bot_gui_activate=false                      // activate gui
bot_gui_exitonclose=true                    // quit application when the gui is closed
```
The application should now be restarted. At this point the bot will stop again with a note that no client token or client secret has been set. The now existing twitch.config should be modified now. It should look something like this:
Now you have to specify a Twitch application which can be created here: https://dev.twitch.tv/console/apps
```
twitch_client_id=                           // your application client id
twitch_client_secret=                       // your application secret
twitch_bearer_token=                        // created bearer token
twitch_bearer_token_validuntil=             // bearer token expiration
twitch_worker_max=                          // number of additional TwitchWorkers
```
Analytics tool to be selected as category. You need to copy the Client-ID (twitch_client_id) and create a new client-secret (twitch_client_secret).
The application should now be restarted and be ready for use. You can add it to your Discord guild by using an o2auth authorization link with your bot id. (You can get the bot id from the url of your Discord application)
https://discordapp.com/developers/applications/YOURBOTID/ => to => https://discordapp.com/oauth2/authorize?client_id=YOURBOTID&scope=bot


### Commands
Commands are divided into two groups: These which are sent in Discord and those which are sent locally via gui or terminal.

#### Discord
Commands start with an indicator configurable via config as 'bot_command_indicator' (default: x!).  
This list contains only the commands included by default and their required permissions (all require Permission.MESSAGE_WRITE obviously).
```
Command                                                   || Permission                   ||Type     || Description

help                                                      || -                            || Global  || Kicks the user from the server
info                                                      || -                            || Global  || Shows some information
commands                                                  || -                            || Guild   || Displays all commands
kick <user>                                               || membermanagement_kick        || Guild   || Kicks the user from the server
ban <user>                                                || membermanagement_ban         || Guild   || Bans the user from the server
music <command>                                           || See 'Music Commands          || Guild   || See 'Music Commands
ghost <channel> <msg>                                     || ghost                        || Guild   || Send <msg> as bot to <channel>
blacklist <add/remove> <channel>                          || blacklist_manage             || Guild   || Add <channel> to blacklist so that Xenia neither listen nor respond there
twitchhook <list|add/remove> <username> <boolean> [msg]   || twitchhooks_manage           || Guild   || Add a webhook for a specific twitch channel to your textchannel; <boolean> true or false - use @everyone; If [msg] is set it is used as alternative notification (supports placeholders, see changelog 1.1.1.0)
extperm <add/remove/list> <permission1> <permission2> ... || permission_manage            || Guild   || Manage permissions of a given role

```
Commands to control music functions:
```
play <url>                                  || music_play         || Add the song to the queue
stop                                        || music_stop         || Stops the playback and deletes the queue
list                                        || music_play         || Display songs in queue
queue                                       || music_play         || Same as list
next                                        || music_manage_queue || Play next song in queue
skip                                        || music_manage_queue || Same as next
shuffle                                     || music_manage_queue || Shuffle queue
info                                        || music_play         || Displaying information about the current song
off                                         || music_manage_off   || Disconnect from voice channel
```
Commands limited to admin user (bot_admin_id) (type: private)
```
admin status                                || advanced status
admin shutdown                              || shutdown bot
admin onlinestatus <idle/on>                || set onlinestatus to dnd, idle or online
admin blacklistforcesave                    || force saving the blacklist
admin twitchhookforcesave                   || force saving the twitchhooks
admin updateconfig <prop> <val>             || update config property to value
admin log <listerrors/export/reset>         || list last 10 errors; export the log; reset log
```

#### Local
Commands start without an indicator
```
broadcast <msg>                             || tries to send msg to all guilds
log <>                                   
       list <errorlevel>                    || list all entrys equal or above <errorlevel>
       export                               || export errors to file
       reset                                || reset errors
guild  <>                                   
       list                                 || list all connected guilds
       leave <name/id>                      || leave specific guild
help                                        || show available commands
info                                        || display information about this bot
shutdown                                    || save all files and exit
status <>                                   
       dnd                                  || set online status to do_not_disturb
       idle                                 || set online status to idle
       online                               || set online status to online
twitch <>                                    
       listhooks                            || Lists all twitchhooks
       listgames                            || Lists all cached games
config <>
       update <property> <value>            || update <property> to <value> in sys.config
```


### Permissions
admin                           || all permissions
permission_manage               || can manage permissions - is overwritten by Discord administrator and manage_permissions
music_all                       || all music permissions
music_play                      || can play music, add to queue, etc
music_stop                      || can stop the music
music_manage_queue              || can manage the queue, skip tracks, etc
music_manage_off                || can disconnect the bot from voice
membermanagement_all            || all membermanagement permission
membermanagement_kick           || can kick member
membermanagement_ban            || can ban member
ghost                           || can talk as the bot
blacklist_manage                || can manage blacklisted channel
twitchhooks_manage              || can create & edit twitch hooks


### Modules
The functionality of the bot can be extended with modules. These can react to any interactions as long as they do not start with "bot_command_indicator"  
Modules can be used in two ways: as normal module or as core module. Both are built in the same way, but the core module is executed before all other modules.

##### Create your own module:
Its very simple.  
You need to create a class containing five functions as shown in the example below.
onLoad() gets called once when the module is loaded for the first time and onUnload() when the bot is shut down (or restarted)
permission() gets called to check whether a particular member has the appropriate permissions to use the module. Note that it only gets called if the request isn't from a private chat.
If the permission is sufficient the guild_execute() function is called with the current GuildMessageReceivedEvent and the Member. If the message was sent via private chat private_execute() gets called instead with a given PrivateMessageReceivedEvent. If the module has triggered an action guild/private_execute should return true.
The module should then be packed as jar where via the manifest file Main-Class points to the class with those functions.  
The jar file of the module can be named as you like and should be stored in ./modules/. If a module should be used as core module instead, it has to be named coremodule.jar in ./coremodule/ .

The main class of a module could look something like this:
```
public class YourModule {

    public boolean onEnable(){
        return // true if successfull
    }
    public boolean onDisable(){
        return // true if successfull
    }

    public boolean permission(Member member){
        return member.hasPermission(Permission);    // return true if has permission
    }
    public boolean guild_execute(GuildMessageReceivedEvent event, Member member){
        // process the event
        return <boolean>;                                                         // return if the module has handled the request. (it doesnt need to try other modules)
    }
    public boolean private_execute(PrivateMessageReceivedEvent event){
        // process the event
        return <boolean>;                                                         // return if the module has handled the request. (it doesnt need to try other modules)
    }
}
```


### ToDo
Scheduled tasks (sorted by priority) (Target: none)
- [ ] multi thread optimization for input to twitchwrap


### Changelog
##### 1.1.4.1
```
- updated JDA
```
##### 1.1.4.0
```
- introduction of a secondary permission system
- some improvements
```
##### 1.1.3.0
```
- TwitchWrap requests can now be processed parallel with multiple TwitchWorker instances
- various improvements
- fixed escape sequences in TwitchHooks
```
##### 1.1.2.1
```
- fixed TwitchWrap
- fixed TwitchHooks
- fixed LavaPlayer (added newer version)
- some improvements
```
##### 1.1.2.0
```
- fixed property @ coremoduleloader
- some improvements
- added another local command
```
##### 1.1.1.0
```
- added customizable notifications for TwitchHooks
    placeholders:
        %uname% - Channel name starting with a capital letter
        %lname% - Channel name in all lower case letters
        %game%  - Name of the game which is currently streamed
        %title% - Stream title
- fixed module loaders
- minor improvements
```
##### 1.1.0.1
```
- minor fix (core module reenable)
- minor improvement
```
##### 1.1.0.0
```
- rework of the module loaders
```
##### 1.0.7.1
```
- minor improvements
```
##### 1.0.7.0
```
- improved event logging (replacing errorlog)
- updated commands
```
##### 1.0.6.3
```
- updating the twitchgamecache every 24h
- exporting the error log won't overwrite an existing error log anymore
- error logs moved to ./logs/
- general improvements
- switched to JDA 4.0.0_46
```
##### 1.0.6.2
```
- added more local commands
- minor improvements
```
##### 1.0.6.1
```
- added twitchgamecache
- twitchhook now displays game title
```
##### 1.0.6.0
```
- added optional gui
- twitchworker will be restarted if it stopped working
- added local commands
- minor changes
```
##### 1.0.5.2
```
- minor improvements
- updated readme
```
##### 1.0.5.1
```
- an invalid bearer token is replaced before the next query is sent
    - no request should be lost anymore
- application is terminated to avoid further errors if the token cannot be created
- minor improvements
```
##### 1.0.5.0
```
- Twitch api integration improved
    - bearer token is used instead of a client id -> higher query limits
    - automatic exchange of the token when needed
    - added task queue
    - several other improvements
- application adapted to use the new integration
```
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
