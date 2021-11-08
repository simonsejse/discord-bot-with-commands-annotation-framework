package dk.simonsejse.discordbot.cooldown;

import dk.simonsejse.discordbot.commands.Command;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class CooldownCommand {
    private Command command;
    private LocalDateTime whenExecuted;
}
