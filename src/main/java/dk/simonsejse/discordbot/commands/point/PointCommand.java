package dk.simonsejse.discordbot.commands.point;

import dk.simonsejse.discordbot.commands.Command;
import dk.simonsejse.discordbot.commands.CommandPerform;
import dk.simonsejse.discordbot.exceptions.CommandException;
import dk.simonsejse.discordbot.services.UserService;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@Command(
        cmdName = "points",
        cooldown = 5,
        parameterNames = {"bruger"},
        parameterDescriptions = {"Hvilken persons point du gerne vil tjekke"},
        info = "Tjek hvor mange point du eller en anden har!",
        types = {OptionType.USER},
        isRequired = {false}
)
@AllArgsConstructor
public class PointCommand implements CommandPerform {

    private final UserService userService;

    @Override
    public void perform(SlashCommandEvent event) throws CommandException {
        if (event.getGuild() == null) throw new CommandException("Dit guild kunne ikke blive fundet!");

        long jdaUserID = event.getUser().getIdLong();
        final long guildID = event.getGuild().getIdLong();
        long points = this.userService.getUserByID(jdaUserID, guildID).getPoints();

        event.deferReply().queue(interactionHook -> {
            interactionHook.sendMessage(String.valueOf(points))
                    .queue();
        });
    }
}
