package org.example.demo1207;

import org.example.demo1207.exception.MyCustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class ControllerAdviser {

    @ExceptionHandler
    public ResponseEntity<?> handleException(MyCustomException exception) {
        System.out.println(exception.getMessage());
        return ResponseEntity.badRequest().body(Map.of("badRequest", exception.getMessage()));
    }


}
