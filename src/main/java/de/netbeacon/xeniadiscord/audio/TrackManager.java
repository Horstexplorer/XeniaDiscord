package de.netbeacon.xeniadiscord.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackManager extends AudioEventAdapter {

    private final AudioPlayer PLAYER;
    private final Queue<AudioTrack> queue;
    private final Guild guild;

    public TrackManager(AudioPlayer player, Guild guild) {
        this.PLAYER = player;
        this.queue = new LinkedBlockingQueue<>();
        this.guild = guild;
    }

    public void queue(AudioTrack audioTrack){
        queue.add(audioTrack);
        if (PLAYER.getPlayingTrack() == null) {
            PLAYER.playTrack(queue.remove());
        }
    }

    public void purgeQueue() {
        queue.clear();
    }

    public Set<AudioTrack> getQueue() {
        return new LinkedHashSet<>(queue);
    }

    public void shuffleQueue() {
        List<AudioTrack> cQueue = new ArrayList<>(getQueue());
        Collections.shuffle(cQueue);
        purgeQueue();
        Collections.shuffle(cQueue);
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        // check if connected
        if(!guild.getAudioManager().isConnected()){
            // stop
            purgeQueue();
            PLAYER.stopTrack();
        }else{
            if(guild.getAudioManager().getConnectedChannel().getMembers().size() > 0){
                boolean islistening = false;
                for(Member m : guild.getAudioManager().getConnectedChannel().getMembers()){
                    if(!m.getUser().isBot() && !m.getVoiceState().isDeafened()){
                        islistening = true;
                    }
                }
                if(!islistening) {
                    // stop
                    purgeQueue();
                    PLAYER.stopTrack();
                }
            }else{
                // stop
                purgeQueue();
                PLAYER.stopTrack();
            }
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        try{
            if(!queue.isEmpty()){
                player.playTrack(queue.remove());
            }
        }catch (NullPointerException ignore){}
    }

}