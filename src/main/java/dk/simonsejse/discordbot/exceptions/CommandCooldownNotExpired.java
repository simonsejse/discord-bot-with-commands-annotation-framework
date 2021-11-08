package dk.simonsejse.discordbot.exceptions;

import lombok.Getter;

@Getter
public class CommandCooldownNotExpired extends RuntimeException{
    public CommandCooldownNotExpired(String cooldown){
        super(cooldown);
    }
}
