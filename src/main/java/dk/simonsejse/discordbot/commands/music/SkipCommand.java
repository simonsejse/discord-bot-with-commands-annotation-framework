package dk.simonsejse.discordbot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dk.simonsejse.discordbot.commands.Command;
import dk.simonsejse.discordbot.commands.CommandPerform;
import dk.simonsejse.discordbot.exceptions.CommandException;
import dk.simonsejse.discordbot.models.Role;
import dk.simonsejse.discordbot.music.GuildMusicManager;
import dk.simonsejse.discordbot.music.PlayerManager;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

@Command(
     cmdName = "skip",
     info = "Skip nuværende sang",
     roleNeeded = Role.ADMIN
)
public class SkipCommand implements CommandPerform {
    @SuppressWarnings("Duplicates")
    @Override
    public void perform(SlashCommandEvent event) throws CommandException {
        if (event.getMember() == null) throw new CommandException("Spilleren kunne ikke findes!");
        if (event.getMember().getVoiceState() == null && !event.getMember().getVoiceState().inVoiceChannel()) throw new CommandException("Spilleren er ikke i en tale kanal!");
        if (event.getGuild() == null) throw new CommandException("Spilleren er ikke i noget guild!");

        final GuildMusicManager musicManagerByGuildId = PlayerManager.getPlayerManager().getMusicManagerByGuildId(event.getGuild());
        final AudioTrack currentPlayingTrack = musicManagerByGuildId.getAudioPlayer().getPlayingTrack();

        if (currentPlayingTrack == null) throw new CommandException("Der er ingen sang du kan skippe..");

        event.reply(String.format("Sangen `%s (%dm)` af `%s` er blevet skippet!", currentPlayingTrack.getInfo().title, ((currentPlayingTrack.getInfo().length/1000)/60), currentPlayingTrack.getInfo().author)).queue();
        musicManagerByGuildId.getTrackScheduler().nextTrack();
    }
}
