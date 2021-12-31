package dk.simonsejse.discordbot.commands.music;

import dk.simonsejse.discordbot.commands.Command;
import dk.simonsejse.discordbot.commands.CommandPerform;
import dk.simonsejse.discordbot.exceptions.CommandException;
import dk.simonsejse.discordbot.music.PlayerManager;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.managers.AudioManager;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


@Command(
        cmdName = "afspil",
        info = "En kommando til at afspille musik!",
        types = {OptionType.STRING},
        parameterNames = {"url"},
        parameterDescriptions = {"link til din sang"},
        isRequired = {true}
)
public class PlayCommand implements CommandPerform {

    @Override
    public void perform(SlashCommandEvent event) throws CommandException {
        if (event.getMember() == null) throw new CommandException("Spilleren kunne ikke findes!");
        if (event.getMember().getVoiceState() == null && !event.getMember().getVoiceState().inVoiceChannel()) throw new CommandException("Spilleren er ikke i en tale kanal!");
        if (event.getGuild() == null) throw new CommandException("Spilleren er ikke i noget guild!");

        String trackURL = event.getOptions().get(0).getAsString();
        final AudioManager audioManager = event.getGuild().getAudioManager();
        final VoiceChannel channel = event.getMember().getVoiceState().getChannel();

        audioManager.openAudioConnection(channel);

        if (checkIfIsSearch(trackURL))
            trackURL = String.format("ytsearch:%s", trackURL);

        PlayerManager.getPlayerManager().loadAndPlay(event.getTextChannel(), trackURL, (message -> {
            event.deferReply(false).queue(iHook -> {
                iHook.sendMessage(message).queue();
            });
        }));
    }

    private boolean checkIfIsSearch(String trackURL) {
        try{
            new URL(trackURL).toURI();
            return false;
        }catch(URISyntaxException | MalformedURLException e){
            return true;
        }
    }
}
