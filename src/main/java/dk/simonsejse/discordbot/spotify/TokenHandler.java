package dk.simonsejse.discordbot.spotify;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.simonsejse.discordbot.JavaMailService;
import dk.simonsejse.discordbot.spotify.ex.SpotifyServiceUnavailableException;
import dk.simonsejse.discordbot.spotify.ex.TokenRefreshException;
import dk.simonsejse.discordbot.spotify.models.SpotifyAccessToken;
import okhttp3.FormBody;
import okhttp3.Response;
import org.apache.commons.collections4.keyvalue.DefaultKeyValue;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.logging.Logger;

@Component
@EnableScheduling
public class TokenHandler {

    private LocalDateTime lastAttemptAt;
    private int retryTokenFetchCount;

    private static final Logger log = Logger.getLogger(TokenHandler.class.getName());

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String CLIENT_ID = System.getenv("SPOTIFY_CLIENT_ID");
    private static final String SECRET_ID = System.getenv("SPOTIFY_SECRET_ID");
    private static final int MINUTES_BEFORE = 5;


    private final SpotifyURLConfiguration urlConfiguration;
    private final SpotifyHttpManager spotifyHttpManager;
    private final JavaMailService javaMailService;

    private SpotifyAccessToken token;

    @Autowired
    public TokenHandler(SpotifyURLConfiguration spotifyURLConfiguration, SpotifyHttpManager spotifyHttpManager, JavaMailService javaMailService) {
        this.urlConfiguration = spotifyURLConfiguration;
        this.spotifyHttpManager = spotifyHttpManager;
        this.javaMailService = javaMailService;
    }

    public SpotifyAccessToken getSpotifyAccessToken() throws SpotifyServiceUnavailableException {
        if (token == null) throw new SpotifyServiceUnavailableException();
        return this.token;
    }


    //new FormBody.Builder().add("grant_type", "client_credentials").build()

    private SpotifyAccessToken createAccessTokenByClientAndSecretId() throws TokenRefreshException {
        try {
            final Response response = spotifyHttpManager.post(
                    urlConfiguration.getTokenAuthentication(),
                    spotifyHttpManager.createHeaders(
                            new DefaultKeyValue<>("Content-Type", "application/x-www-form-urlencoded"),
                            new DefaultKeyValue<>("Authorization", String.format("Basic %s", Base64.getEncoder().encodeToString((CLIENT_ID+":"+SECRET_ID).getBytes())))
                    ),
                    new FormBody.Builder().add("grant_type", "client_credentials").build()
            );
            if (response.code() == HttpStatus.SC_OK){
                final SpotifyAccessToken spotifyAccessToken = mapper.readValue(response.body().string(), SpotifyAccessToken.class);
                System.out.println(spotifyAccessToken.toString());
                lastAttemptAt = null;
                retryTokenFetchCount = 0;
                return spotifyAccessToken;
            }
        } catch (IOException e) {
            e.printStackTrace();
            lastAttemptAt = LocalDateTime.now();
            retryTokenFetchCount += 1;
            final String error = String.format("Kunne ikke opdatere Spotify-tokenet. Sidst kendte gode token var ved %s, og der har været %d genforsøg. Fejlmeddelelse er %s", lastAttemptAt, retryTokenFetchCount);
            log.severe(error);
            throw new TokenRefreshException(error);
        }
        return null;
    }

    @Scheduled(fixedDelay = 10000)
    public void checkIfTokenIsExpired(){
        if (token == null || Duration.between(
                LocalDateTime.now().minusMinutes(MINUTES_BEFORE),
                token.getExpiresAt()
            ).isNegative()
        ){
            try {
                this.token = createAccessTokenByClientAndSecretId();
            } catch (TokenRefreshException e) {
                if (retryTokenFetchCount == 10 || retryTokenFetchCount%100 == 0){
                    javaMailService.addErrorLog(e.getMessage());
                }
            }

        }
    }
}
