package dk.simonsejse.discordbot.spotify;

import dk.simonsejse.discordbot.spotify.controllers.SpotifyAPIController;
import dk.simonsejse.discordbot.spotify.errors.SpotifyBadRequestException;
import dk.simonsejse.discordbot.spotify.models.OkTokenRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Logger;

@Getter
@Setter
@Component
public class SpotifyTokenHandler {

    private final static Logger LOGGER = Logger.getLogger(SpotifyTokenHandler.class.getName());

    private final SpotifyAPIController spotifyAPIController;
    @Nonnull
    private String token;

    @Autowired
    public SpotifyTokenHandler(SpotifyAPIController spotifyAPIController) {
        this.spotifyAPIController = spotifyAPIController;
    }

    @Bean
    public void generateTokenOnStart(){
        LOGGER.info("Trying to create Spotify API token");

    }

}
