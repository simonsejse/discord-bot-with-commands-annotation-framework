package dk.simonsejse.discordbot.commands.point;

import dk.simonsejse.discordbot.commands.Command;
import dk.simonsejse.discordbot.commands.CommandPerform;
import dk.simonsejse.discordbot.exceptions.CommandException;
import dk.simonsejse.discordbot.services.UserService;
import dk.simonsejse.discordbot.utility.Messages;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Command(
        cmdName = "points",
        cooldown = 0,
        parameterNames = {"bruger"},
        parameterDescriptions = {"Hvilken persons point du gerne vil tjekke"},
        info = "Tjek hvor mange point du eller en anden har!",
        types = {OptionType.USER},
        isRequired = {false}
)
@AllArgsConstructor
public class PointCommand implements CommandPerform {

    private final UserService userService;
    private final Messages messages;

    @Override
    public void perform(SlashCommandEvent event) throws CommandException {
        final long userId = event.getUser().getIdLong();

        final boolean isSelf = event.getOptions().isEmpty();

        Optional<Long> optionUserPoints = Optional.empty();
        Optional<String> optionUserName = Optional.empty();

        if (!isSelf){
            final User argAsUser = event.getOptionsByType(OptionType.USER).get(0).getAsUser();

            optionUserPoints = Optional.of(this.userService.getUserByID(argAsUser.getIdLong()).orElseThrow(() -> {
                throw new CommandException("Brugeren findes ikke!");
            }).getPoints());

            optionUserName = Optional.of(argAsUser.getAsTag());

        }

        long userPoints = this.userService.getUserByID(userId).orElseThrow(() -> {
            throw new CommandException("Brugeren findes ikke!");
        }).getPoints();


        final Message pointsMessage = this.messages.amountOfUserPointsByPointsAndName(optionUserPoints, optionUserName, userPoints);

        event.deferReply().queue(interactionHook -> {
            interactionHook.sendMessage(pointsMessage)
                    .queue();
        });
    }
}
