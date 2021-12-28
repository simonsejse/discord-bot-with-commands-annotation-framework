package dk.simonsejse.discordbot.commands.warningcmd;


import dk.simonsejse.discordbot.commands.Command;
import dk.simonsejse.discordbot.commands.CommandPerform;
import dk.simonsejse.discordbot.exceptions.CommandException;
import dk.simonsejse.discordbot.exceptions.UserNotFoundException;
import dk.simonsejse.discordbot.models.Role;
import dk.simonsejse.discordbot.services.UserService;
import dk.simonsejse.discordbot.utility.Messages;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@Command(
        cmdName = "warn",
        info = "Giv en bruger en advarsel!",
        types = {OptionType.USER, OptionType.STRING},
        parameterDescriptions = {"Brugeren du vil give en advarsel!", "En detaljeret beskrivelse over advarslen!"},
        parameterNames = {"bruger", "advarsel"},
        isRequired = {true, false},
        roleNeeded = Role.STAFF
)
public class WarnCommand implements CommandPerform {


    private final UserService userService;
    private final Messages messages;

    @Autowired
    public WarnCommand(final UserService userService, Messages messages){
        this.userService = userService;
        this.messages = messages;
    }

    @Override
    public void perform(SlashCommandEvent event) throws CommandException {
        final int size = event.getOptions().size();

        if (size == 2) performWarnUser(event);
        else performUserViewWarnings(event);

    }

    private void performWarnUser(SlashCommandEvent event) throws CommandException {
        final User warned = event.getOptions().get(0).getAsUser();
        final User warnedBy = event.getUser();

        final String reason = event.getOptions().get(1).getAsString();

        try {
            final LocalDateTime now = LocalDateTime.now();
            this.userService.addWarning(warned, warnedBy, reason, now);
            final Message message = this.messages.successfullyWarnUser(warned, warnedBy, reason, now);
            event.deferReply(false).queue(iHook -> {
                iHook.sendMessage(message)
                        .queue();
            });
        } catch (UserNotFoundException e) {
            event.deferReply(true).queue(interactionHook -> {
                interactionHook.sendMessage(messages.userCreatedInDB(e.getId())).queue();
            });
            this.userService.createNewUserByID(e.getId(), event.getGuild().getIdLong());
        }

    }

    private void performUserViewWarnings(SlashCommandEvent event) throws CommandException {



    }



}
