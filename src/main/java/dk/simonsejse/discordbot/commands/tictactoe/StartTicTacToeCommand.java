package dk.simonsejse.discordbot.commands.tictactoe;

import dk.simonsejse.discordbot.button.ButtonID;
import dk.simonsejse.discordbot.commands.Command;
import dk.simonsejse.discordbot.commands.CommandPerform;
import dk.simonsejse.discordbot.exceptions.CommandException;
import dk.simonsejse.discordbot.exceptions.GameChallengeAlreadySentException;
import dk.simonsejse.discordbot.games.TicTacToeManager;
import dk.simonsejse.discordbot.utility.Colors;
import dk.simonsejse.discordbot.utility.Messages;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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

        final Message build = new MessageBuilder()
                .append(Messages.BORDER_LARGE)
                .setEmbed(new EmbedBuilder()
                    .setTitle("** Udfordring **")
                    .setDescription("Du skal holde op med at se på det, du IKKE HAR, og HUSK HVAD DU KÆMPER FOR! \nHvem VIL du VÆRE FRA DETTE ØJEBLIK FREM? DET ER VÆRD AT KÆMPE FOR!")
                    .addField("Udfordreren:", event.getUser().getAsMention(), true)
                    .addField("Modstanderen:", opponentUserById.getAsMention(), true)
                    .setColor(Colors.BLUE)
                    .setTimestamp(LocalDateTime.now())
                    .setFooter("Bot Dover", "https://cdn.discordapp.com/app-icons/906719301791268904/c2642069744073d0d700d0e79a1722d8.png?size=256")
                    .build())
                .append(Messages.BORDER_LARGE)
            .build();

        event.deferReply(false)
                .queue(interactionHook -> interactionHook.sendMessage(build)
                        .queue());


        try {
            ticTacToeManager.addChallenge(event.getUser(), opponentUserById);
        } catch (GameChallengeAlreadySentException e) {
            //found out how to set empheral to trues
            event.getChannel().sendMessage(Messages.ALREADY_CHALLENGED_SOMEONE_TTT)
                    .setActionRow(
                            Button.secondary(ButtonID.ACCEPT_TIC_TAC_TOE_CHALLENGE, "Forstået"),
                            Button.danger(ButtonID.CANCEL_TIC_TAC_TOE_CHALLENGE, "Slet udfordring ")
                    ).queue();
        }
    }
}
