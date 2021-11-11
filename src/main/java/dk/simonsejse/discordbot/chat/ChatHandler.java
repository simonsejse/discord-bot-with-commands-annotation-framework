package dk.simonsejse.discordbot.chat;

import dk.simonsejse.discordbot.handlers.UserCommentPointHandler;
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
        userCommentPointHandler.getUserCommentPointQueue().add(event.getAuthor().getIdLong());
    }
}
