package dk.simonsejse.discordbot.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.simonsejse.discordbot.errors.BadRequestException;
import dk.simonsejse.discordbot.interfaces.ISongCallback;
import dk.simonsejse.discordbot.interfaces.ITokenCallback;
import dk.simonsejse.discordbot.spotify.OkTokenRequest;
import dk.simonsejse.discordbot.spotify.SpotifyHttpManager;
import okhttp3.FormBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.collections4.keyvalue.DefaultKeyValue;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
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

    @Autowired
    public SpotifyAPIController(SpotifyHttpManager spotifyHttpManager) {
        this.spotifyHttpManager = spotifyHttpManager;
    }

    @Bean
    public void getAccessToken() throws URISyntaxException, IOException {
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
            final OkTokenRequest okTokenRequest = mapper.readValue(body.bytes(), OkTokenRequest.class);
            System.out.println(okTokenRequest.getAccessToken());
           // callback.success(okTokenRequest);
        }else {
            final BadRequestException badRequestException = mapper.readValue(body.bytes(), BadRequestException.class);
           // callback.failure(badRequestException);
        }
    }

    public void getSongNameByTrackURL(String token, ISongCallback callback) throws IOException, URISyntaxException {


    }

}
