package dk.simonsejse.discordbot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dk.simonsejse.discordbot.interfaces.IMusicMessageCallback;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerManager {

    private final Map<Long, GuildMusicManager> musicManagersByGuildIds;

    /**
     * This is an IMPL for LavaPlayer that automatically loads things for us
     * we then later use it to add to our TrackScheduler
     */

    private final AudioPlayerManager audioPlayerManager;

    private static PlayerManager INSTANCE;

    public PlayerManager() {
        this.musicManagersByGuildIds = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public GuildMusicManager getMusicManagerByGuildId(Guild guild){
        return this.musicManagersByGuildIds.computeIfAbsent(guild.getIdLong(), (guildID) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);
            guild.getAudioManager().setSendingHandler(guildMusicManager.getAudioHandler());
            return guildMusicManager;
        });
    }

    public void loadAndPlay(TextChannel textChannel, String trackURL, IMusicMessageCallback iMusicMessageCallback){
        final GuildMusicManager musicManagerByGuildId = this.getMusicManagerByGuildId(textChannel.getGuild());

        this.audioPlayerManager.loadItemOrdered(musicManagerByGuildId, trackURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                musicManagerByGuildId.getTrackScheduler().queueTrack(audioTrack);
                iMusicMessageCallback.callback(String.format("Tilføjet `%s` af `%s` til køen.", audioTrack.getInfo().title, audioTrack.getInfo().author));
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                if (!audioPlaylist.isSearchResult()){
                    final List<AudioTrack> tracks = audioPlaylist.getTracks();
                    tracks.forEach(musicManagerByGuildId.getTrackScheduler()::queueTrack);
                    iMusicMessageCallback.callback(String.format("***Vrinsk*** - jeg har sat `%d` sange i kø fra playlisten `%s`", tracks.size(), audioPlaylist.getName()));
                }else{
                    final AudioTrack audioTrack = audioPlaylist.getTracks().get(0);
                    musicManagerByGuildId.getTrackScheduler().queueTrack(audioTrack);
                    iMusicMessageCallback.callback(String.format("Tilføjet `%s` af `%s` til køen.", audioTrack.getInfo().title, audioTrack.getInfo().author));
                }
            }

            @Override
            public void noMatches() {
                iMusicMessageCallback.callback("Hvad er det for noget pis, du forsøger at få mig til at afspille. Giv mig et oprigtigt url.");
            }

            @Override
            public void loadFailed(FriendlyException e) {
                iMusicMessageCallback.callback(String.format("Kunne ikke loade en af sangene med fejlbeskenden %s", e.getMessage()));
            }
        });
    }

    public static PlayerManager getPlayerManager(){
        if (INSTANCE == null){
            INSTANCE = new PlayerManager();
        }
        return INSTANCE;
    }
}
