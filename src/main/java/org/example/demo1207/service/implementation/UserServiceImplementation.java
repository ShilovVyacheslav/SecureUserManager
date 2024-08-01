package org.example.demo1207.service.implementation;

import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.example.demo1207.dto.UserDto;
import org.example.demo1207.model.Change;
import org.example.demo1207.model.User;
import org.example.demo1207.repository.UserRepository;
import org.example.demo1207.service.ChangeService;
import org.example.demo1207.service.UserService;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;

    @Override
    public Optional<User> createUser(User user) {
        return Optional.of(userRepository.save(user));
    }

    @Override
    public List<UserDto> readAllUsers() {
        return userRepository.findAll().stream().map(User::mapUserToDto).toList();
    }

    @Override
    public Optional<User> readUserById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> updateUserById(String id, User newUser) throws IllegalAccessException {
        Optional<User> oldUserData = userRepository.findById(id);
        if (oldUserData.isEmpty()) {
            return Optional.empty();
        }

        User updatedUser = oldUserData.get();

        Field[] fields = updatedUser.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object newValue = field.get(newUser);
            if (newValue != null && !newValue.toString().isEmpty() && !newValue.toString().isBlank()) {
                field.set(updatedUser, newValue);
            }
        }

        User userObj = userRepository.save(updatedUser);
        return Optional.of(userObj);
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
        firstName = StringUtils.trimToNull(firstName);
        lastName = StringUtils.trimToNull(lastName);
        email = StringUtils.trimToNull(email);
        var userPage = userRepository.findAll(getSeachingSpecification(
                User.builder().firstName(firstName).lastName(lastName).email(email).password(null).build()), pageable);

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
    public boolean saveLog(ChangeService changeService,
                           User oldUser,
                           User newUser) throws IllegalAccessException {

        Map<String, String> oldValues = new HashMap<>(), newValues = new HashMap<>();
        Field[] fields = oldUser.getClass().getDeclaredFields();
        List<String> fieldsChanged = new ArrayList<>();
        for (Field field : fields) {
            field.setAccessible(true);
            String oldValue = field.get(oldUser).toString(), newValue = field.get(newUser).toString();
            String fieldName = field.getName();
            if (!oldValue.equals(newValue)) {
                oldValues.put(fieldName, oldValue);
                newValues.put(fieldName, newValue);
                fieldsChanged.add(fieldName);
            }
        }
        if (!fieldsChanged.isEmpty()) {
            changeService.createChange(Change.builder().id(ObjectId.get()).userId(newUser.getId())
                    .fieldsChanged(fieldsChanged).oldValues(oldValues).newValues(newValues)
                    .changeTimestamp(new Date(System.currentTimeMillis())).build());
        } else return false;

        return true;
    }

}
