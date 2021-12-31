package dk.simonsejse.discordbot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;


public class AudioSentHandler implements AudioSendHandler {
    private final AudioPlayer audioPlayer;
    private final ByteBuffer buffer;
    private final MutableAudioFrame lastFrame;

    public AudioSentHandler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        this.buffer = ByteBuffer.allocate(2024);
        this.lastFrame = new MutableAudioFrame();
        this.lastFrame.setBuffer(this.buffer);
    }

    @Override
    public boolean canProvide() {
        return this.audioPlayer.provide(this.lastFrame);
    }

    @Nullable
    @Override
    public ByteBuffer provide20MsAudio() {
        return this.buffer.flip();
    }

    @Override
    public boolean isOpus() {
        return true;
    }
}
