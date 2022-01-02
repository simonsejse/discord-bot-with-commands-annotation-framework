package dk.simonsejse.discordbot.spotify.errors;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SpotifyError {
    @JsonProperty(value = "status")
    private int code;
    @JsonProperty(value = "message")
    private String message;
    protected SpotifyError() { }
}
