package dk.simonsejse.discordbot.repositories;

import dk.simonsejse.discordbot.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsUserById(Long id);
    List<User> findTop10ByOrderByPointsDesc();
    Optional<User> getUserById(long id);
    int getUserByPoints(long id);
}
