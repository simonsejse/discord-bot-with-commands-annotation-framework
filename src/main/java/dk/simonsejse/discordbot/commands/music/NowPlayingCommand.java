package dk.simonsejse.discordbot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import dk.simonsejse.discordbot.commands.Command;
import dk.simonsejse.discordbot.commands.CommandPerform;
import dk.simonsejse.discordbot.exceptions.CommandException;
import dk.simonsejse.discordbot.models.mcreq.Player;
import dk.simonsejse.discordbot.music.GuildMusicManager;
import dk.simonsejse.discordbot.music.PlayerManager;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

@Command(
        cmdName = "hvadspiller",
        info = "Viser info om den nuværende sang der spiller"
)
public class NowPlayingCommand implements CommandPerform {

    @SuppressWarnings("Duplicates")
    @Override
    public void perform(SlashCommandEvent event) throws CommandException {
        if (event.getMember() == null) throw new CommandException("Spilleren kunne ikke findes!");
        if (event.getMember().getVoiceState() == null && !event.getMember().getVoiceState().inVoiceChannel()) throw new CommandException("Spilleren er ikke i en tale kanal!");
        if (event.getGuild() == null) throw new CommandException("Spilleren er ikke i noget guild!");

        final GuildMusicManager musicManagerByGuildId = PlayerManager.getPlayerManager().getMusicManagerByGuildId(event.getGuild());
        final AudioTrack currentPlayingTrack = musicManagerByGuildId.getAudioPlayer().getPlayingTrack();

        if (currentPlayingTrack == null) throw new CommandException("Der er ingen info at vise, da der er ingen sang der spiller");
        final AudioTrackInfo trackInfo = currentPlayingTrack.getInfo();
        event.deferReply().queue(iHook -> {
            iHook.sendMessage(String.format("Lige nu spilles `%s` af `%s` (Link: <%s>)", trackInfo.title, trackInfo.author, trackInfo.uri)).queue();
        });

    }
}
