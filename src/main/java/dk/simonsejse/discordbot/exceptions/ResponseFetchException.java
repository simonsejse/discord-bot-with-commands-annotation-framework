package dk.simonsejse.discordbot.exceptions;

public class ResponseFetchException extends Exception {
    public ResponseFetchException(String errorMsg) {
        super(errorMsg);
    }
}
