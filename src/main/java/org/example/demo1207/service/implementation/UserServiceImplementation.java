package org.example.demo1207.service.implementation;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.demo1207.model.User;
import org.example.demo1207.repository.UserRepository;
import org.example.demo1207.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Optional<User> createUser(User user) {
        return Optional.of(userRepository.save(user));
    }

    @Override
    public List<User> readAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> readUserById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> updateUserById(String id, User newUserData) {
        Optional<User> oldUserData = userRepository.findById(id);
        if (oldUserData.isPresent()) {
            User updatedUserData = oldUserData.get();
            updatedUserData.setEmail(newUserData.getEmail());
            updatedUserData.setFirstName(newUserData.getFirstName());
            updatedUserData.setLastName(newUserData.getLastName());
            updatedUserData.setPassword(newUserData.getPassword());

            User userObj = userRepository.save(updatedUserData);
            return Optional.of(userObj);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public boolean deleteUserById(String id) {
        if (!userRepository.existsById(id))
            return false;
        userRepository.deleteById(id);
        return true;
    }

}
