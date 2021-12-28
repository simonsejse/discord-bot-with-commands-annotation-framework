package dk.simonsejse.discordbot.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="guilds")
@Entity
public class Guild {

    @Id
    private final long guildId;

    private long guildOwnerId;

    public Guild(final long guildId, long guildOwnerId){
        this.guildId = guildId;
        this.guildOwnerId = guildOwnerId;
    }
}
