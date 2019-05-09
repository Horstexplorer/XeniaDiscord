package de.netbeacon.xeniadiscord.handler;

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;

import java.util.Random;

public class GuildMemberJoinHandler implements Runnable{

    private GuildMemberJoinEvent event;

    public GuildMemberJoinHandler(GuildMemberJoinEvent event){
        this.event = event;
    }

    public void run(){
        // try to open private channel.
        try{
            event.getUser().openPrivateChannel().queue((channel) ->
            {
                // prepare messages
                String[] messages = {
                        "Hey, "+event.getUser().getName()+" and welcome on "+ event.getGuild().getName()+"."+"\n"+"Have a nice day and a lot of fun on the server!"+"\n"+"With kind regards, Xenia"+"\n",
                        "Hello "+event.getUser().getName()+" nice to meet you."+"\n"+"Glad you made it to "+ event.getGuild().getName() +"\n"+"Have a nice time here!"+"\n"+"Sincerely, Xenia"+"\n",
                        "Hi "+event.getUser().getName()+"!"+"\n"+"Nice to see you here on "+event.getGuild().getName()+"."+"\n"+"Best regards, Xenia"+"\n",
                        "Welcome to "+event.getGuild().getName()+", "+event.getUser().getName()+"!"+"\n"+"With kind regards, Xenia"+"\n",
                        "Welcome to "+event.getGuild().getName()+", enjoy your stay :)"+"\n"+"Xenia"+"\n",
                        "Hey "+event.getUser().getName()+"!"+"\n"+"We're super excited to have you here!"+"\n"+"Xenia"+"\n",
                        "Welcome "+event.getUser().getName()+"!"+"\n"+"Thank you for joining "+event.getGuild().getName()+"\n",
                        "Greetings "+event.getUser().getName()+"!"+"\n"+"Welcome to "+event.getGuild().getName()+"!"+"\n",
                        "Good to see you here "+event.getUser().getName()+"!"+"\n"
                };
                // send message
                channel.sendMessage(messages[new Random().nextInt(messages.length)]).queue();
            });
        }catch (Exception ignore){
            // no need to catch exceptions here but we do so that the thread will finish successful
        }
    }
}
