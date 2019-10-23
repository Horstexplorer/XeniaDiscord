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
import de.netbeacon.xeniadiscord.audio.AudioInfo;
import de.netbeacon.xeniadiscord.audio.PlayerSendHandler;
import de.netbeacon.xeniadiscord.audio.TrackManager;
import de.netbeacon.xeniadiscord.util.extperm.ExtPermManager;
import de.netbeacon.xeniadiscord.util.extperm.permission.ExtPerm;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.*;
import java.util.stream.Collectors;

public class Music implements GuildCommand {

    private static final int PLAYLIST_LIMIT = 1000;
    private static Guild guild;
    private static final AudioPlayerManager MANAGER = new DefaultAudioPlayerManager();
    private static final Map<Guild, Map.Entry<AudioPlayer, TrackManager>> PLAYERS = new HashMap<>();

    public Music() {
        AudioSourceManagers.registerRemoteSources(MANAGER);
    }

    @Override
    public void execute(GuildMessageReceivedEvent event, Member member, String[] args) {
        guild = event.getGuild();
        if (args.length > 2) {
            if(args[1].toLowerCase().equals("play") && new ExtPermManager().hasPermission(member, new ExtPerm[]{ExtPerm.admin, ExtPerm.music_all, ExtPerm.music_play})){
                String input = Arrays.stream(args).skip(2).map(s -> " " + s).collect(Collectors.joining()).substring(1);
                if (!(input.startsWith("http://") || input.startsWith("https://")))
                    input = "ytsearch: " + input;
                loadTrack(input, member, event.getMessage());
                event.getChannel().sendMessage("Added to queue.").queue();
            }
            if(args[1].toLowerCase().equals("volume") && new ExtPermManager().hasPermission(member, new ExtPerm[]{ExtPerm.admin, ExtPerm.music_all, ExtPerm.music_manage_queue})){
                int i = -1;
                try{
                    i = Integer.parseInt(args[2]);
                }catch (Exception ignore){}
                if(i >= 0){
                    setVolume(i, event.getGuild());
                }
            }

        }
        if(args.length > 1){
            if((args[1].toLowerCase().equals("list") || args[1].toLowerCase().equals("queue")) && new ExtPermManager().hasPermission(member, new ExtPerm[]{ExtPerm.admin, ExtPerm.music_all, ExtPerm.music_play})){
                if (isIdle(guild)) return;
                int sideNumb = 1;
                if(args.length > 3){
                    sideNumb = Integer.parseInt(args[1]);
                }
                List<String> tracks = new ArrayList<>();
                List<String> trackSublist;
                getManager(guild).getQueue().forEach(audioInfo -> tracks.add(buildQueueMessage(audioInfo)));
                if (tracks.size() > 20)
                    trackSublist = tracks.subList((sideNumb-1)*20, (sideNumb-1)*20+20);
                else
                    trackSublist = tracks;
                String out = trackSublist.stream().collect(Collectors.joining("\n"));
                int sideNumbAll = tracks.size() >= 20 ? tracks.size() / 20 : 1;
                event.getChannel().sendMessage(
                        new EmbedBuilder()
                                .setDescription(
                                        "**CURRENT QUEUE:**\n" +
                                                "*[ Tracks | Side " + sideNumb + " / " + sideNumbAll + "]*\n" +
                                                out
                                )
                                .build()
                ).queue();
            }
            if(args[1].toLowerCase().equals("stop") && new ExtPermManager().hasPermission(member, new ExtPerm[]{ExtPerm.admin, ExtPerm.music_all, ExtPerm.music_stop})){
                getManager(guild).purgeQueue();
                skip(guild);
            }
            if((args[1].toLowerCase().equals("next") || args[1].toLowerCase().equals("skip")) && new ExtPermManager().hasPermission(member, new ExtPerm[]{ExtPerm.admin, ExtPerm.music_all, ExtPerm.music_manage_queue})){
                if (isIdle(guild)) return;
                skip(guild);
            }
            if(args[1].toLowerCase().equals("shuffle") && new ExtPermManager().hasPermission(member, new ExtPerm[]{ExtPerm.admin, ExtPerm.music_all, ExtPerm.music_manage_queue})){
                if (isIdle(guild)) return;
                getManager(guild).shuffleQueue();
            }
            if(args[1].toLowerCase().equals("info") && new ExtPermManager().hasPermission(member, new ExtPerm[]{ExtPerm.admin, ExtPerm.music_all, ExtPerm.music_play})){
                if (isIdle(guild)) return;
                getManager(guild).shuffleQueue();
                if (isIdle(guild)) return;
                AudioTrack track = getPlayer(guild).getPlayingTrack();
                AudioTrackInfo info = track.getInfo();
                event.getChannel().sendMessage(
                        new EmbedBuilder()
                                .setDescription("**CURRENT TRACK INFO:**")
                                .addField("Title", info.title, false)
                                .addField("Author", info.author, false)
                                .build()
                ).queue();
            }
            if(args[1].toLowerCase().equals("off") && new ExtPermManager().hasPermission(member, new ExtPerm[]{ExtPerm.admin, ExtPerm.music_all, ExtPerm.music_manage_off})){
                getManager(guild).purgeQueue();
                skip(guild);
                guild.getAudioManager().closeAudioConnection();
            }
        }
    }

    private AudioPlayer createPlayer(Guild g) {
        AudioPlayer p = MANAGER.createPlayer();
        TrackManager m = new TrackManager(p);
        p.addListener(m);

        guild.getAudioManager().setSendingHandler(new PlayerSendHandler(p));

        PLAYERS.put(g, new AbstractMap.SimpleEntry<>(p, m));

        return p;
    }

    private boolean hasPlayer(Guild g) {
        return PLAYERS.containsKey(g);
    }

    private AudioPlayer getPlayer(Guild g) {
        if (hasPlayer(g))
            return PLAYERS.get(g).getKey();
        else
            return createPlayer(g);
    }

    private TrackManager getManager(Guild g) {
        return PLAYERS.get(g).getValue();
    }

    private boolean isIdle(Guild g) {
        return !hasPlayer(g) || getPlayer(g).getPlayingTrack() == null;
    }

    private String buildQueueMessage(AudioInfo info) {
        return info.getTrack().getInfo().title;
    }

    private void loadTrack(String identifier, Member author, Message msg) {

        Guild guild = author.getGuild();
        getPlayer(guild);

        MANAGER.setFrameBufferDuration(5000);
        MANAGER.loadItemOrdered(guild, identifier, new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack track) {
                getManager(guild).queue(track, author);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                for (int i = 0; i < (playlist.getTracks().size() > PLAYLIST_LIMIT ? PLAYLIST_LIMIT : playlist.getTracks().size()); i++) {
                    getManager(guild).queue(playlist.getTracks().get(i), author);
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

    private void skip(Guild g) {
        getPlayer(g).stopTrack();
    }

    private void setVolume(int i, Guild g){
        if(i < 0){
            getPlayer(g).setVolume(0);
        }else getPlayer(g).setVolume(Math.min(i, 100));
    }
}
