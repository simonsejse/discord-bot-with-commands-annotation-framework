package dk.simonsejse.discordbot.commands.music;

import dk.simonsejse.discordbot.commands.Command;
import dk.simonsejse.discordbot.commands.CommandPerform;
import dk.simonsejse.discordbot.exceptions.CommandException;
import dk.simonsejse.discordbot.models.Role;
import dk.simonsejse.discordbot.music.GuildMusicManager;
import dk.simonsejse.discordbot.music.PlayerManager;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

@Command(
        cmdName = "pause",
        info = "Bruges til at pause musikken!",
        roleNeeded = Role.ADMIN
)
public class PauseCommand implements CommandPerform {

    @Override
    public void perform(SlashCommandEvent event) throws CommandException {
        if (event.getMember() == null) throw new CommandException("Spilleren kunne ikke findes!");
        if (event.getMember().getVoiceState() == null || !event.getMember().getVoiceState().inVoiceChannel()) throw new CommandException("Spilleren er ikke i en tale kanal!");
        if (event.getGuild() == null) throw new CommandException("Spilleren er ikke i noget guild!");

        final GuildMusicManager musicManagerById = PlayerManager.getPlayerManager().getMusicManagerByGuildId(event.getGuild());
        final boolean isPaused = musicManagerById.getAudioPlayer().isPaused();
        musicManagerById.getAudioPlayer().setPaused(!isPaused);
        event.deferReply(false).queue(iHook -> {
            iHook.sendMessage(isPaused ? "Starter musikken igen..." : "Musikken er blevet pauset!!").queue();
        });
    }
}
