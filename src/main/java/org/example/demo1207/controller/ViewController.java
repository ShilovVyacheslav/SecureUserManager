package org.example.demo1207.controller;

import lombok.RequiredArgsConstructor;
import org.example.demo1207.dto.ViewDto;
import org.example.demo1207.model.View;
import org.example.demo1207.service.ViewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/views")
public class ViewController {
    private final ViewService viewService;

    @PostMapping
    public ResponseEntity<?> createView(@RequestBody View view) {
        Optional<ViewDto> viewData = viewService.createView(view);
        return viewData.map(createdView -> new ResponseEntity<>(createdView, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> readAllViews() {
        List<ViewDto> viewList = viewService.readAllViews();
        if (viewList == null) return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        if (viewList.isEmpty()) return new ResponseEntity<>(viewList, HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(viewList.reversed(), HttpStatus.OK);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> readViewById(@PathVariable String id) {
        Optional<ViewDto> viewData = viewService.readViewById(id);
        return viewData.map(view -> new ResponseEntity<>(view, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
