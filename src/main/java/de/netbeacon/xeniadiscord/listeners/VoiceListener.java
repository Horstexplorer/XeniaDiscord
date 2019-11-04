package de.netbeacon.xeniadiscord.listeners;

import de.netbeacon.xeniadiscord.audio.MusicManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.voice.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class VoiceListener extends ListenerAdapter {

    private static MusicManager musicManager;

    public VoiceListener(){
        if(musicManager == null){
            musicManager = new MusicManager();
        }
    }

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event){
        if(event.getMember().getUser().equals(event.getJDA().getSelfUser())){
            // add soundy things :3
            if(musicManager.getAudioPlayer(event.getGuild()) == null){
                musicManager.createAudioPlayer(event.getGuild());
            }
        }
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event){
        if(event.getMember().getUser().equals(event.getJDA().getSelfUser())){
            // stop music
            musicManager.getTrackManager(event.getGuild()).purgeQueue();
            musicManager.getAudioPlayer(event.getGuild()).stopTrack();
        }else{
            if(event.getGuild().getAudioManager().getConnectedChannel().getMembers().size() <= 1){ // only the bot left
                // stop music
                musicManager.getTrackManager(event.getGuild()).purgeQueue();
                musicManager.getAudioPlayer(event.getGuild()).stopTrack();
            }
        }
    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event){
        if(event.getGuild().getAudioManager().getConnectedChannel().getMembers().size() <= 1){ // only the bot left
            // stop music
            musicManager.getTrackManager(event.getGuild()).purgeQueue();
            musicManager.getAudioPlayer(event.getGuild()).stopTrack();
        }
    }

    @Override
    public void onGuildVoiceMute(GuildVoiceMuteEvent event){
        if(event.getMember().getUser().equals(event.getJDA().getSelfUser())){
            // stop music
            musicManager.getTrackManager(event.getGuild()).purgeQueue();
            musicManager.getAudioPlayer(event.getGuild()).stopTrack();
        }
    }

    @Override
    public void onGuildVoiceDeafen(GuildVoiceDeafenEvent event){
        // check if anyone in the channel is still listening
        if(event.getGuild().getAudioManager().isConnected()){
            boolean islistening = false;
            for(Member m : event.getGuild().getAudioManager().getConnectedChannel().getMembers()){
                if(!m.getVoiceState().isDeafened() && !m.getUser().isBot()){
                    islistening = true;
                    break;
                }
            }
            if(!islistening){
                // stop music
                musicManager.getTrackManager(event.getGuild()).purgeQueue();
                musicManager.getAudioPlayer(event.getGuild()).stopTrack();
            }
        }
    }

}
