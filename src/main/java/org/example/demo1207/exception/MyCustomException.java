package org.example.demo1207.exception;

public class MyCustomException extends RuntimeException {
    public MyCustomException(String message) {
        super(message);
    }
}
