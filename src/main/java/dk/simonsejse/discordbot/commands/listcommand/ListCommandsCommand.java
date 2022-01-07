package dk.simonsejse.discordbot.commands.listcommand;

import dk.simonsejse.discordbot.commands.Command;
import dk.simonsejse.discordbot.commands.CommandPerform;
import dk.simonsejse.discordbot.exceptions.CommandException;
import dk.simonsejse.discordbot.utility.Messages;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.logging.Logger;

@Command(
        cmdName = "kommandoer",
        info = "En kommando der viser alle kommandoer bot dover indeholder.",
        cooldown = 15
)
public class ListCommandsCommand implements CommandPerform {

    private static final Logger log = Logger.getLogger(ListCommandsCommand.class.getName());

    private final Message message;

    @Autowired
    public ListCommandsCommand(List<CommandPerform> commandsList, Messages messages) {
        log.info("Loading ListAllCommands Message!");
        this.message = messages.listAllCommands(commandsList);
    }

    @Override
    public void perform(SlashCommandEvent event) throws CommandException {
        event.deferReply(false).queue(iHook -> {
            iHook.sendMessage(message).queue();
        });
    }
}
