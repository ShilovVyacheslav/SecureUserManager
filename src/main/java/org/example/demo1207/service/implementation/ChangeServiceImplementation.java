package org.example.demo1207.service.implementation;

import lombok.RequiredArgsConstructor;

import org.bson.types.ObjectId;
import org.example.demo1207.dto.ChangeDto;
import org.example.demo1207.model.Change;
import org.example.demo1207.model.User;
import org.example.demo1207.repository.ChangeRepository;
import org.example.demo1207.repository.UserRepository;
import org.example.demo1207.service.ChangeService;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChangeServiceImplementation implements ChangeService {

    private final ChangeRepository changeRepository;
    private final UserRepository userRepository;
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Override
    public Optional<Change> createChange(Change change) {
        return Optional.of(changeRepository.save(change));
    }

    @Override
    public ChangeDto mapChangeToDto(Change change) {
        ChangeDto changeDto = new ChangeDto();
        User user = userRepository.findById(change.getUserId()).orElse(null);
        changeDto.setId(change.getId().toString());
        if (user != null) {
            changeDto.setUser(user.mapUserToDto());
        }
        changeDto.setFieldsChanged(change.getFieldsChanged());
        changeDto.setOldValues(change.getOldValues());
        changeDto.setNewValues(change.getNewValues());
        if (change.getChangeTimestamp() != null) {
            changeDto.setChangeTimestamp(formatter.format(change.getChangeTimestamp()));
        }
        return changeDto;
    }

    @Override
    public List<ChangeDto> readAllChanges() {
        return changeRepository.findAll().stream().map(this::mapChangeToDto).toList();
    }

    @Override
    public Optional<Change> readChangeById(String id) {
        return changeRepository.findById(new ObjectId(id));
    }

    @Override
    public boolean deleteChangeById(String id) {
        if (!changeRepository.existsById(new ObjectId(id))) return false;
        changeRepository.deleteById(new ObjectId(id));
        return true;
    }

}
