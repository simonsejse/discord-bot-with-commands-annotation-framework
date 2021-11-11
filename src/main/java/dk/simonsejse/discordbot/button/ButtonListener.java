package dk.simonsejse.discordbot.button;

import dk.simonsejse.discordbot.exceptions.GameChallengeNotSent;
import dk.simonsejse.discordbot.games.TicTacToeManager;
import dk.simonsejse.discordbot.utility.Messages;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ButtonListener extends ListenerAdapter {

    public final TicTacToeManager ticTacToeManager;
    private final Messages messages;

    @Autowired
    public ButtonListener(final TicTacToeManager ticTacToeManager, final Messages messages){
        this.ticTacToeManager = ticTacToeManager;
        this.messages = messages;
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
                    event.deferReply(true).queue(interactionHook -> {
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
            default:
                break;
        }
    }
}
