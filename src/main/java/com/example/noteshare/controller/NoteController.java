package com.example.noteshare.controller;

import com.example.noteshare.model.Note;
import com.example.noteshare.service.NoteService;
import com.example.noteshare.service.StudentService;
import com.example.noteshare.service.SubjectService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
public class NoteController {

    private final NoteService noteService;
    private final StudentService studentService;
    private final SubjectService subjectService;
    private final Path uploadDir = Paths.get("uploads");

    public NoteController(NoteService noteService, StudentService studentService, SubjectService subjectService) {
        this.noteService = noteService;
        this.studentService = studentService;
        this.subjectService = subjectService;

        // Create upload directory if it does not exist
        try {
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/notes")
    public String getAllNotes(Model model) {
        model.addAttribute("notes", noteService.getAllNotes());
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("subjects", subjectService.getAllSubjects());
        if (!model.containsAttribute("note")) {
            model.addAttribute("note", new Note());
        }
        model.addAttribute("isEdit", false);
        return "notes";
    }

    @GetMapping("/notes/add")
    public String showAddNoteForm() {
        return "redirect:/notes";
    }

    @PostMapping("/notes/add")
    public String addNote(@Valid @ModelAttribute("note") Note note,
                          BindingResult result,
                          @RequestParam(value = "file", required = false) MultipartFile file,
                          Model model) {
        if (result.hasErrors()) {
            model.addAttribute("notes", noteService.getAllNotes());
            model.addAttribute("students", studentService.getAllStudents());
            model.addAttribute("subjects", subjectService.getAllSubjects());
            model.addAttribute("isEdit", false);
            return "notes";
        }

        // Handle uploaded file
        if (file != null && !file.isEmpty()) {
            try {
                String originalFilename = file.getOriginalFilename();
                String fileName = System.currentTimeMillis() + "_" + (originalFilename != null ? originalFilename : "document.pdf");
                Path targetLocation = uploadDir.resolve(fileName);
                Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                note.setFileReference(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        noteService.addNote(note);
        return "redirect:/notes";
    }

    @GetMapping("/notes/edit/{id}")
    public String showEditNoteForm(@PathVariable("id") Long id, Model model) {
        Note note = noteService.getNoteById(id);
        model.addAttribute("note", note);
        model.addAttribute("notes", noteService.getAllNotes());
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("subjects", subjectService.getAllSubjects());
        model.addAttribute("isEdit", true);
        return "notes";
    }

    @PostMapping("/notes/edit/{id}")
    public String updateNote(@PathVariable("id") Long id,
                             @Valid @ModelAttribute("note") Note note,
                             BindingResult result,
                             @RequestParam(value = "file", required = false) MultipartFile file,
                             Model model) {
        if (result.hasErrors()) {
            note.setId(id);
            model.addAttribute("notes", noteService.getAllNotes());
            model.addAttribute("students", studentService.getAllStudents());
            model.addAttribute("subjects", subjectService.getAllSubjects());
            model.addAttribute("isEdit", true);
            return "notes";
        }

        Note existingNote = noteService.getNoteById(id);

        // If a new file is uploaded, save it and update fileReference
        if (file != null && !file.isEmpty()) {
            try {
                String originalFilename = file.getOriginalFilename();
                String fileName = System.currentTimeMillis() + "_" + (originalFilename != null ? originalFilename : "document.pdf");
                Path targetLocation = uploadDir.resolve(fileName);
                Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                note.setFileReference(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Retain previous file reference if no new file is uploaded
            note.setFileReference(existingNote.getFileReference());
        }

        noteService.updateNote(id, note);
        return "redirect:/notes";
    }

    @GetMapping("/notes/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("id") Long id) {
        Note note = noteService.getNoteById(id);
        if (note.getFileReference() == null || note.getFileReference().trim().isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path filePath = uploadDir.resolve(note.getFileReference()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/notes/delete/{id}")
    public String deleteNote(@PathVariable("id") Long id) {
        Note note = noteService.getNoteById(id);
        if (note.getFileReference() != null && !note.getFileReference().isEmpty()) {
            try {
                Path filePath = uploadDir.resolve(note.getFileReference()).normalize();
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        noteService.deleteNote(id);
        return "redirect:/notes";
    }

    @GetMapping("/notes/search")
    public String searchNotes(@RequestParam(value = "subjectId", required = false) Long subjectId,
                              @RequestParam(value = "unit", required = false) String unit,
                              Model model) {
        List<Note> searchResults;

        if (subjectId != null) {
            searchResults = noteService.searchBySubject(subjectId);
            model.addAttribute("selectedSubjectId", subjectId);
        } else if (unit != null && !unit.trim().isEmpty()) {
            searchResults = noteService.searchByUnit(unit.trim());
            model.addAttribute("searchedUnit", unit.trim());
        } else {
            searchResults = noteService.getAllNotes();
        }

        model.addAttribute("notes", searchResults);
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("subjects", subjectService.getAllSubjects());
        model.addAttribute("note", new Note());
        model.addAttribute("isEdit", false);
        model.addAttribute("isSearch", true);
        return "notes";
    }
}
