package dk.simonsejse.discordbot.exceptions;

import lombok.Getter;

@Getter
public class CommandException extends RuntimeException {
    public CommandException(String error){
        super(error);
    }
}
