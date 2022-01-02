package dk.simonsejse.discordbot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import dk.simonsejse.discordbot.commands.Command;
import dk.simonsejse.discordbot.commands.CommandPerform;
import dk.simonsejse.discordbot.exceptions.CommandException;
import dk.simonsejse.discordbot.models.Role;
import dk.simonsejse.discordbot.music.GuildMusicManager;
import dk.simonsejse.discordbot.music.PlayerManager;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.aspectj.weaver.IHasPosition;

import java.time.Duration;
import java.time.LocalTime;

@Command(
        cmdName = "setposition",
        info = "Bruges til at spole frem i musikken!",
        types = {OptionType.STRING},
        parameterNames = "tid",
        parameterDescriptions = "hvor lang tid der skal spoles frem i format hh:mm:ss",
        isRequired = true,
        roleNeeded = Role.ADMIN
)
public class SetPositionCommand implements CommandPerform {
    @Override
    public void perform(SlashCommandEvent event) throws CommandException {
        if (event.getGuild() == null) throw new CommandException("Guildet findes ikke længere!!!");
        final String timeFormat = event.getOptions().get(0).getAsString();

        final GuildMusicManager musicManagerByGuildId = PlayerManager.getPlayerManager().getMusicManagerByGuildId(event.getGuild());
        final AudioTrack currentPlayingTrack = musicManagerByGuildId.getAudioPlayer().getPlayingTrack();
        if(currentPlayingTrack == null) throw new CommandException("Kan ikke spole frem, da der nuværende ikke spiller nogen sang..");

        final AudioTrackInfo trackInfo = currentPlayingTrack.getInfo();

        final long fastForwardAmountInMs = convertStringToMs(timeFormat);
        currentPlayingTrack.setPosition(fastForwardAmountInMs);

        event.deferReply(false).queue(iHook -> {
            iHook.sendMessage(String.format("Sangen `%s` af `%s` blev sat til position `%s`", trackInfo.title, trackInfo.author, delimiter(timeFormat))).queue();
        });
    }

    private String delimiter(String format){
        final String[] split = format.split(":");
        if (split.length < 3) return "wtf";
        return String.format("%st:%sm:%ss", split[0], split[1], split[2]);
    }

    private long convertStringToMs(String formatting){
        return Duration.between(
                LocalTime.MIN,
                LocalTime.parse(formatting)
        ).toMillis();
    }
}
