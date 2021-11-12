package dk.simonsejse.discordbot.commands.infocmd;

import dk.simonsejse.discordbot.commands.Command;
import dk.simonsejse.discordbot.commands.CommandPerform;
import dk.simonsejse.discordbot.exceptions.CommandException;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.time.temporal.ChronoUnit;

@Command(
        cmdName = "info",
        info = "En kommando til at vise info om botten og hvem den er lavet af!",
        unit = ChronoUnit.MINUTES,
        cooldown = 1
)
public class InfoCommand implements CommandPerform {

    @Override
    public void perform(SlashCommandEvent event) throws CommandException {
        event.reply("Ja, det virker jo fint..").queue();

    }




}
