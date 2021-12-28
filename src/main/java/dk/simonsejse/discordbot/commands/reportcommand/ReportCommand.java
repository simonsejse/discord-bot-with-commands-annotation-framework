package dk.simonsejse.discordbot.commands.reportcommand;

import dk.simonsejse.discordbot.button.ButtonID;
import dk.simonsejse.discordbot.commands.Command;
import dk.simonsejse.discordbot.commands.CommandPerform;
import dk.simonsejse.discordbot.exceptions.CommandException;
import dk.simonsejse.discordbot.exceptions.UserNotFoundException;
import dk.simonsejse.discordbot.entities.Report;
import dk.simonsejse.discordbot.models.Role;
import dk.simonsejse.discordbot.services.UserService;
import dk.simonsejse.discordbot.utility.Messages;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.Button;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Command(
        cmdName = "report",
        info = "A command for reporting people",
        cooldown = 5,
        types = {OptionType.USER, OptionType.STRING},
        isRequired = {true, false},
        parameterNames = {"bruger", "begrundelse"},
        parameterDescriptions = {"brugeren du vil rapportere!", "begrundelse for at rapportere"}
)
public class ReportCommand implements CommandPerform {

    public static final Role REQUIRED_TO_CLEAR_REPORTS = Role.ADMIN;
    public static final Role REQUIRED_TO_BAN = Role.MOD;

    private final UserService userService;
    private final Messages message;
    private final ReportListener reportListener;

    @Autowired
    public ReportCommand(Messages message, UserService userService, ReportListener reportListener) {
        this.message = message;
        this.userService = userService;
        this.reportListener = reportListener;
    }

    @Override
    public void perform(SlashCommandEvent event) throws CommandException {
        final List<OptionMapping> options = event.getOptions();
        if (options.size() == 1){
            final net.dv8tion.jda.api.entities.User reportedUserJda = options.get(0).getAsUser();
            final long reportedUserID = reportedUserJda.getIdLong();

            final List<Report> reports;
            try {
                reports = userService.getReportsByUserId(reportedUserID);
            } catch (UserNotFoundException e) {
                event.deferReply(true).queue(interactionHook -> {
                    interactionHook.sendMessage(this.message.userCreatedInDB(e.getId())).queue();
                });
                this.userService.createNewUserByID(e.getId(), event.getGuild().getIdLong());
                throw new CommandException();
            }

            if (reports.isEmpty())
                throw new CommandException("Brugeren har ingen anmeldelser!");

            final Message userReportMessage = message.getUserReportMessage(reports, reportedUserJda);
            event.deferReply(false)
                    .queue(interactionHook -> {
                        interactionHook.sendMessage(userReportMessage)
                                .addActionRow(
                                        Button.danger(ButtonID.BAN_PLAYER_ON_REPORT, "Udeluk brugeren!"),
                                        Button.danger(ButtonID.CLEAR_PLAYER_REPORTS, "Fjern anmeldelser")
                                )
                                .queue(m -> m.delete().queueAfter(40, TimeUnit.SECONDS, message -> this.reportListener.removeUserFromListen(reportedUserID)));
                    });

            this.reportListener.addUserToListen(event.getUser().getIdLong(), reportedUserJda);
        }else if (options.size() == 2){
            final net.dv8tion.jda.api.entities.User reportedUserJda = options.get(0).getAsUser();
            final long reportedUserID = reportedUserJda.getIdLong();
            final String reason = options.get(1).getAsString();

            //todo: null check
            try {
                this.userService.reportUserById(reportedUserID, reason, userService.getUserById(event.getUser().getIdLong()).get());
            } catch (UserNotFoundException e) {
                event.deferReply(true).queue(interactionHook -> {
                    interactionHook.sendMessage(this.message.userCreatedInDB(e.getId())).queue();
                });
                this.userService.createNewUserByID(e.getId(), event.getGuild().getIdLong());
                throw new CommandException();
            }

            event.reply(String.format("Du har rapporteret %s med begrundelsen %s", reportedUserJda.getAsTag(), reason)).queue();

        }
    }
}
