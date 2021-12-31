package dk.simonsejse.discordbot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {

    private final AudioPlayer audioPlayer;
    private final Queue<AudioTrack> trackQueue;

    public TrackScheduler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        this.trackQueue = new LinkedBlockingQueue<>();
    }

    public void clearTrackQueue(){
        this.trackQueue.clear();
    }

    public void nextTrack() {
        this.audioPlayer.startTrack(this.trackQueue.poll(), false);
    }

    public void queueTrack(AudioTrack nextTrack){
        if (!this.audioPlayer.startTrack(nextTrack, true)){
            this.trackQueue.offer(nextTrack);
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext){
            nextTrack();
        }
    }



}
