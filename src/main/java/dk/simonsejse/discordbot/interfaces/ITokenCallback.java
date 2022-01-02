package dk.simonsejse.discordbot.interfaces;

import dk.simonsejse.discordbot.spotify.errors.SpotifyBadRequestException;
import dk.simonsejse.discordbot.spotify.models.OkTokenRequest;

public interface ITokenCallback {
    void success(OkTokenRequest token);
    void failure(SpotifyBadRequestException error);
}
