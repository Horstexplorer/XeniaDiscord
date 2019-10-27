package de.netbeacon.xeniadiscord.commands.guild;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import de.netbeacon.xeniadiscord.audio.AudioPackage;
import de.netbeacon.xeniadiscord.audio.PlayerSendHandler;
import de.netbeacon.xeniadiscord.audio.TrackManager;
import de.netbeacon.xeniadiscord.util.extperm.ExtPermManager;
import de.netbeacon.xeniadiscord.util.extperm.permission.ExtPerm;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class Music implements GuildCommand {

    private static final int PLAYLIST_LIMIT = 256;
    private static final AudioPlayerManager MANAGER = new DefaultAudioPlayerManager();
    private static AtomicReference<HashMap<Guild, AudioPackage>> PLAYERS = new AtomicReference<HashMap<Guild, AudioPackage>>();

    public Music(){
        if(PLAYERS.get() == null){
            AudioSourceManagers.registerRemoteSources(MANAGER);
            PLAYERS.set(new HashMap<Guild, AudioPackage>());
        }
    }

    @Override
    public void execute(GuildMessageReceivedEvent event, Member member, String[] args) {
        if(args.length > 2){
            if(args[1].toLowerCase().equals("play") && new ExtPermManager().hasPermission(member, new ExtPerm[]{ExtPerm.admin, ExtPerm.music_all, ExtPerm.music_play})){
                if(!event.getGuild().getAudioManager().isConnected()){
                    // try connect
                    if(new ExtPermManager().hasPermission(member, new ExtPerm[]{ExtPerm.admin, ExtPerm.music_all, ExtPerm.music_play})){
                        if(member.getVoiceState().getChannel() != null){
                            try{
                                event.getGuild().getAudioManager().openAudioConnection(member.getVoiceState().getChannel());
                                getAudioPlayer(event.getGuild());

                                String input = Arrays.stream(args).skip(2).map(s -> " " + s).collect(Collectors.joining()).substring(1);
                                if (!(input.startsWith("http://") || input.startsWith("https://")))
                                    input = "ytsearch: " + input;
                                loadTrack(input, event.getGuild());
                                Random random = new Random();
                                event.getChannel().sendMessage(new EmbedBuilder()
                                        .setColor(new Color(random.nextFloat(),random.nextFloat(),random.nextFloat()).brighter())
                                        .setDescription("Added to queue.")
                                        .build()).queue();
                            }catch (Exception e){
                                Random random = new Random();
                                event.getChannel().sendMessage(new EmbedBuilder()
                                        .setColor(new Color(random.nextFloat(),random.nextFloat(),random.nextFloat()).brighter())
                                        .setDescription("Could not connect to voice. Please check my permissions.")
                                        .build()).queue();
                            }
                        }else{
                            Random random = new Random();
                            event.getChannel().sendMessage(new EmbedBuilder()
                                    .setColor(new Color(random.nextFloat(),random.nextFloat(),random.nextFloat()).brighter())
                                    .setDescription("Could not connect to voice. Make sure you are connected to a voice channel where I can join (or try connecting me manually)")
                                    .build()).queue();
                        }
                    }else{
                        Random random = new Random();
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setColor(new Color(random.nextFloat(),random.nextFloat(),random.nextFloat()).brighter())
                                .setDescription("Could not connect to voice. You don't have the necessary permissions to connect me to a voice channel.")
                                .build()).queue();
                    }

                }else{
                    String input = Arrays.stream(args).skip(2).map(s -> " " + s).collect(Collectors.joining()).substring(1);
                    if (!(input.startsWith("http://") || input.startsWith("https://")))
                        input = "ytsearch: " + input;
                    loadTrack(input, event.getGuild());
                    Random random = new Random();
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setColor(new Color(random.nextFloat(),random.nextFloat(),random.nextFloat()).brighter())
                            .setDescription("Added to queue.")
                            .build()).queue();
                }
            }
            if(args[1].toLowerCase().equals("volume") && new ExtPermManager().hasPermission(member, new ExtPerm[]{ExtPerm.admin, ExtPerm.music_all, ExtPerm.music_manage_queue})){
                int i = -1;
                try{
                    i = Integer.parseInt(args[2]);
                }catch (Exception ignore){}
                if(i >= 0){
                    setVolume(i, event.getGuild());
                    Random random = new Random();
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setColor(new Color(random.nextFloat(),random.nextFloat(),random.nextFloat()).brighter())
                            .setDescription("Set volume to "+i)
                            .build()).queue();
                }
            }
        }
        if(args.length > 1){
            if(args[1].toLowerCase().equals("info") && new ExtPermManager().hasPermission(member, new ExtPerm[]{ExtPerm.admin, ExtPerm.music_all, ExtPerm.music_play})){
                if (isIdle(event.getGuild())) return;
                getTrackManager(event.getGuild()).shuffleQueue();
                if (isIdle(event.getGuild())) return;
                AudioTrack track = getAudioPlayer(event.getGuild()).getPlayingTrack();
                AudioTrackInfo info = track.getInfo();
                Random random = new Random();
                event.getChannel().sendMessage(
                        new EmbedBuilder()
                                .setDescription("**CURRENT TRACK INFO:**")
                                .setColor(new Color(random.nextFloat(),random.nextFloat(),random.nextFloat()).brighter())
                                .addField("Title", info.title, false)
                                .addField("Author", info.author, false)
                                .build()
                ).queue();
            }
            if((args[1].toLowerCase().equals("list") || args[1].toLowerCase().equals("queue")) && new ExtPermManager().hasPermission(member, new ExtPerm[]{ExtPerm.admin, ExtPerm.music_all, ExtPerm.music_play})){
                if (isIdle(event.getGuild())) return;
                int sideNumb = 1;
                if(args.length > 3){
                    sideNumb = Integer.parseInt(args[1]);
                }
                List<String> tracks = new ArrayList<>();
                List<String> trackSublist;
                getTrackManager(event.getGuild()).getQueue().forEach(audioInfo -> tracks.add(buildQueueMessage(audioInfo)));
                if (tracks.size() > 20)
                    trackSublist = tracks.subList((sideNumb-1)*20, (sideNumb-1)*20+20);
                else
                    trackSublist = tracks;
                String out = trackSublist.stream().collect(Collectors.joining("\n"));
                int sideNumbAll = tracks.size() >= 20 ? tracks.size() / 20 : 1;
                Random random = new Random();
                event.getChannel().sendMessage(
                        new EmbedBuilder()
                                .setColor(new Color(random.nextFloat(),random.nextFloat(),random.nextFloat()).brighter())
                                .setDescription(
                                        "**CURRENT QUEUE:**\n" +
                                                "*[ Tracks | Side " + sideNumb + " / " + sideNumbAll + "]*\n" +
                                                out
                                )
                                .build()
                ).queue();
            }
            if((args[1].toLowerCase().equals("next") || args[1].toLowerCase().equals("skip")) && new ExtPermManager().hasPermission(member, new ExtPerm[]{ExtPerm.admin, ExtPerm.music_all, ExtPerm.music_manage_queue})){
                if (isIdle(event.getGuild())) return;
                skip(event.getGuild());
                Random random = new Random();
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setColor(new Color(random.nextFloat(),random.nextFloat(),random.nextFloat()).brighter())
                        .setDescription("Track skipped")
                        .build()).queue();
            }
            if(args[1].toLowerCase().equals("shuffle") && new ExtPermManager().hasPermission(member, new ExtPerm[]{ExtPerm.admin, ExtPerm.music_all, ExtPerm.music_manage_queue})){
                if (isIdle(event.getGuild())) return;
                getTrackManager(event.getGuild()).shuffleQueue();
                Random random = new Random();
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setColor(new Color(random.nextFloat(),random.nextFloat(),random.nextFloat()).brighter())
                        .setDescription("Queue shuffled")
                        .build()).queue();
            }
            if(args[1].toLowerCase().equals("stop") && new ExtPermManager().hasPermission(member, new ExtPerm[]{ExtPerm.admin, ExtPerm.music_all, ExtPerm.music_stop})){
                getTrackManager(event.getGuild()).purgeQueue();
                skip(event.getGuild());
                Random random = new Random();
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setColor(new Color(random.nextFloat(),random.nextFloat(),random.nextFloat()).brighter())
                        .setDescription("Music stopped")
                        .build()).queue();
            }
            if(args[1].toLowerCase().equals("connect") && new ExtPermManager().hasPermission(member, new ExtPerm[]{ExtPerm.admin, ExtPerm.music_all, ExtPerm.music_manage_connection})){
                if(!event.getGuild().getAudioManager().isConnected()){
                    try{
                        if(args.length > 2){
                            if(event.getGuild().getVoiceChannelById(args[2]) != null){
                                event.getGuild().getAudioManager().openAudioConnection(event.getGuild().getVoiceChannelById(args[2]));
                                getAudioPlayer(event.getGuild());
                            }
                        }else{
                            if(member.getVoiceState().getChannel() != null){
                                event.getGuild().getAudioManager().openAudioConnection(member.getVoiceState().getChannel());
                                getAudioPlayer(event.getGuild());
                            }
                        }
                    }catch (Exception e){
                        Random random = new Random();
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setColor(new Color(random.nextFloat(),random.nextFloat(),random.nextFloat()).brighter())
                                .setDescription("Could not connect to voice. Make sure you are connected to a voice channel where I can join (or try connecting me manually)")
                                .build()).queue();
                    }
                }
            }
            if(args[1].toLowerCase().equals("disconnect") && new ExtPermManager().hasPermission(member, new ExtPerm[]{ExtPerm.admin, ExtPerm.music_all, ExtPerm.music_manage_connection})){
                getTrackManager(event.getGuild()).purgeQueue();
                skip(event.getGuild());
                event.getGuild().getAudioManager().closeAudioConnection();
            }
        }
    }

    //####################################################################

    private AudioPlayer getAudioPlayer(Guild guild){
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

    private boolean hasAudioPlayer(Guild guild){
        return PLAYERS.get().containsKey(guild);
    }

    private TrackManager getTrackManager(Guild guild){
        return PLAYERS.get().get(guild).getTrackManager();
    }

    private void loadTrack(String identifier, Guild guild){
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

    private void skip(Guild guild) {
        getAudioPlayer(guild).stopTrack();
    }

    private void setVolume(int i, Guild guild){
        if(i < 0){
            getAudioPlayer(guild).setVolume(0);
        }else getAudioPlayer(guild).setVolume(Math.min(i, 100));
    }

    private boolean isIdle(Guild guild) {
        return !hasAudioPlayer(guild) || getAudioPlayer(guild).getPlayingTrack() == null;
    }

    private String buildQueueMessage(AudioTrack audioTrack) {
        return audioTrack.getInfo().title;
    }
}
