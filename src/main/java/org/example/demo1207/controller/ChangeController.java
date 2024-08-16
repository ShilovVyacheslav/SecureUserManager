package org.example.demo1207.controller;

import lombok.RequiredArgsConstructor;

import org.example.demo1207.dto.ChangeDto;
import org.example.demo1207.model.Change;
import org.example.demo1207.service.ChangeService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/changes")
public class ChangeController {
    private final ChangeService changeService;

    @PostMapping
    public ResponseEntity<?> createChange(@RequestBody Change change) {
        Optional<ChangeDto> changeData = changeService.createChange(change);
        return changeData.map(createdChange -> new ResponseEntity<>(createdChange, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> readAllChanges() {
        List<ChangeDto> changeList = changeService.readAllChanges();
        if (changeList == null) return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        if (changeList.isEmpty()) return new ResponseEntity<>(changeList, HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(changeList.reversed(), HttpStatus.OK);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> readChangeById(@PathVariable String id) {
        Optional<ChangeDto> changeData = changeService.readChangeById(id);
        return changeData.map(change -> new ResponseEntity<>(change, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}