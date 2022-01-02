package dk.simonsejse.discordbot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;


public class AudioSentHandler implements AudioSendHandler {
    private static final int BUFFER_SIZE = 1024;

    private final AudioPlayer audioPlayer;
    private final ByteBuffer buffer;
    private final MutableAudioFrame audioFrame;

    public AudioSentHandler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        this.audioFrame = new MutableAudioFrame();
        this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
        this.audioFrame.setBuffer(this.buffer);
    }

    @Override
    public boolean canProvide() {
        return this.audioPlayer.provide(this.audioFrame);
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
