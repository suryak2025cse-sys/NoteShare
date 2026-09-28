package com.example.noteshare.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "NOTE")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Unit is required")
    private String unit;

    private String fileReference;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private int rating = 1;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student uploadedBy;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    
    public Note() {
    }

    
    public Note(String title, String unit, String fileReference, int rating, Student uploadedBy, Subject subject) {
        this.title = title;
        this.unit = unit;
        this.fileReference = fileReference;
        this.rating = rating;
        this.uploadedBy = uploadedBy;
        this.subject = subject;
    }

    public Note(Long id, String title, String unit, String fileReference, int rating, Student uploadedBy, Subject subject) {
        this.id = id;
        this.title = title;
        this.unit = unit;
        this.fileReference = fileReference;
        this.rating = rating;
        this.uploadedBy = uploadedBy;
        this.subject = subject;
    }

   
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getFileReference() {
        return fileReference;
    }

    public void setFileReference(String fileReference) {
        this.fileReference = fileReference;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Student getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(Student uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }
}
