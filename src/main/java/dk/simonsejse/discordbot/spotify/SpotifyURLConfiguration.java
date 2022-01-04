package dk.simonsejse.discordbot.spotify;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Configuration
@ConfigurationProperties(prefix = "spotify")
@Getter
@Setter
public class SpotifyURLConfiguration {
    private URI baseUrl;
    private URI tokenAuthentication;
}
