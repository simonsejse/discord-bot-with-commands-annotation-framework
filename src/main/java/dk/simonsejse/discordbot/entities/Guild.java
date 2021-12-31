package dk.simonsejse.discordbot.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="guilds")
@Entity
public class Guild {
    @Id
    private final long guildId;

    public Guild(final long guildId){
        this.guildId = guildId;
    }

    public Guild() {
        this.guildId = 0;
    }
}
