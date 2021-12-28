package dk.simonsejse.discordbot.repositories;

import dk.simonsejse.discordbot.entities.Guild;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuildRepository extends JpaRepository<Guild, Long> {
}
