package dk.simonsejse.discordbot.spotify.errors;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
public class SpotifyBadRequestException {
    @JsonProperty(value = "error")
    private SpotifyError error;
    @JsonProperty(value = "error")
    private String errorMessage;
    protected SpotifyBadRequestException() { }
}
