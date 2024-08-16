package org.example.demo1207.service.implementation;

import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.example.demo1207.controller.ChangeController;
import org.example.demo1207.controller.ViewController;
import org.example.demo1207.dto.UserDto;
import org.example.demo1207.model.Change;
import org.example.demo1207.model.Role;
import org.example.demo1207.model.User;
import org.example.demo1207.model.View;
import org.example.demo1207.repository.UserRepository;
import org.example.demo1207.service.UserService;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;
    private final ViewController viewController;
    private final ChangeController changeController;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Optional<User> createUser(User user) throws IllegalAccessException {
        Optional<User> userData = userRepository.findByEmail(user.getEmail());
        if (userData.isPresent()) return Optional.empty();
        User savedUser = userRepository.save(user);
        saveChange(new User(), savedUser);
        return Optional.of(savedUser);
    }

    @Override
    public List<UserDto> readAllUsers(HttpServletRequest request) {
        saveView(request);
        return userRepository.findAll().stream().filter(user -> user.getRoles().stream()
                .noneMatch(role -> role.toString().equals("ROLE_ADMIN"))).map(User::mapUserToDto).toList();
    }

    @Override
    public Optional<UserDto> readUserById(String id, HttpServletRequest request) {
        saveView(request);
        return userRepository.findById(id).map(User::mapUserToDto);
    }

    @Override
    public Optional<UserDto> readUserByEmail(String email, HttpServletRequest request) {
        saveView(request);
        return userRepository.findByEmail(email).map(User::mapUserToDto);
    }

    private Optional<UserDto> updateFields(User oldUser, User newUser) throws IllegalAccessException {
        if (oldUser == null ||
                (oldUser.getRoles().contains(Role.ROLE_ADMIN) &&
                        !oldUser.getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getName()))) {
            return Optional.empty();
        }
        User updatedUser = oldUser.clone();
        Field[] fields = updatedUser.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object newValue = field.get(newUser);
            if (newValue != null && !newValue.toString().isEmpty()
                    && !newValue.toString().isBlank() && !field.getName().equals("roles")) {
                field.set(updatedUser, field.getName().equals("password") ?
                        passwordEncoder.encode(newValue.toString()) : newValue
                );
            }
        }
        newUser = userRepository.save(updatedUser);
        saveChange(oldUser, newUser);
        return Optional.of(newUser).map(User::mapUserToDto);
    }

    @Override
    public Optional<UserDto> updateUserById(String id, User newUser) throws IllegalAccessException {
        Optional<User> oldUserData = userRepository.findById(id);
        return updateFields(oldUserData.orElse(null), newUser);
    }

    @Override
    public Optional<UserDto> updateUserByEmail(String email, User newUser) throws IllegalAccessException {
        Optional<User> oldUserData = userRepository.findByEmail(email);
        return updateFields(oldUserData.orElse(null), newUser);
    }

    @Override
    @Transactional
    public boolean deleteUserById(String id) throws IllegalAccessException {
        Optional<User> userData = userRepository.findById(id);
        if (userData.isEmpty() ||
                userData.get().getRoles().contains(Role.ROLE_ADMIN) ||
                userData.get().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getName()))
            return false;
        saveChange(userData.get(), new User());
        userRepository.deleteById(id);
        return true;
    }

    @Override
    public Map<String, Object> getUsersPage(String firstName,
                                            String lastName,
                                            String email,
                                            Pageable pageable,
                                            HttpServletRequest request) {
        saveView(request);
        var res = new HashMap<String, Object>();
        firstName = StringUtils.trimToNull(firstName);
        lastName = StringUtils.trimToNull(lastName);
        email = StringUtils.trimToNull(email);
        var userPage = userRepository.findAll(
                getSeachingSpecification(
                        User.builder()
                                .firstname(firstName)
                                .lastname(lastName)
                                .email(email)
                                .password(null)
                                .roles(null)
                                .build()
                ),
                pageable
        );

        res.put("content", userPage.getContent());
        res.put("totalPages", userPage.getTotalPages());
        res.put("totalElements", userPage.getTotalElements());
        res.put("number", userPage.getNumber());
        res.put("size", userPage.getSize());

        return res;
    }

    private Specification<User> getSeachingSpecification(User user) {
        return ((root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();
            Field[] fields = user.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = null;
                try {
                    value = field.get(user);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                if (value != null && !value.toString().isEmpty() && !value.toString().isBlank()) {
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(field.getName())),
                            "%" + value.toString().toLowerCase() + "%"));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }

    @Override
    public void saveView(HttpServletRequest request) {
        String fullUrl = request.getRequestURL().toString();
        String queryString = request.getQueryString();
        if (queryString != null) {
            fullUrl += "?" + queryString;
        }
        Optional<User> sessionUserData = userRepository.findByEmail(
                SecurityContextHolder.getContext().getAuthentication().getName()
        );
        viewController.createView(
                View.builder()
                        .id(ObjectId.get())
                        .userId(sessionUserData.map(User::getId).orElse(null))
                        .url(fullUrl)
                        .viewTimestamp(new Date(System.currentTimeMillis()))
                        .build()
        );
    }

    @Override
    public void saveChange(User oldUser, User newUser) throws IllegalAccessException {
        Map<String, String> oldValues = new HashMap<>(), newValues = new HashMap<>();
        Field[] fields = oldUser.getClass().getDeclaredFields();
        List<String> fieldsChanged = new ArrayList<>();
        for (Field field : fields) {
            field.setAccessible(true);
            String oldValue = Objects.toString(field.get(oldUser), null);
            String newValue = Objects.toString(field.get(newUser), null);
            String fieldName = field.getName();
            if ((oldValue == null || newValue == null) || !oldValue.equals(newValue)) {
                fieldsChanged.add(fieldName);
            }
            oldValues.put(fieldName, oldValue);
            newValues.put(fieldName, newValue);
        }
        if (!fieldsChanged.isEmpty()) {
            Optional<User> sessionUserData = userRepository.findByEmail(
                    SecurityContextHolder.getContext().getAuthentication().getName()
            );
            if (sessionUserData.isEmpty()) {
                sessionUserData = Optional.of(newUser);
            }
            changeController.createChange(
                    Change.builder()
                            .id(ObjectId.get())
                            .userId(sessionUserData.map(User::getId).orElse(null))
                            .fieldsChanged(fieldsChanged)
                            .oldValues(oldValues)
                            .newValues(newValues)
                            .changeTimestamp(new Date(System.currentTimeMillis()))
                            .build());
        }
    }

}
