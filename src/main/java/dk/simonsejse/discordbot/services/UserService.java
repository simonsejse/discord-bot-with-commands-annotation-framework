package dk.simonsejse.discordbot.services;

import dk.simonsejse.discordbot.exceptions.UserNotFoundException;
import dk.simonsejse.discordbot.handlers.UserCommentPointHandler;
import dk.simonsejse.discordbot.models.User;
import dk.simonsejse.discordbot.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public boolean doesUserExists(long id){
        return userRepository.existsUserById(id);
    }

    public void createNewUserByID(long id) {
        userRepository.save(new User(id));
    }

    public Optional<User> getUserByID(long id){
        return this.userRepository.getUserById(id);
    }


    public List<User> getTopTenPointUsers() {
        return this.userRepository.findTop10ByOrderByPointsDesc();
    }

    public int getPointsByUserId(long id){
        return this.userRepository.getUserByPoints(id);
    }

    public void updateUser(User user) throws UserNotFoundException {
        if (!this.userRepository.existsUserById(user.getId())) throw new UserNotFoundException();
        this.userRepository.save(user);
    }


    public void incrementUserPointByUserId(long id) throws UserNotFoundException {
        final User user = this.userRepository.getUserById(id).orElseThrow(UserNotFoundException::new);
        user.incrementPoint();
        updateUser(user);
    }
}
