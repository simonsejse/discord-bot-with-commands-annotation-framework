package dk.simonsejse.discordbot.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class UserIDs {
    private long jdaUserID;
    private long guildID;
}
