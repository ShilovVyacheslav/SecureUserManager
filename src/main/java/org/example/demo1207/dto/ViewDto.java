package org.example.demo1207.dto;

import lombok.Data;

@Data
public class ViewDto {
    String id;
    UserDto user;
    String url;
    //yyyy-MM-dd HH:mm
    private String viewTimestamp;
}
