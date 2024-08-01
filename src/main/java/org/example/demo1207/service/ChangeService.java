package org.example.demo1207.service;

import org.example.demo1207.dto.ChangeDto;
import org.example.demo1207.model.Change;

import java.util.List;
import java.util.Optional;

public interface ChangeService {

    Optional<Change> createChange(Change change);

    ChangeDto mapChangeToDto(Change change);

    List<ChangeDto> readAllChanges();

    Optional<Change> readChangeById(String id);

    boolean deleteChangeById(String id);

}