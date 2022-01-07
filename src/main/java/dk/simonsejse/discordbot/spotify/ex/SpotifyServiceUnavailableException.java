package dk.simonsejse.discordbot.spotify.ex;

public class SpotifyServiceUnavailableException extends Exception{
    public SpotifyServiceUnavailableException(String message) {
        super(message);
    }
}
