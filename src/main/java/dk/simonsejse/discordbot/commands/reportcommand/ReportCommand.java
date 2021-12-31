package dk.simonsejse.discordbot.commands.reportcommand;

import dk.simonsejse.discordbot.button.ButtonID;
import dk.simonsejse.discordbot.commands.Command;
import dk.simonsejse.discordbot.commands.CommandPerform;
import dk.simonsejse.discordbot.dtos.ReportDTO;
import dk.simonsejse.discordbot.exceptions.CommandException;
import dk.simonsejse.discordbot.entities.Report;
import dk.simonsejse.discordbot.models.Role;
import dk.simonsejse.discordbot.services.ReportService;
import dk.simonsejse.discordbot.services.UserService;
import dk.simonsejse.discordbot.utility.Messages;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
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

    private final ReportService reportService;
    private final UserService userService;
    private final Messages message;
    private final ReportListener reportListener;

    @Autowired
    public ReportCommand(Messages message, ReportService reportService, UserService userService, ReportListener reportListener) {
        this.message = message;
        this.reportService = reportService;
        this.userService = userService;
        this.reportListener = reportListener;
    }

    @Override
    public void perform(SlashCommandEvent event) throws CommandException {
        if(event.getGuild() == null) return;
        final List<OptionMapping> options = event.getOptions();
        final long guildID = event.getGuild().getIdLong();
        final long reporteeUserID = event.getUser().getIdLong();
        if (options.size() == 1){
            final net.dv8tion.jda.api.entities.User reportedUserJda = options.get(0).getAsUser();
            final long reportedUserID = reportedUserJda.getIdLong();

            List<ReportDTO> reports = reportService.getReportsByUserId(reportedUserID, guildID);

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

            this.reportListener.addUserToListen(reporteeUserID, reportedUserJda);
        }else if (options.size() == 2){
            final User reportedUser = options.get(0).getAsUser();
            final long reportedUserID = reportedUser.getIdLong();
            final String reason = options.get(1).getAsString();

            this.reportService.reportUserById(guildID, reportedUserID, reporteeUserID, reason);

            event.reply(String.format("Du har rapporteret %s med begrundelsen %s", reportedUser.getAsTag(), reason)).queue();
        }
    }
}
