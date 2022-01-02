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

import java.time.Duration;
import java.time.LocalTime;

@Command(
        cmdName = "spolfrem",
        info = "Bruges til at spole frem i musikken!",
        types = {OptionType.INTEGER},
        parameterNames = "tid",
        parameterDescriptions = "hvor lang tid der skal spoles frem i sekunder",
        isRequired = true,
        roleNeeded = Role.ADMIN
)
public class FastForwardCommand implements CommandPerform {
    @SuppressWarnings("Duplicates")
    @Override
    public void perform(SlashCommandEvent event) throws CommandException {
        if (event.getGuild() == null) throw new CommandException("Guildet findes ikke længere!!!");
        final long timeInSeconds = event.getOptions().get(0).getAsLong();

        final GuildMusicManager musicManagerByGuildId = PlayerManager.getPlayerManager().getMusicManagerByGuildId(event.getGuild());
        final AudioTrack currentPlayingTrack = musicManagerByGuildId.getAudioPlayer().getPlayingTrack();
        if (currentPlayingTrack == null)
            throw new CommandException("Kan ikke spole frem, da der nuværende ikke spiller nogen sang..");

        final AudioTrackInfo trackInfo = currentPlayingTrack.getInfo();
        final long fastForwardAmountInMs = timeInSeconds * 1000;
        currentPlayingTrack.setPosition(currentPlayingTrack.getPosition() + fastForwardAmountInMs);

        event.deferReply(false).queue(iHook -> {
            iHook.sendMessage(String.format("Sangen `%s` af `%s` blev spolet `%ds` frem", trackInfo.title, trackInfo.author, timeInSeconds)).queue();
        });
    }
}