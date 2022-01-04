package dk.simonsejse.discordbot.exceptions;

import lombok.Getter;

@Getter
public class CommandException extends RuntimeException {
    private boolean deferReply;
    public CommandException(String error, boolean deferReply){
        super(error);
        this.deferReply = deferReply;
    }
    public CommandException(String error){
        this(error, true);
    }

    public CommandException(){ }

    public boolean isDeferReply() {
        return deferReply;
    }

}
