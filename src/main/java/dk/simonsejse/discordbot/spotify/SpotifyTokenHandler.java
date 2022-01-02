package dk.simonsejse.discordbot.spotify;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

@AllArgsConstructor
@Getter
@Setter
@Component
public class SpotifyTokenHandler {
    @Nullable
    private String token;
}
