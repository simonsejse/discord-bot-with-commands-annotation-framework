package dk.simonsejse.discordbot.repositories;

import dk.simonsejse.discordbot.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findTop10ByOrderByPointsDesc();
    
    Optional<User> getUserByUserId(long id);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.reports where u.userId = :id")
    Optional<User> getUserByIdFetchReports(long id);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.warnings where u.userId = :id")
    Optional<User> getUserByIdFetchWarnings(long id);
}
