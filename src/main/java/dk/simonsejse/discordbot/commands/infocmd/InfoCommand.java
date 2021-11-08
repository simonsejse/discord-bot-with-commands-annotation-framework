package dk.simonsejse.discordbot.commands.infocmd;

import dk.simonsejse.discordbot.commands.Command;
import dk.simonsejse.discordbot.commands.CommandPerform;
import dk.simonsejse.discordbot.exceptions.CommandException;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Command(
        prefix = "@",
        cmdName = "info",
        info = "En kommando til at vise info om botten",
        cooldown = 20
)
public class InfoCommand implements CommandPerform {

    @Override
    public void perform(MessageReceivedEvent event) throws CommandException {

        event.getChannel().sendMessage("Simon brugte kommandoen Info!").queue();
    }
}
