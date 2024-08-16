package org.example.demo1207.service;

import org.example.demo1207.dto.ViewDto;
import org.example.demo1207.model.View;

import java.util.List;
import java.util.Optional;

public interface ViewService {

    Optional<ViewDto> createView(View view);

    ViewDto mapViewToDto(View view);

    List<ViewDto> readAllViews();

    Optional<ViewDto> readViewById(String id);

}
