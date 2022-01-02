package dk.simonsejse.discordbot.commands;

import dk.simonsejse.discordbot.exceptions.CommandException;
import dk.simonsejse.discordbot.interfaces.ISongCallback;
import dk.simonsejse.discordbot.spotify.SpotifyTokenHandler;
import dk.simonsejse.discordbot.spotify.models.SpotifyArtists;
import dk.simonsejse.discordbot.spotify.models.SpotifyTrackInfoData;
import dk.simonsejse.discordbot.spotify.controllers.SpotifyAPIController;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.stream.Collectors;


@Command(
    cmdName = "test",
    info = "test"
)
public class TestCommand implements CommandPerform{

    private final SpotifyAPIController spotifyAPIController;
    private final SpotifyTokenHandler tokenHandler;
    @Autowired
    public TestCommand(SpotifyAPIController spotifyAPIController, SpotifyTokenHandler tokenHandler) {
        this.spotifyAPIController = spotifyAPIController;
        this.tokenHandler = tokenHandler;
    }

    @Override
    public void perform(SlashCommandEvent event) throws CommandException {
        try {
            this.spotifyAPIController.getSongNameByTrackURL(tokenHandler.getToken(), "5Y3ID1NKAg8qydyVCrM7ji?si=26a5b8f4c35a4d8e", new ISongCallback() {
                @Override
                public void success(SpotifyTrackInfoData spotifyTrackInfo) {
                    String name = spotifyTrackInfo.getName() + " af ";
                    String by = String.join(",", spotifyTrackInfo.getSpotifyArtists().stream().map(SpotifyArtists::getArtistName).collect(Collectors.toSet()));
                    name = name + by;
                    event.reply(name).queue();
                }

                @Override
                public void failure(int code, String errorMsg, boolean isTokenExpired) {
                    event.reply("Kunne ikke finde beskeden spotify med fejlbeskeden: "+errorMsg).queue();
                }
            });
        } catch (IOException | URISyntaxException exception) {
            exception.printStackTrace();
        }
    }
}
