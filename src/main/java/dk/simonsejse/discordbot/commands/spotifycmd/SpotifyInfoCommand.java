package dk.simonsejse.discordbot.commands.spotifycmd;

import dk.simonsejse.discordbot.commands.Command;
import dk.simonsejse.discordbot.commands.CommandPerform;
import dk.simonsejse.discordbot.exceptions.CommandException;
import dk.simonsejse.discordbot.spotify.controllers.SpotifyAPIManager;
import dk.simonsejse.discordbot.spotify.ex.SpotifyServiceUnavailableException;
import dk.simonsejse.discordbot.spotify.models.SpotifyArtists;
import dk.simonsejse.discordbot.spotify.models.SpotifyTrackInfoData;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.utils.AttachmentOption;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.stream.Collectors;


@Command(
    cmdName = "test",
    info = "test"
)
public class SpotifyInfoCommand implements CommandPerform {

    private final SpotifyAPIManager spotifyAPIManager;


    @Autowired
    public SpotifyInfoCommand(SpotifyAPIManager spotifyAPIManager) {
        this.spotifyAPIManager = spotifyAPIManager;
    }

    @Override
    public void perform(SlashCommandEvent event) throws CommandException {
        /*
        String message;
        try {
            final SpotifyTrackInfoData songNameByTrackURL = this.spotifyAPIManager.getSongNameByTrackURL("5Y3ID1NKAg8qydyVCrM7ji?si=9e248403757d4007");

            String name = songNameByTrackURL.getName() + " af ";
            String by = String.join(",", songNameByTrackURL.getSpotifyArtists().stream().map(SpotifyArtists::getArtistName).collect(Collectors.toSet()));
            name = name + by;
            message = name;
        } catch (SpotifyServiceUnavailableException e) {
            message = String.format("Noget er galt med spotify's access token, kontakt SimonWin!\nFejlbesked er %s", e.getMessage());
        }
        String finalMessage = message;
        event.deferReply(false).queue(iHook -> {
            iHook.sendMessage(finalMessage).queue();
        });

         */
    }
}
