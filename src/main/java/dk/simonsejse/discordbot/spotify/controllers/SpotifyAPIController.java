package dk.simonsejse.discordbot.spotify.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.simonsejse.discordbot.spotify.SpotifyTokenHandler;
import dk.simonsejse.discordbot.spotify.errors.SpotifyBadRequestException;
import dk.simonsejse.discordbot.interfaces.ISongCallback;
import dk.simonsejse.discordbot.spotify.ex.TokenCouldNotBeRetrievedException;
import dk.simonsejse.discordbot.spotify.models.OkTokenRequest;
import dk.simonsejse.discordbot.spotify.SpotifyHttpManager;
import dk.simonsejse.discordbot.spotify.models.SpotifyTrackInfoData;
import okhttp3.FormBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.collections4.keyvalue.DefaultKeyValue;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Logger;

@Controller
public class SpotifyAPIController {

    private final static Logger LOGGER = Logger.getLogger(SpotifyAPIController.class.getName());
    private final static ObjectMapper mapper = new ObjectMapper();
    private static final String TOKEN_URL = "https://accounts.spotify.com/api/token";
    private static final String BASE_URL = "https://api.spotify.com/v1";
    private static final String CLIENT_ID = System.getenv("SPOTIFY_API_CLIENT_ID");
    private static final String SECRET_ID = System.getenv("SPOTIFY_API_CLIENT_SECRET");

    private final SpotifyHttpManager spotifyHttpManager;
    private final SpotifyTokenHandler spotifyTokenHandler;

    @Autowired
    public SpotifyAPIController(SpotifyHttpManager spotifyHttpManager, SpotifyTokenHandler spotifyTokenHandler) {
        this.spotifyHttpManager = spotifyHttpManager;
        this.spotifyTokenHandler = spotifyTokenHandler;
    }

    public OkTokenRequest getAccessToken() throws TokenCouldNotBeRetrievedException, URISyntaxException, IOException {
        final Response post = spotifyHttpManager.post(
                new URI(TOKEN_URL),
                this.spotifyHttpManager.createHeaders(
                        new DefaultKeyValue<>("Content-Type", "application/x-www-form-urlencoded"),
                        new DefaultKeyValue<>("Authorization", "Basic " + Base64.getEncoder().encodeToString(String.format("%s:%s", CLIENT_ID, SECRET_ID).getBytes(StandardCharsets.UTF_8)))
                ),
                new FormBody.Builder().add("grant_type", "client_credentials")
                        .build()
        );
        final ResponseBody body = post.body();
        assert body != null;
        if (post.code() == HttpStatus.SC_OK){
            return mapper.readValue(body.bytes(), OkTokenRequest.class);
        }else {
            final SpotifyBadRequestException spotifyBadRequestException = mapper.readValue(body.bytes(), SpotifyBadRequestException.class);
            throw new TokenCouldNotBeRetrievedException(spotifyBadRequestException);
        }
    }

    public void getSongNameByTrackURL(String token, String trackID, ISongCallback callback) throws IOException, URISyntaxException {

        final Response response = this.spotifyHttpManager.get(
                new URI(String.format("%s/tracks/%s", BASE_URL, trackID)),
                this.spotifyHttpManager.createHeaders(
                        new DefaultKeyValue<>("Authorization", "Bearer " + token)
                )
        );

        final int code = response.code();
        final ResponseBody body = response.body();
        switch (code) {
            case HttpStatus.SC_OK:
                final SpotifyTrackInfoData spotifyTrackInfoData = mapper.readValue(body.bytes(), SpotifyTrackInfoData.class);
                callback.success(spotifyTrackInfoData);
                break;
            case HttpStatus.SC_UNAUTHORIZED:
                System.out.println("ask for new token using the token?? O_o");
                callback.failure(code, body.string(), true);
                break;
        }
        response.close();

    }

}
