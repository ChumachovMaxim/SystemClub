package org.example.studio.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // 409
public class DuplicateStudioNameException extends RuntimeException {
    public DuplicateStudioNameException() {
        super("Студия с таким именем уже существует");
    }
}