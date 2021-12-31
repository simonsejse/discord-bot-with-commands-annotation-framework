package dk.simonsejse.discordbot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

public class GuildMusicManager {
    private final AudioPlayer audioPlayer;
    private final TrackScheduler trackScheduler;
    private final AudioSentHandler audioSentHandler;

    public GuildMusicManager(AudioPlayerManager manager) {
        this.audioPlayer = manager.createPlayer();
        this.trackScheduler = new TrackScheduler(this.audioPlayer);
        this.audioPlayer.addListener(trackScheduler);
        this.audioSentHandler = new AudioSentHandler(this.audioPlayer);
    }

    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }

    public TrackScheduler getTrackScheduler() {
        return trackScheduler;
    }

    public AudioSentHandler getAudioHandler() {
        return audioSentHandler;
    }
}
