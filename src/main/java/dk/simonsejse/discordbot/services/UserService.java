package dk.simonsejse.discordbot.services;

import dk.simonsejse.discordbot.entities.AUser;
import dk.simonsejse.discordbot.models.Role;
import dk.simonsejse.discordbot.repositories.UserRepository;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public AUser getUserByID(long jdaUserID, long guildID) {
        final Optional<AUser> aUserByJdaUserIDAndGuildID = this.userRepository.getAUserByJdaUserIDAndGuildID(jdaUserID, guildID);
        return aUserByJdaUserIDAndGuildID.orElseGet(() -> createNewUserByID(jdaUserID, guildID));
    }

    public AUser getUserByJDAUserIDAndGuildIDFetchReports(long jdaUserID, long guildID) {
        final Optional<AUser> userByJDAUserIDAndGuildIDFetchReports = this.userRepository.getUserByJDAUserIDAndGuildIDFetchReports(jdaUserID, guildID);
        return userByJDAUserIDAndGuildIDFetchReports.orElseGet(() -> createNewUserByID(jdaUserID, guildID));
    }

    public AUser getUserByJDAUserIDAndGuildIDFetchWarnings(long jdaUserID, long guildID) {
        final Optional<AUser> userByJDAUserIDAndGuildIDFetchWarnings = this.userRepository.getUserByJDAUserIDAndGuildIDFetchWarnings(jdaUserID, guildID);
        return userByJDAUserIDAndGuildIDFetchWarnings.orElseGet(() -> createNewUserByID(jdaUserID, guildID));
    }

    public AUser createNewUserByID(long jdaUserID, long guildID) {
        return userRepository.saveAndFlush(new AUser(jdaUserID, guildID));
    }

    public List<AUser> getTopTenPointUsers(long guildID) {
        return this.userRepository.findTop10ByGuildIDOrderByPointsDesc(guildID);
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

    @Transactional
    public void incrementUserPointByUserId(long id, long guildID) {
        final AUser AUser = getUserByID(id, guildID);
        AUser.incrementPoint();
    }


}
