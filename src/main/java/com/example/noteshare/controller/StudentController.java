package com.example.noteshare.controller;

import com.example.noteshare.model.Student;
import com.example.noteshare.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/students")
    public String getAllStudents(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        if (!model.containsAttribute("student")) {
            model.addAttribute("student", new Student());
        }
        return "students";
    }

    @GetMapping("/students/add")
    public String showAddStudentForm(Model model) {
        return "redirect:/students";
    }

    @PostMapping("/students/add")
    public String addStudent(@Valid @ModelAttribute("student") Student student,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("students", studentService.getAllStudents());
            return "students";
        }
        studentService.addStudent(student);
        return "redirect:/students";
    }
}
