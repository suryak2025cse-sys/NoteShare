package com.example.noteshare.exception;

public class NoteNotFoundException extends RuntimeException {

    public NoteNotFoundException(String message) {
        super(message);
    }

    public NoteNotFoundException(Long id) {
        super("Note not found with id: " + id);
    }
}
