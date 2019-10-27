package de.netbeacon.xeniadiscord.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class MusicManager {

    private static final int PLAYLIST_LIMIT = 256;
    private static final AudioPlayerManager MANAGER = new DefaultAudioPlayerManager();
    private static AtomicReference<HashMap<Guild, AudioPackage>> PLAYERS = new AtomicReference<HashMap<Guild, AudioPackage>>();

    public MusicManager(){
        if(PLAYERS.get() == null){
            System.out.println("[INFO] Init MusicManager");
            AudioSourceManagers.registerRemoteSources(MANAGER);
            PLAYERS.set(new HashMap<Guild, AudioPackage>());
        }
    }

    public AudioPlayer getAudioPlayer(Guild guild){
        // find existing
        if(PLAYERS.get().containsKey(guild)){
            return PLAYERS.get().get(guild).getAudioPlayer();
        }
        // create new
        AudioPlayer p = MANAGER.createPlayer();
        p.setFrameBufferDuration(2000);
        TrackManager m = new TrackManager(p, guild);
        p.addListener(m);
        guild.getAudioManager().setSendingHandler(new PlayerSendHandler(p));
        PLAYERS.get().put(guild, new AudioPackage(p, m, guild));

        return p;
    }

    public boolean hasAudioPlayer(Guild guild){
        return PLAYERS.get().containsKey(guild);
    }

    public TrackManager getTrackManager(Guild guild){
        return PLAYERS.get().get(guild).getTrackManager();
    }

    public void loadTrack(String identifier, Guild guild){
        MANAGER.loadItemOrdered(guild, identifier, new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack track) {
                getTrackManager(guild).queue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                for (int i = 0; i < (Math.min(playlist.getTracks().size(), PLAYLIST_LIMIT)); i++) {
                    getTrackManager(guild).queue(playlist.getTracks().get(i));
                }
            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException exception) {
                exception.printStackTrace();
            }
        });
    }

    public void skip(Guild guild) {
        getAudioPlayer(guild).stopTrack();
    }

    public void setVolume(int i, Guild guild){
        if(i < 0){
            getAudioPlayer(guild).setVolume(0);
        }else getAudioPlayer(guild).setVolume(Math.min(i, 100));
    }

    public boolean isIdle(Guild guild) {
        return !hasAudioPlayer(guild) || getAudioPlayer(guild).getPlayingTrack() == null;
    }

    public String buildQueueMessage(AudioTrack audioTrack) {
        return audioTrack.getInfo().title;
    }
}
