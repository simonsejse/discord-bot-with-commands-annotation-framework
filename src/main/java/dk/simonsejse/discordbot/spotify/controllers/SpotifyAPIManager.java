package dk.simonsejse.discordbot.spotify.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.simonsejse.discordbot.exceptions.ResponseFetchException;
import dk.simonsejse.discordbot.spotify.SpotifyHttpManager;
import dk.simonsejse.discordbot.spotify.SpotifyURLConfiguration;
import dk.simonsejse.discordbot.spotify.TokenHandler;
import dk.simonsejse.discordbot.spotify.ex.SpotifyServiceUnavailableException;
import dk.simonsejse.discordbot.spotify.models.SpotifyAccessToken;
import dk.simonsejse.discordbot.spotify.models.SpotifyTrackInfoData;
import org.apache.commons.collections4.keyvalue.DefaultKeyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.logging.Logger;

@Component
public class SpotifyAPIManager {
    private final static ObjectMapper mapper = new ObjectMapper();
    private final static Logger log = Logger.getLogger(SpotifyAPIManager.class.getName());

    private final TokenHandler tokenHandler;
    private final SpotifyURLConfiguration urlConfiguration;
    private final SpotifyHttpManager spotifyHttpManager;

    @Autowired
    public SpotifyAPIManager(TokenHandler tokenHandler, SpotifyURLConfiguration spotifyURLConfiguration, SpotifyHttpManager spotifyHttpManager) {
        this.tokenHandler = tokenHandler;
        this.urlConfiguration = spotifyURLConfiguration;
        this.spotifyHttpManager = spotifyHttpManager;
    }


    public SpotifyTrackInfoData getSongNameByTrackURL(String trackID) throws SpotifyServiceUnavailableException {
        String newPath = String.format("%s/tracks/%s", urlConfiguration.getBaseUrl().getPath(), trackID);
        log.info(String.format("Sending get request to %s", newPath));
        final SpotifyAccessToken spotifyAccessToken = tokenHandler.getSpotifyAccessToken();

        try {
            String response = this.spotifyHttpManager.get(
                    urlConfiguration.getBaseUrl().resolve(newPath),
                    this.spotifyHttpManager.createHeaders(
                            new DefaultKeyValue<>("Authorization", String.format("%s %s", spotifyAccessToken.getTokenType(), spotifyAccessToken.getAccessToken()))
                    )
            );
            log.info(String.format("Requesting Body for song with id '%s' has body %s", trackID, response));

            return mapper.readValue(response, SpotifyTrackInfoData.class);
        } catch (IOException | ResponseFetchException exception) {
            throw new SpotifyServiceUnavailableException(exception.getMessage());
        }

    }

}
