package dk.simonsejse.discordbot.exceptions;

public class CommandException extends Exception {
    public CommandException(String error){
        super(error);
    }
}
