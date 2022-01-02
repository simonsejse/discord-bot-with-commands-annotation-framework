package dk.simonsejse.discordbot.spotify.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpotifyArtists {
    @JsonProperty(value = "name")
    private String artistName;

    protected SpotifyArtists() { }
}
