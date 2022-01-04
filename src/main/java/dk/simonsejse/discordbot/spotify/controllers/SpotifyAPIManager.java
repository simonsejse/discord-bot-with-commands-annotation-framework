package dk.simonsejse.discordbot.spotify.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.simonsejse.discordbot.spotify.SpotifyHttpManager;
import dk.simonsejse.discordbot.spotify.SpotifyURLConfiguration;
import dk.simonsejse.discordbot.spotify.TokenHandler;
import dk.simonsejse.discordbot.spotify.ex.SpotifyServiceUnavailableException;
import dk.simonsejse.discordbot.spotify.ex.SpotifySongByTrackIDNotFoundException;
import dk.simonsejse.discordbot.spotify.models.SpotifyAccessToken;
import dk.simonsejse.discordbot.spotify.models.SpotifyTrackInfoData;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.collections4.keyvalue.DefaultKeyValue;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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


    public SpotifyTrackInfoData getSongNameByTrackURL(String trackID) throws IOException, SpotifySongByTrackIDNotFoundException, SpotifyServiceUnavailableException {
        String newPath = String.format("%s/tracks/%s", urlConfiguration.getBaseUrl().getPath(), trackID);
        log.info(String.format("Sending get request to %s", newPath));
        final SpotifyAccessToken spotifyAccessToken = tokenHandler.getSpotifyAccessToken();

        final Response response = this.spotifyHttpManager.get(
                urlConfiguration.getBaseUrl().resolve(newPath),
                this.spotifyHttpManager.createHeaders(
                        new DefaultKeyValue<>("Authorization", String.format("%s %s", spotifyAccessToken.getTokenType(), spotifyAccessToken.getAccessToken()))
                )
        );

        final int code = response.code();
        final String string = response.body().string();
        log.info(String.format("Requesting Body for song with id '%s' has body %s", trackID, string));
        switch (code) {
            case HttpStatus.SC_OK:
                return mapper.readValue(string, SpotifyTrackInfoData.class);
            default:
                throw new SpotifySongByTrackIDNotFoundException();
        }

    }

}
