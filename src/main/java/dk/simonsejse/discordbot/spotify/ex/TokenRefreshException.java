package dk.simonsejse.discordbot.spotify.ex;

public class TokenRefreshException extends Exception {
    public TokenRefreshException(String error){
        super(error);
    }
}
