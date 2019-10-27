package de.netbeacon.xeniadiscord.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Guild;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackManager extends AudioEventAdapter {

    private final AudioPlayer PLAYER;
    private final Queue<AudioTrack> queue;

    public TrackManager(AudioPlayer player) {
        this.PLAYER = player;
        this.queue = new LinkedBlockingQueue<>();
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
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        try{
            if(!queue.isEmpty()){
                player.playTrack(queue.remove());
            }
        }catch (NullPointerException ignore){}
    }

}