package dk.simonsejse.discordbot.chat;

import dk.simonsejse.discordbot.handlers.UserCommentPointHandler;
import dk.simonsejse.discordbot.models.UserIDs;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ChatHandler extends ListenerAdapter  {

    private final UserCommentPointHandler userCommentPointHandler;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        userCommentPointHandler.getUserCommentPointQueue().add(new UserIDs(event.getAuthor().getIdLong(), event.getGuild().getIdLong()));
    }
}
