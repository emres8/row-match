package com.emres.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class BadRequestException extends RuntimeException {
    private String statement;


    public BadRequestException(String statement) {
        super(statement);
        this.statement = statement;

    }

}
