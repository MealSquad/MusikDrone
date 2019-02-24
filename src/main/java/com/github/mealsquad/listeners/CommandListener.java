package com.github.mealsquad.listeners;

import com.github.mealsquad.guild.GuildMusicManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.AudioManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandListener extends ListenerAdapter {

    private static final Logger log = LogManager.getFormatterLogger();
    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;

    public CommandListener() {
        this.musicManagers = new HashMap<>();
        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        log.info("Getting Audio Player for Guild with Id: %s", guildId);
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String[] command = event.getMessage().getContentRaw().split(" ", 2);
        Guild guild = event.getGuild();

        if (guild != null) {
            if (";play".equals(command[0]) && command.length == 2) {
                loadAndPlay(event.getTextChannel(), command[1], event.getAuthor());
            } else if (";skip".equals(command[0])) {
                skipTrack(event.getTextChannel());
            } else if (";stop".equals(command[0])) {
                stopTrack(event.getTextChannel());
            }
        }

        super.onMessageReceived(event);
    }

    private void loadAndPlay(final TextChannel channel, final String trackUrl, final User user) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                channel.sendMessage("Adding to queue " + track.getInfo().title).queue();
                log.info("Adding Track: %s to queue, requested by %s", track.getInfo().title, user.getName());

                play(channel.getGuild(), musicManager, track, user);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                channel.sendMessage("Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")").queue();

                play(channel.getGuild(), musicManager, firstTrack, user);
            }

            @Override
            public void noMatches() {
                channel.sendMessage("Nothing found by " + trackUrl).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                channel.sendMessage("Could not play: " + exception.getMessage()).queue();
            }
        });
    }

    private void play(Guild guild, GuildMusicManager musicManager, AudioTrack track, User user) {
        connectToUserOccupiedVoiceChannel(guild.getAudioManager(), user);

        musicManager.scheduler.queue(track);
    }

    private void skipTrack(TextChannel channel) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.scheduler.nextTrack();

        channel.sendMessage("Skipped to next track.").queue();
    }

    private void stopTrack(TextChannel channel) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.player.stopTrack();

        channel.sendMessage("Stopping track").queue();
    }

    private static void connectToUserOccupiedVoiceChannel(AudioManager audioManager, User user) {
        if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
            for (VoiceChannel voiceChannel : audioManager.getGuild().getVoiceChannels()) {
                if (voiceChannel.getMembers().stream().map(Member::getEffectiveName).collect(Collectors.toList()).contains(user.getName())) {
                    audioManager.openAudioConnection(voiceChannel);
                    log.info("Established audio connection with Voice Channel %s in Guild %s", voiceChannel.getName(), voiceChannel.getGuild().getName());
                    break;
                } else {
                    log.warn("Requesting user %s not present in Voice Channel: %s", user.getName(), voiceChannel.getName());
                }
            }
        }
    }
}
