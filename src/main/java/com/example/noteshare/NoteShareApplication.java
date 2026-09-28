package com.example.noteshare;

import com.example.noteshare.model.Note;
import com.example.noteshare.model.Student;
import com.example.noteshare.model.Subject;
import com.example.noteshare.repository.NoteRepository;
import com.example.noteshare.repository.StudentRepository;
import com.example.noteshare.repository.SubjectRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class NoteShareApplication {

    public static void main(String[] args) {
        SpringApplication.run(NoteShareApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(StudentRepository studentRepository,
                                      SubjectRepository subjectRepository,
                                      NoteRepository noteRepository) {
        return args -> {
            // Seed sample data only if database is empty
            if (studentRepository.count() == 0 && subjectRepository.count() == 0) {
                // Students
                Student surya = studentRepository.save(new Student("Surya", "surya@gmail.com"));
                Student arun = studentRepository.save(new Student("Arun", "arun@gmail.com"));

                // Subjects
                Subject java = subjectRepository.save(new Subject("Java", "JAVA"));
                Subject dbms = subjectRepository.save(new Subject("DBMS", "DBMS"));
                Subject dsa = subjectRepository.save(new Subject("DSA", "DSA"));

                // Notes
                noteRepository.save(new Note("Java OOP", "Unit 2", "java-oop.pdf", 5, surya, java));
                noteRepository.save(new Note("SQL Basics", "Unit 1", "sql-basics.pdf", 4, arun, dbms));
                noteRepository.save(new Note("Stack Notes", "Unit 3", "stack.pdf", 5, surya, dsa));
            }
        };
    }
}
