package dk.simonsejse.discordbot.commands;

import dk.simonsejse.discordbot.exceptions.CommandException;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

@Component
public interface CommandPerform {
    void perform(MessageReceivedEvent event) throws CommandException;
}
