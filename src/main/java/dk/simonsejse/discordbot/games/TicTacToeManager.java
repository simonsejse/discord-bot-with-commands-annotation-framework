package dk.simonsejse.discordbot.games;

import dk.simonsejse.discordbot.exceptions.GameChallengeAlreadySentException;
import dk.simonsejse.discordbot.exceptions.GameChallengeNotSent;
import dk.simonsejse.discordbot.utility.Messages;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TicTacToeManager {
    /**
     * Udfordrer: User
     *
     *  Modstanderen: User
     *
     */
    private Map<User, User> challenges = new HashMap<>();
    private final Messages messages;

    @Autowired
    public TicTacToeManager(Messages messages) {
        this.messages = messages;
    }

    public void addChallenge(User user, User opponent) throws GameChallengeAlreadySentException {
        System.out.println(challenges.containsKey(user));
        if (challenges.containsKey(user)) throw new GameChallengeAlreadySentException();
        challenges.put(user, opponent);
    }

    public void removeChallenger(User user, ButtonClickEvent e) throws GameChallengeNotSent {
        if (challenges.containsKey(user)){
            challenges.remove(user);
            Message deletedChallenge = this.messages.deletedChallengeTTT;
            e.reply(deletedChallenge).setEphemeral(true).queue();
        }else throw new GameChallengeNotSent();
    }

    public boolean hasGame(User user){
        return challenges.containsKey(user);
    }
}
