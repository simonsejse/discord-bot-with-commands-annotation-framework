package dk.simonsejse.discordbot.button;

import dk.simonsejse.discordbot.exceptions.GameChallengeNotSent;
import dk.simonsejse.discordbot.games.TicTacToeManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ButtonListener extends ListenerAdapter {

    @Autowired
    public TicTacToeManager ticTacToeManager;

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        final String componentId = event.getComponentId();
        switch (componentId){
            case ButtonID.CANCEL_TIC_TAC_TOE_CHALLENGE:
                final User user = event.getUser();
                final Message message = event.getMessage();
                if (message != null){
                    message.delete().queue();
                    try {
                        this.ticTacToeManager.removeChallenger(user, event);
                    } catch (GameChallengeNotSent gameChallengeNotSent) {
                        event.reply("Du kan ikke slette din udfordring, når du ikke har udfordret nogen").setEphemeral(true).queue();
                        return;
                    }
                }
                break;
            default:
                break;
        }
    }
}
