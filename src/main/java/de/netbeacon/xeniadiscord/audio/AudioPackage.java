package de.netbeacon.xeniadiscord.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.entities.Guild;

public class AudioPackage {

    private TrackManager trackManager;
    private AudioPlayer audioPlayer;
    private Guild guild;

    public AudioPackage (AudioPlayer audioPlayer, TrackManager trackManager, Guild guild){
        this.trackManager = trackManager;
        this.audioPlayer = audioPlayer;
        this.guild = guild;
    }

    public AudioPlayer getAudioPlayer(){ return this.audioPlayer; }
    public TrackManager getTrackManager(){ return this.trackManager; }
    public Guild getGuild(){return this.guild; }
}
