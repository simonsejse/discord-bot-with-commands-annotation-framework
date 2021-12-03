package dk.simonsejse.discordbot.commands.point;

import dk.simonsejse.discordbot.commands.Command;
import dk.simonsejse.discordbot.commands.CommandPerform;
import dk.simonsejse.discordbot.exceptions.CommandException;
import dk.simonsejse.discordbot.exceptions.UserNotFoundException;
import dk.simonsejse.discordbot.models.Role;
import dk.simonsejse.discordbot.services.UserService;
import dk.simonsejse.discordbot.utility.Messages;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@Command(
    cmdName = "setpoint",
    info = "Kommandoen er til staff så de kan give folk point!",
    types = {OptionType.USER, OptionType.INTEGER},
    parameterNames = {"bruger", "antal_point"},
    parameterDescriptions = {"bruger du skal sætte navn på", "hvor mange point brugeren skal have!"},
    isRequired = {true, true},
    roleNeeded = Role.OWNER
)
@AllArgsConstructor
public class SetPointCommand implements CommandPerform {

    private final Messages messages;
    private final UserService userService;


    @Override
    public void perform(SlashCommandEvent event) throws CommandException {
        if (!event.getOptions().isEmpty()){
            final User targetUser = event.getOptions().get(0).getAsUser();
            final long points = event.getOptions().get(1).getAsLong();
            try {
                long oldPoints = userService.setUserPointsById(targetUser.getIdLong(), points);

                event.deferReply(false).queue(interactionHook -> {
                    interactionHook.sendMessage(this.messages.successfullySetUserPoints(targetUser, points, oldPoints)).queue();
                });
            } catch (UserNotFoundException e) {
                event.deferReply(false).queue(interactionHook -> {
                    interactionHook.sendMessage(this.messages.userNotFound).queue();
                });
            }
        }else event.deferReply(true).queue(interactionHook -> {
            interactionHook.sendMessage(this.messages.setUserPointsMissingArguments).queue();
        });
    }
}
