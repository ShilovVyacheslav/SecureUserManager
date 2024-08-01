package org.example.demo1207.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class ChangeDto {
    String id;
    UserDto user;
    List<String> fieldsChanged;
    private Map<String, String> oldValues;
    private Map<String, String> newValues;
    //yyyy-MM-dd HH:mm
    private String changeTimestamp;
}
