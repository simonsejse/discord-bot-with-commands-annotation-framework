package dk.simonsejse.discordbot.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserNotFoundException extends Exception {
    private long id;
}
