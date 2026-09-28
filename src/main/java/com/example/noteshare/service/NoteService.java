package com.example.noteshare.service;

import com.example.noteshare.exception.NoteNotFoundException;
import com.example.noteshare.model.Note;
import com.example.noteshare.repository.NoteRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public Note addNote(Note note) {
        return noteRepository.save(note);
    }

    public List<Note> getAllNotes() {
        return noteRepository.findAll();
    }

    public Note getNoteById(Long id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException(id));
    }

    public Note updateNote(Long id, Note noteDetails) {
        Note existingNote = getNoteById(id);
        existingNote.setTitle(noteDetails.getTitle());
        existingNote.setUnit(noteDetails.getUnit());
        existingNote.setFileReference(noteDetails.getFileReference());
        existingNote.setRating(noteDetails.getRating());
        if (noteDetails.getUploadedBy() != null) {
            existingNote.setUploadedBy(noteDetails.getUploadedBy());
        }
        if (noteDetails.getSubject() != null) {
            existingNote.setSubject(noteDetails.getSubject());
        }
        return noteRepository.save(existingNote);
    }

    public void deleteNote(Long id) {
        Note existingNote = getNoteById(id);
        noteRepository.delete(existingNote);
    }

    public List<Note> searchBySubject(Long subjectId) {
        return noteRepository.findBySubjectId(subjectId);
    }

    public List<Note> searchByUnit(String unit) {
        return noteRepository.findByUnit(unit);
    }
}
