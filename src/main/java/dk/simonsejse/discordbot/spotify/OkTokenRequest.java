package dk.simonsejse.discordbot.spotify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(value = {"token_type", "expires_in"})
public class OkTokenRequest {
    @JsonProperty(value = "access_token")
    private String accessToken;
}
