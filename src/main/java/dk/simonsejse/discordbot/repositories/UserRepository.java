package dk.simonsejse.discordbot.repositories;

import dk.simonsejse.discordbot.entities.AUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AUser, Long> {
    //List<AUser> findTop10ByGuildIDAndOrderByPointsDesc(long guildID);

    List<AUser> findTop10ByGuildIDOrderByPointsDesc(long guildID);

    Optional<AUser> getAUserByJdaUserIDAndGuildID(long jdaUserID, long guildID);

    @Query("SELECT u FROM AUser u LEFT JOIN FETCH u.reports where u.jdaUserID = :jdaUserID AND u.guildID = :guildID")
    Optional<AUser> getUserByJDAUserIDAndGuildIDFetchReports(long jdaUserID, long guildID);

    @Query("SELECT u FROM AUser u LEFT JOIN FETCH u.warnings where u.jdaUserID = :jdaUserID AND u.guildID = :guildID")
    Optional<AUser> getUserByJDAUserIDAndGuildIDFetchWarnings(long jdaUserID, long guildID);

}
