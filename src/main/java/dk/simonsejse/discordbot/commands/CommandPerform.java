package dk.simonsejse.discordbot.commands;

import dk.simonsejse.discordbot.exceptions.CommandException;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.springframework.stereotype.Component;

@Component
public interface CommandPerform {
    void perform(SlashCommandEvent event) throws CommandException;
}
