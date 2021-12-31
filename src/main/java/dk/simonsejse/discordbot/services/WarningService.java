package dk.simonsejse.discordbot.services;

import dk.simonsejse.discordbot.entities.AUser;
import dk.simonsejse.discordbot.entities.Warning;
import dk.simonsejse.discordbot.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class WarningService {

    private final UserService userService;

    @Autowired
    public WarningService(UserService userService) {
        this.userService = userService;
    }

    @Transactional
    public void addWarning(final long guildID, final long warnedUserID, final long warnedByUserID, String reason, LocalDateTime date) throws UserNotFoundException {
        final AUser warned = this.userService.getUserByJDAUserIDAndGuildIDFetchWarnings(warnedUserID, guildID);
        final AUser warnedBy = this.userService.getUserByID(warnedByUserID, guildID);
        warned.addWarning(new Warning(reason, date, warned, warnedBy));
    }

}
