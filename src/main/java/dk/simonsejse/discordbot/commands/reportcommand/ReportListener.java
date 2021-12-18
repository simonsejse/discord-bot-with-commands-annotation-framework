package dk.simonsejse.discordbot.commands.reportcommand;

import net.dv8tion.jda.api.entities.User;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ReportListener {
    /**
     * @Key User reporting ID (long)
     * @Value User reported (JDA object)
     */
    private Map<Long, User> performReportActionListener;

    protected ReportListener(){
        this.performReportActionListener = new HashMap<>();
    }

    public void addUserToListen(Long userId, User reportedUser){
        this.performReportActionListener.compute(userId, (id, user) -> reportedUser);
    }

    public void removeUserFromListen(Long userId){
        if (this.performReportActionListener.containsKey(userId))
            this.performReportActionListener.remove(userId);
    }

    public boolean hasUserListen(long id){
        return this.performReportActionListener.containsKey(id);
    }

    public User retrieveUserById(long id){
        final User user = this.performReportActionListener.get(id);
        this.removeUserFromListen(id);
        return user;
    }

}
