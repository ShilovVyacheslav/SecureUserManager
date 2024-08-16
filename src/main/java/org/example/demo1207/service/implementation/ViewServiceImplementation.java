package org.example.demo1207.service.implementation;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.example.demo1207.dto.ViewDto;
import org.example.demo1207.model.User;
import org.example.demo1207.model.View;
import org.example.demo1207.repository.UserRepository;
import org.example.demo1207.repository.ViewRepository;
import org.example.demo1207.service.ViewService;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ViewServiceImplementation implements ViewService {
    private final ViewRepository viewRepository;
    private final UserRepository userRepository;
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Override
    public Optional<ViewDto> createView(View view) {
        return Optional.of(viewRepository.save(view)).map(this::mapViewToDto);
    }

    @Override
    public ViewDto mapViewToDto(View view) {
        ViewDto viewDto = new ViewDto();
        User user = userRepository.findById(view.getUserId()).orElse(null);
        viewDto.setId(view.getId().toString());
        if (user != null) {
            viewDto.setUser(user.mapUserToDto());
        }
        viewDto.setUrl(view.getUrl());
        if (view.getViewTimestamp() != null) {
            viewDto.setViewTimestamp(formatter.format(view.getViewTimestamp()));
        }
        return viewDto;
    }

    @Override
    public List<ViewDto> readAllViews() {
        return viewRepository.findAll().stream().map(this::mapViewToDto).toList();
    }

    @Override
    public Optional<ViewDto> readViewById(String id) {
        return viewRepository.findById(new ObjectId(id)).map(this::mapViewToDto);
    }

}
