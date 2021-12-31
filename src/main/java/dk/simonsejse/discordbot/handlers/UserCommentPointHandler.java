package dk.simonsejse.discordbot.handlers;

import dk.simonsejse.discordbot.models.UserIDs;
import dk.simonsejse.discordbot.services.UserService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@Getter
@Setter
@EnableScheduling
public class UserCommentPointHandler {

    private ConcurrentLinkedQueue<UserIDs> userCommentPointQueue = new ConcurrentLinkedQueue<>();
    private final UserService userService;

    @Autowired
    public UserCommentPointHandler(UserService userService) {
        this.userService = userService;
    }

    @Scheduled(fixedRate = 500)
    public void continuouslyDequeUsers() {
        if (userCommentPointQueue.size() > 0){
            final UserIDs userIDs = userCommentPointQueue.poll();
            userService.incrementUserPointByUserId(userIDs.getJdaUserID(), userIDs.getGuildID());
        }
    }

}
