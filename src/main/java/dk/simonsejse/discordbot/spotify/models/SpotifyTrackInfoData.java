package dk.simonsejse.discordbot.spotify.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.simonsejse.discordbot.spotify.models.SpotifyArtists;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpotifyTrackInfoData {
    @JsonProperty(value = "id")
    private String id;
    @JsonProperty(value = "name")
    private String name;
    @JsonProperty(value = "popularity")
    private int popularity;

    @JsonProperty(value = "artists")
    private Set<SpotifyArtists> spotifyArtists;
}
