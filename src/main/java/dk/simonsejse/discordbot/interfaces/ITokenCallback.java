package dk.simonsejse.discordbot.interfaces;

import dk.simonsejse.discordbot.errors.BadRequestException;
import dk.simonsejse.discordbot.spotify.OkTokenRequest;

public interface ITokenCallback {
    void success(OkTokenRequest token);
    void failure(BadRequestException error);
}
