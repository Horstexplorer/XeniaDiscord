package de.netbeacon.xeniadiscord.commands.guild;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import de.netbeacon.xeniadiscord.audio.MusicManager;
import de.netbeacon.xeniadiscord.util.extperm.ExtPermManager;
import de.netbeacon.xeniadiscord.util.extperm.permission.ExtPerm;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Music implements GuildCommand {

    private static MusicManager musicManager;
    public Music(){
        if(musicManager == null){
            musicManager = new MusicManager();
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
                                musicManager.getAudioPlayer(event.getGuild());

                                String input = Arrays.stream(args).skip(2).map(s -> " " + s).collect(Collectors.joining()).substring(1);
                                if (!(input.startsWith("http://") || input.startsWith("https://")))
                                    input = "ytsearch: " + input;
                                musicManager.loadTrack(input, event.getGuild());
                                Random random = new Random();
                                event.getChannel().sendMessage(new EmbedBuilder()
                                        .setColor(new Color(random.nextFloat(),random.nextFloat(),random.nextFloat()).brighter())
                                        .setDescription("Added to queue.")
                                        .build()).queue(message -> {message.delete().queueAfter(15,TimeUnit.SECONDS);});
                            }catch (Exception e){
                                Random random = new Random();
                                event.getChannel().sendMessage(new EmbedBuilder()
                                        .setColor(new Color(random.nextFloat(),random.nextFloat(),random.nextFloat()).brighter())
                                        .setDescription("Could not connect to voice. Please check my permissions.")
                                        .build()).queue(message -> {message.delete().queueAfter(15,TimeUnit.SECONDS);});
                            }
                        }else{
                            Random random = new Random();
                            event.getChannel().sendMessage(new EmbedBuilder()
                                    .setColor(new Color(random.nextFloat(),random.nextFloat(),random.nextFloat()).brighter())
                                    .setDescription("Could not connect to voice. Make sure you are connected to a voice channel where I can join (or try connecting me manually)")
                                    .build()).queue(message -> {message.delete().queueAfter(15,TimeUnit.SECONDS);});
                        }
                    }else{
                        Random random = new Random();
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setColor(new Color(random.nextFloat(),random.nextFloat(),random.nextFloat()).brighter())
                                .setDescription("Could not connect to voice. You don't have the necessary permissions to connect me to a voice channel.")
                                .build()).queue(message -> {message.delete().queueAfter(15,TimeUnit.SECONDS);});
                    }

                }else{
                    String input = Arrays.stream(args).skip(2).map(s -> " " + s).collect(Collectors.joining()).substring(1);
                    if (!(input.startsWith("http://") || input.startsWith("https://")))
                        input = "ytsearch: " + input;
                    musicManager.loadTrack(input, event.getGuild());
                    Random random = new Random();
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setColor(new Color(random.nextFloat(),random.nextFloat(),random.nextFloat()).brighter())
                            .setDescription("Added to queue.")
                            .build()).queue(message -> {message.delete().queueAfter(15,TimeUnit.SECONDS);});
                }
            }
            if(args[1].toLowerCase().equals("volume") && new ExtPermManager().hasPermission(member, new ExtPerm[]{ExtPerm.admin, ExtPerm.music_all, ExtPerm.music_manage_queue})){
                int i = -1;
                try{
                    i = Integer.parseInt(args[2]);
                }catch (Exception ignore){}
                if(i >= 0){
                    musicManager.setVolume(i, event.getGuild());
                    Random random = new Random();
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setColor(new Color(random.nextFloat(),random.nextFloat(),random.nextFloat()).brighter())
                            .setDescription("Set volume to "+i)
                            .build()).queue(message -> {message.delete().queueAfter(15,TimeUnit.SECONDS);});
                }
            }
        }
        if(args.length > 1){
            if(args[1].toLowerCase().equals("info") && new ExtPermManager().hasPermission(member, new ExtPerm[]{ExtPerm.admin, ExtPerm.music_all, ExtPerm.music_play})){
                if (musicManager.isIdle(event.getGuild())) return;
                musicManager.getTrackManager(event.getGuild()).shuffleQueue();
                if (musicManager.isIdle(event.getGuild())) return;
                AudioTrack track = musicManager.getAudioPlayer(event.getGuild()).getPlayingTrack();
                AudioTrackInfo info = track.getInfo();
                Random random = new Random();
                event.getChannel().sendMessage(
                        new EmbedBuilder()
                                .setDescription("**CURRENT TRACK INFO:**")
                                .setColor(new Color(random.nextFloat(),random.nextFloat(),random.nextFloat()).brighter())
                                .addField("Title", info.title, false)
                                .addField("Author", info.author, false)
                                .build()
                ).queue(message -> {message.delete().queueAfter(15,TimeUnit.SECONDS);});
            }
            if((args[1].toLowerCase().equals("list") || args[1].toLowerCase().equals("queue")) && new ExtPermManager().hasPermission(member, new ExtPerm[]{ExtPerm.admin, ExtPerm.music_all, ExtPerm.music_play})){
                if (musicManager.isIdle(event.getGuild())) return;
                int sideNumb = 1;
                if(args.length > 3){
                    sideNumb = Integer.parseInt(args[1]);
                }
                List<String> tracks = new ArrayList<>();
                List<String> trackSublist;
                musicManager.getTrackManager(event.getGuild()).getQueue().forEach(audioInfo -> tracks.add(musicManager.buildQueueMessage(audioInfo)));
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
                ).queue(message -> {message.delete().queueAfter(15,TimeUnit.SECONDS);});
            }
            if((args[1].toLowerCase().equals("next") || args[1].toLowerCase().equals("skip")) && new ExtPermManager().hasPermission(member, new ExtPerm[]{ExtPerm.admin, ExtPerm.music_all, ExtPerm.music_manage_queue})){
                if (musicManager.isIdle(event.getGuild())) return;
                musicManager.skip(event.getGuild());
                Random random = new Random();
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setColor(new Color(random.nextFloat(),random.nextFloat(),random.nextFloat()).brighter())
                        .setDescription("Track skipped")
                        .build()).queue(message -> {message.delete().queueAfter(15,TimeUnit.SECONDS);});
            }
            if(args[1].toLowerCase().equals("shuffle") && new ExtPermManager().hasPermission(member, new ExtPerm[]{ExtPerm.admin, ExtPerm.music_all, ExtPerm.music_manage_queue})){
                if (musicManager.isIdle(event.getGuild())) return;
                musicManager.getTrackManager(event.getGuild()).shuffleQueue();
                Random random = new Random();
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setColor(new Color(random.nextFloat(),random.nextFloat(),random.nextFloat()).brighter())
                        .setDescription("Queue shuffled")
                        .build()).queue(message -> {message.delete().queueAfter(15,TimeUnit.SECONDS);});
            }
            if(args[1].toLowerCase().equals("stop") && new ExtPermManager().hasPermission(member, new ExtPerm[]{ExtPerm.admin, ExtPerm.music_all, ExtPerm.music_stop})){
                musicManager.getTrackManager(event.getGuild()).purgeQueue();
                musicManager.skip(event.getGuild());
                Random random = new Random();
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setColor(new Color(random.nextFloat(),random.nextFloat(),random.nextFloat()).brighter())
                        .setDescription("Music stopped")
                        .build()).queue(message -> {message.delete().queueAfter(15,TimeUnit.SECONDS);});
            }
            if(args[1].toLowerCase().equals("connect") && new ExtPermManager().hasPermission(member, new ExtPerm[]{ExtPerm.admin, ExtPerm.music_all, ExtPerm.music_manage_connection})){
                if(!event.getGuild().getAudioManager().isConnected()){
                    try{
                        if(args.length > 2){
                            if(event.getGuild().getVoiceChannelById(args[2]) != null){
                                event.getGuild().getAudioManager().openAudioConnection(event.getGuild().getVoiceChannelById(args[2]));
                                musicManager.getAudioPlayer(event.getGuild());
                            }
                        }else{
                            if(member.getVoiceState().getChannel() != null){
                                event.getGuild().getAudioManager().openAudioConnection(member.getVoiceState().getChannel());
                                musicManager.getAudioPlayer(event.getGuild());
                            }
                        }
                    }catch (Exception e){
                        Random random = new Random();
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setColor(new Color(random.nextFloat(),random.nextFloat(),random.nextFloat()).brighter())
                                .setDescription("Could not connect to voice. Make sure you are connected to a voice channel where I can join (or try connecting me manually)")
                                .build()).queue(message -> {message.delete().queueAfter(15, TimeUnit.SECONDS);});
                    }
                }
            }
            if(args[1].toLowerCase().equals("disconnect") && new ExtPermManager().hasPermission(member, new ExtPerm[]{ExtPerm.admin, ExtPerm.music_all, ExtPerm.music_manage_connection})){
                musicManager.getTrackManager(event.getGuild()).purgeQueue();
                musicManager.skip(event.getGuild());
                event.getGuild().getAudioManager().closeAudioConnection();
            }
        }

        event.getMessage().delete().queueAfter(10, TimeUnit.SECONDS);
    }

}
