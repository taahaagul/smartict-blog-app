package com.taahaagul.smartictblog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class IncorrectValueException extends RuntimeException{

    public IncorrectValueException(String message) {
        super(message);
    }

}
