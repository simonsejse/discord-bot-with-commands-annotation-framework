package dk.simonsejse.discordbot.commands.tictactoe;

import dk.simonsejse.discordbot.button.ButtonID;
import dk.simonsejse.discordbot.commands.Command;
import dk.simonsejse.discordbot.commands.CommandPerform;
import dk.simonsejse.discordbot.exceptions.CommandException;
import dk.simonsejse.discordbot.exceptions.GameChallengeAlreadySentException;
import dk.simonsejse.discordbot.games.TicTacToeManager;
import dk.simonsejse.discordbot.utility.Messages;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;

@Command(
        cmdName = "startkrydsogbolle",
        cooldown = 4,
        unit = ChronoUnit.SECONDS,
        info = "Kommandoen starter et nyt kryds og bolle spil!",
        types = OptionType.USER,
        parameterNames = "modstanderen",
        parameterDescriptions = "Hvem din modstander skal være?",
        isRequired = true
)
@Component
public class StartTicTacToeCommand implements CommandPerform {

    private final Command commandAnnotation = getClass().getAnnotation(Command.class);

    @Autowired
    private TicTacToeManager ticTacToeManager;

    @Override
    public void perform(SlashCommandEvent event) throws CommandException {
        final JDA jda = event.getJDA();
        final OptionMapping option = event.getOption(commandAnnotation.parameterNames()[0]);
        if (option == null) throw new CommandException("Der vist et eller andet der gik galt... Prøv igen");
        final long opponentID = option.getAsLong();

        final User opponentUserById = jda.getUserById(opponentID);

        if (opponentUserById == null) throw new CommandException("Brugeren findes ikke!");

        try {
            ticTacToeManager.addChallenge(event.getUser(), opponentUserById);

            Message challengeSent = Messages.CHALLENGE_TTT(event.getUser().getAsMention(), opponentUserById.getAsMention());

            event.deferReply(false)
                    .queue(interactionHook -> interactionHook.sendMessage(challengeSent)
                            .queue());
        } catch (GameChallengeAlreadySentException e) {
            event.deferReply(true)
                    .queue(interactionHook -> {
                        interactionHook.sendMessage(Messages.ALREADY_CHALLENGED_SOMEONE_TTT)
                                .addActionRow(
                                        Button.secondary(ButtonID.REGRET_CANCEL_TTT, "Forstået"),
                                        Button.danger(ButtonID.CANCEL_TTT_CHALLENGE, "Slet udfordring ")
                                ).queue();
                    });
        }
    }
}
