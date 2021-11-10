package dk.simonsejse.discordbot.services;

import dk.simonsejse.discordbot.exceptions.UserNotFoundException;
import dk.simonsejse.discordbot.models.User;
import dk.simonsejse.discordbot.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public boolean doesUserExists(long id){
        return userRepository.existsUserById(id);
    }

    public void setupUserById(long id) {
        userRepository.save(new User(id));
    }

    //should throw usernotfoundexception
    @Transactional
    public void updatePoints(long id) throws UserNotFoundException {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException());
        userRepository.save(user);
    }


}
