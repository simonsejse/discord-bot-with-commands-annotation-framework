package dk.simonsejse.discordbot.spotify.ex;

public class TokenException extends Exception {
    public TokenException(String error){
        super(error);
    }
}
