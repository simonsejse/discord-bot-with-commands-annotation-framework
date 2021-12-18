package dk.simonsejse.discordbot.services;

import dk.simonsejse.discordbot.exceptions.UserNotFoundException;
import dk.simonsejse.discordbot.entities.Report;
import dk.simonsejse.discordbot.models.Role;
import dk.simonsejse.discordbot.entities.User;
import dk.simonsejse.discordbot.entities.Warning;
import dk.simonsejse.discordbot.repositories.UserRepository;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> getUserById(long id) {
        return this.userRepository.getUserByUserId(id);
    }

    public Optional<User> getUserByIdFetchReports(long id) {
        return this.userRepository.getUserByIdFetchReports(id);
    }

    public Optional<User> getUserByIdFetchWarnings(long id) {
        return this.userRepository.getUserByIdFetchWarnings(id);
    }

    public void createNewUserByID(long id) {
        userRepository.save(new User(id));
    }

    public List<User> getTopTenPointUsers() {
        return this.userRepository.findTop10ByOrderByPointsDesc();
    }


    public boolean doesMemberHaveSufficientRole(Member member, Role requiredRole) {
        final Set<String> rolesBelowRequiredRole = Arrays.stream(Role.values())
                .filter(role -> role.getPriority() > requiredRole.getPriority())
                .map(Role::getRole)
                .collect(Collectors.toSet());

        rolesBelowRequiredRole.add(requiredRole.getRole());

        //If member == null -> return true since we dont want to check roles then just allow them
        //If member == null -> return false then we do want to check other expression and it will
        return member == null || member.getRoles().stream()
                .map(net.dv8tion.jda.api.entities.Role::getName)
                .anyMatch(rolesBelowRequiredRole::contains);
    }

    /**
     *
     * @param id User id
     * @throws UserNotFoundException If user ID does not exist in DB
     */
    @Transactional
    public void incrementUserPointByUserId(long id) throws UserNotFoundException {
        final User user = getUserById(id).orElseThrow(
                () -> new UserNotFoundException(id)
        );
        user.incrementPoint();
    }

    /**
     *
     * @param id User id
     * @param reason The report reason
     * @param reportedBy The username of who reported
     * @throws UserNotFoundException If user ID does not exist in DB
     */
    @Transactional
    public void reportUserById(long id, String reason, long reportedBy) throws UserNotFoundException {
        final User user = getUserByIdFetchReports(id).orElseThrow(
                () -> new UserNotFoundException(id)
        );
        final Report report = new Report(reason, LocalDateTime.now(), reportedBy, user);
        user.addReport(report);
    }

    /**
     *
     * @param userId User id
     * @throws UserNotFoundException If user ID does not exist in DB
     */
    @Transactional
    public void clearAllReportsByUserId(long userId) throws UserNotFoundException {
        final User user = getUserByIdFetchReports(userId).orElseThrow(
                () -> new UserNotFoundException(userId)
        );
        user.getReports().clear();
    }

    /**
     *
     * @param reportedUserID Reported User id
     * @return a List<Report> of the Users reports
     * @throws UserNotFoundException If user ID does not exist in DB
     */
    public List<Report> getReportsByUserId(long reportedUserID) throws UserNotFoundException {
        final User user = getUserByIdFetchReports(reportedUserID).orElseThrow(
                () -> new UserNotFoundException(reportedUserID)
        );
        return user.getReports();
    }

    @Transactional
    public void addWarning(net.dv8tion.jda.api.entities.User warnedUser, net.dv8tion.jda.api.entities.User warnedByUser, String reason, LocalDateTime date) throws UserNotFoundException{
        //TODO: Check if warning is above 3 then ban.
        final long warnedUserId = warnedUser.getIdLong();
        final User warned = getUserByIdFetchWarnings(warnedUserId).orElseThrow(
                () -> new UserNotFoundException(warnedUserId)
        );
        final long warnedByUserId = warnedByUser.getIdLong();
        final User warnedBy = getUserById(warnedByUserId).orElseThrow(
                () -> new UserNotFoundException(warnedByUserId)
        );

        warned.addWarning(new Warning(reason, date, warned, warnedBy));
    }


}
