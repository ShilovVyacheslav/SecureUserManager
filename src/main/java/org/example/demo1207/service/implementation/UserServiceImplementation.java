package org.example.demo1207.service.implementation;

import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.example.demo1207.model.User;
import org.example.demo1207.repository.UserRepository;
import org.example.demo1207.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;
    private final JaroWinklerDistance jaroWinkler = new JaroWinklerDistance();

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

            String newFirstName = newUserData.getFirstName();
            String newLastName = newUserData.getLastName();
            String newEmail = newUserData.getEmail();
            String newPassword = newUserData.getPassword();

            if (newFirstName != null && !newFirstName.isEmpty() && !newFirstName.isBlank())
                updatedUserData.setFirstName(newFirstName);
            if (newLastName != null && !newLastName.isEmpty() && !newLastName.isBlank())
                updatedUserData.setLastName(newLastName);
            if (newEmail != null && !newEmail.isEmpty() && !newEmail.isBlank())
                updatedUserData.setEmail(newEmail);
            if (newPassword != null && !newPassword.isEmpty() && !newPassword.isBlank())
                updatedUserData.setPassword(newPassword);

            User userObj = userRepository.save(updatedUserData);
            return Optional.of(userObj);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public boolean deleteUserById(String id) {
        if (!userRepository.existsById(id)) return false;
        userRepository.deleteById(id);
        return true;
    }

    @Override
    public Map<String, Object> getUsersPage(String firstName, String lastName, String email, Pageable pageable) {
        var res = new HashMap<String, Object>();
        var userPage = userRepository.findAll(getSeachingSpecification(
                firstName, lastName, email, null), pageable);

        res.put("content", userPage.getContent());
        res.put("totalPages", userPage.getTotalPages());
        res.put("totalElements", userPage.getTotalElements());
        res.put("number", userPage.getNumber());
        res.put("size", userPage.getSize());

        return res;
    }

    Specification<User> getSeachingSpecification(String firstName, String lastName, String email, String password) {
        return (root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (firstName != null && !firstName.isEmpty() && !firstName.isBlank()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%"));
            }
            if (lastName != null && !lastName.isEmpty() && !lastName.isBlank()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%"));
            }
            if (email != null && !email.isEmpty() && !email.isBlank()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
            }
            if (password != null && !password.isEmpty() && !password.isBlank()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("password")), "%" + password.toLowerCase() + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
