package dk.simonsejse.discordbot.commands.music;

import dk.simonsejse.discordbot.JavaMailService;
import dk.simonsejse.discordbot.commands.Command;
import dk.simonsejse.discordbot.commands.CommandPerform;
import dk.simonsejse.discordbot.exceptions.CommandException;
import dk.simonsejse.discordbot.music.PlayerManager;
import dk.simonsejse.discordbot.spotify.controllers.SpotifyAPIManager;
import dk.simonsejse.discordbot.spotify.ex.SpotifyServiceUnavailableException;
import dk.simonsejse.discordbot.spotify.ex.SpotifySongByTrackIDNotFoundException;
import dk.simonsejse.discordbot.spotify.models.SpotifyArtists;
import dk.simonsejse.discordbot.spotify.models.SpotifyTrackInfoData;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.managers.AudioManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


@Command(
        cmdName = "afspil",
        info = "En kommando til at afspille musik!",
        types = {OptionType.STRING, OptionType.BOOLEAN},
        parameterNames = {"url", "album"},
        parameterDescriptions = {"link til din sang", "spil sangen eller hele albummet"},
        isRequired = {true, false}
)
public class PlayCommand implements CommandPerform {

    private final SpotifyAPIManager spotifyAPIManager;
    private final JavaMailService javaMailService;

    @Autowired
    public PlayCommand(SpotifyAPIManager spotifyAPIManager, JavaMailService javaMailService) {
        this.spotifyAPIManager = spotifyAPIManager;
        this.javaMailService = javaMailService;
    }


    @Override
    public void perform(SlashCommandEvent event) throws CommandException {
        if (event.getMember() == null) throw new CommandException("Spilleren kunne ikke findes!");
        if (event.getMember().getVoiceState() == null || !event.getMember().getVoiceState().inVoiceChannel()) throw new CommandException("Du skal være i en tale kanal for at kunne afspille musik.");
        if (event.getGuild() == null) throw new CommandException("Spilleren er ikke i noget guild!");

        String trackURL = event.getOptions().get(0).getAsString();
        final AudioManager audioManager = event.getGuild().getAudioManager();
        final VoiceChannel channel = event.getMember().getVoiceState().getChannel();

        audioManager.openAudioConnection(channel);

        if (trackURL.startsWith("https://open.spotify.com") || trackURL.startsWith("open.spotify.com")){
            if (trackURL.split("track/").length != 2) throw new CommandException("Spotify sangen kunne ikke blive fundet!");
            String spotifyID = trackURL.split("track/")[1];
            final SpotifyTrackInfoData songNameByTrackURL;
            try {
                songNameByTrackURL = this.spotifyAPIManager.getSongNameByTrackURL(spotifyID);
                trackURL = songNameByTrackURL.getName() + " af ";
                String by = String.join(",", songNameByTrackURL.getSpotifyArtists().stream().map(SpotifyArtists::getArtistName).collect(Collectors.toSet()));
                trackURL = trackURL + by;
            } catch (IOException e) {
                javaMailService.addErrorLog(e.getMessage());
                throw new CommandException("Det gik helt galt.. Prøv igen eller hvis det fortsætter bed Simon om at tjekke hans mail!");
            } catch (SpotifySongByTrackIDNotFoundException e) {
                throw new CommandException(String.format("Sangen med ID %s kunne ikke blive fundet!", spotifyID));
            } catch (SpotifyServiceUnavailableException e) {
                throw new CommandException("Noget er galt med spotify's access token, kontakt SimonWin!");
            }
        }

        if (checkIfIsSearch(trackURL))
            trackURL = String.format("ytsearch:%s", trackURL);

        PlayerManager.getPlayerManager().loadAndPlay(event.getGuild(), event.getUser(), trackURL, (message -> {
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
