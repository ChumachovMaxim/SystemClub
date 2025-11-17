package org.example.studio.exception;

public class FileTooLargeException extends RuntimeException {
    public FileTooLargeException() {
        super("File size exceeds 10MB");
    }
}