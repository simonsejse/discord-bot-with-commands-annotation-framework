package dk.simonsejse.discordbot.button;

import dk.simonsejse.discordbot.commands.reportcommand.ReportCommand;
import dk.simonsejse.discordbot.commands.reportcommand.ReportListener;
import dk.simonsejse.discordbot.exceptions.GameChallengeNotSent;
import dk.simonsejse.discordbot.exceptions.UserNotFoundException;
import dk.simonsejse.discordbot.games.TicTacToeManager;
import dk.simonsejse.discordbot.services.UserService;
import dk.simonsejse.discordbot.utility.Messages;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ButtonListener extends ListenerAdapter {

    public final UserService userService;
    public final TicTacToeManager ticTacToeManager;
    private final Messages messages;
    private final ReportListener reportListener;

    @Autowired
    public ButtonListener(final UserService userService, final TicTacToeManager ticTacToeManager, final Messages messages, final ReportListener reportListener){
        this.userService = userService;
        this.ticTacToeManager = ticTacToeManager;
        this.messages = messages;
        this.reportListener = reportListener;
    }


    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        final String componentId = event.getComponentId();
        final User user = event.getUser();
       // final Message message = event.getMessage();
        switch (componentId){
            case ButtonID.CANCEL_TTT_CHALLENGE:
                try {
                    this.ticTacToeManager.removeChallenger(user, event);
                } catch (GameChallengeNotSent gameChallengeNotSent) {
                    Message noChallenge = this.messages.userHasNoChallengeTTT;
                    event.deferReply(true)
                            .queue(interactionHook -> {
                                interactionHook.sendMessage(noChallenge)
                                        .queue();
                            });
                }
                break;
            case ButtonID.REGRET_CANCEL_TTT:
                Message regretCancel = this.messages.userRegretCancellingTTT;
                if (this.ticTacToeManager.hasGame(user)){
                    event.deferReply(true).queue(interactionHook -> {
                       interactionHook.sendMessage(regretCancel).queue();
                    });
                }else {
                    Message noChallenge = this.messages.userHasNoChallengeTTT;
                    event.deferReply(true).queue(interactionHook -> {
                        interactionHook.sendMessage(noChallenge)
                                .queue();
                    });
                }
                break;
            case ButtonID.BAN_PLAYER_ON_REPORT:
                if (!userService.doesMemberHaveSufficientRole(event.getMember(), ReportCommand.REQUIRED_TO_BAN)){
                    event.deferReply(true).queue(interactionHook -> {
                        interactionHook.sendMessage("Du har ikke den tilstrækkelige rang for at banne vedkommende!").queue();
                    });
                }else{
                    event.deferReply(false).queue(i -> i.sendMessage("Du har banned spilleren!").queue());
                }
                break;
            case ButtonID.CLEAR_PLAYER_REPORTS:
                if (!userService.doesMemberHaveSufficientRole(event.getMember(), ReportCommand.REQUIRED_TO_CLEAR_REPORTS)){
                    event.deferReply(true).queue(interactionHook -> {
                        interactionHook.sendMessage("Du har ikke den tilstrækkelige rang til at slette alle vedkommendes anmeldelser!!").queue();
                    });
                }else{
                    final long userId = event.getUser().getIdLong();
                    if (!this.reportListener.hasUserListen(userId)){
                        event.deferReply(true).queue(iHook -> iHook.sendMessage("Der var ingen listener for dig? Prøv forfra..").queue());
                        break;
                    }
                    final User reportedUser = reportListener.retrieveUserById(userId);
                    try {
                        final long reportedUserId = reportedUser.getIdLong();
                        this.userService.clearAllReportsByUserId(reportedUserId);

                        event.deferReply(false)
                                .queue(iHook -> {
                                    iHook.sendMessage(String.format("Du har slettet historik for %s!", reportedUser.getAsTag()))
                                            .queue();
                                });
                    } catch (UserNotFoundException e) {
                        event.deferReply(true).queue(interactionHook -> {
                            interactionHook.sendMessage(this.messages.userCreatedInDB(e.getId())).queue();
                        });
                        this.userService.createNewUserByID(e.getId(), event.getGuild().getIdLong());
                    }

                }
            default:
                break;
        }
    }
}
