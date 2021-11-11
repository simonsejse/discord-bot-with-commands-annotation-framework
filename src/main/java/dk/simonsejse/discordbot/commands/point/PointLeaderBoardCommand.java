package dk.simonsejse.discordbot.commands.point;

import dk.simonsejse.discordbot.commands.Command;
import dk.simonsejse.discordbot.commands.CommandPerform;
import dk.simonsejse.discordbot.exceptions.CommandException;
import dk.simonsejse.discordbot.models.User;
import dk.simonsejse.discordbot.services.UserService;
import dk.simonsejse.discordbot.utility.Messages;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.temporal.ChronoUnit;
import java.util.List;


@Command(
        cmdName = "leaderboards",
        info = "Vis leaderboards over point-tavle",
        cooldown = 1,
        unit = ChronoUnit.MINUTES
)
public class PointLeaderBoardCommand implements CommandPerform {

    private final UserService userService;
    private final Messages messages;

    @Autowired
    public PointLeaderBoardCommand(final UserService userService, Messages messages){
        this.userService = userService;
        this.messages = messages;
    }

    @Override
    public void perform(SlashCommandEvent event) throws CommandException {
        final List<User> topTenPointUsers = userService.getTopTenPointUsers();
        Message topTenMessage = this.messages.getTopTenLeaderBoards(topTenPointUsers, event.getJDA());
        event.deferReply(false).queue(interactionHook -> interactionHook.sendMessage(topTenMessage).queue());
    }
}
