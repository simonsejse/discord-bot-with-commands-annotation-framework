package dk.simonsejse.discordbot.exceptions;

public class NameInfoGetRequestMisMatchException extends Exception {
    public NameInfoGetRequestMisMatchException(String error){
        super(error);
    }
}
