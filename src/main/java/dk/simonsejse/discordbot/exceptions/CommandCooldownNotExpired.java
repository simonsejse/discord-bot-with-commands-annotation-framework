package dk.simonsejse.discordbot.exceptions;

public class CommandCooldownNotExpired extends RuntimeException{
    public CommandCooldownNotExpired(String msg){
        super(msg);
    }
}
