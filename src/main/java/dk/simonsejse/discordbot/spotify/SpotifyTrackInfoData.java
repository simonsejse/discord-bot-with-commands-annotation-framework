package dk.simonsejse.discordbot.spotify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpotifyTrackInfoData {
    @JsonProperty(value = "name")
    private String name;
}
