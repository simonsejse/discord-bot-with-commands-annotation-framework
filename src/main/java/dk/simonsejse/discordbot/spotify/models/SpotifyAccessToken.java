package dk.simonsejse.discordbot.spotify.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class SpotifyAccessToken {

    @JsonProperty(value = "access_token")
    private String accessToken;

    @JsonProperty(value = "token_type")
    private String tokenType;

    @JsonProperty(value = "expires_in")
    private long expiresInSeconds;

    @JsonIgnore
    private LocalDateTime expiresAt;

    public SpotifyAccessToken(String accessToken, String tokenType, long expiresInSeconds){
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresInSeconds = expiresInSeconds;
        this.expiresAt = LocalDateTime.now().plusSeconds(expiresInSeconds);
    }

    protected SpotifyAccessToken() {}

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresInSeconds() {
        return expiresInSeconds;
    }

    public void setExpiresInSeconds(long expiresInSeconds) {
        this.expiresInSeconds = expiresInSeconds;
        setExpiresAt(LocalDateTime.now().plusSeconds(expiresInSeconds));
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
