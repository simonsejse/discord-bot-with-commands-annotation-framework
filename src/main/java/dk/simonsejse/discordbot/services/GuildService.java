package dk.simonsejse.discordbot.services;

import dk.simonsejse.discordbot.repositories.GuildRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service //Spring AOP
public class GuildService {

    private final GuildRepository guildRepository;

    @Autowired
    public GuildService(GuildRepository guildRepository) {

        this.guildRepository = guildRepository;
    }
}
