package org.example.demo1207.controller;

import lombok.RequiredArgsConstructor;

import org.example.demo1207.dto.ChangeDto;
import org.example.demo1207.model.Change;
import org.example.demo1207.service.ChangeService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/change")
public class ChangeController {
    private final ChangeService changeService;

    @PostMapping
    public ResponseEntity<Change> createChange(@RequestBody Change change) {
        Optional<Change> changeData = changeService.createChange(change);
        return changeData.map(createdChange -> new ResponseEntity<>(createdChange, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<?> readAllChanges() {
        List<ChangeDto> changeList = changeService.readAllChanges();
        if (changeList == null) return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        if (changeList.isEmpty()) return new ResponseEntity<>(changeList, HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(changeList.reversed(), HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<ChangeDto> readChangeById(@PathVariable String id) {
        Optional<Change> changeData = changeService.readChangeById(id);
        return changeData.map(change -> new ResponseEntity<>(changeService.mapChangeToDto(change), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<HttpStatus> deleteChangeById(@PathVariable String id) {
        return changeService.deleteChangeById(id) ?
                ResponseEntity.status(HttpStatus.NO_CONTENT).build() :
                ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

}