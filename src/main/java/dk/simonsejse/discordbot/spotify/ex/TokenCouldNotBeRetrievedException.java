package dk.simonsejse.discordbot.spotify.ex;

import dk.simonsejse.discordbot.spotify.errors.SpotifyBadRequestException;

public class TokenCouldNotBeRetrievedException extends Throwable {
    private SpotifyBadRequestException spotifyBadRequestException;
    public TokenCouldNotBeRetrievedException(SpotifyBadRequestException spotifyBadRequestException) {
        this.spotifyBadRequestException = spotifyBadRequestException;
    }
}
