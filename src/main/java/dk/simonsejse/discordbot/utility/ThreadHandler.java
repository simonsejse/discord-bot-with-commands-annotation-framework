package dk.simonsejse.discordbot.utility;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public enum ThreadHandler {
    SCHEDULED_SAVE_USERS_TO_DB(Executors.newSingleThreadScheduledExecutor());

    public ExecutorService thread;
    ThreadHandler(ExecutorService thread) {
        this.thread = thread;

    }
}
