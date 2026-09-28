package com.example.noteshare.controller;

import com.example.noteshare.model.Subject;
import com.example.noteshare.service.SubjectService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping("/subjects")
    public String getAllSubjects(Model model) {
        model.addAttribute("subjects", subjectService.getAllSubjects());
        if (!model.containsAttribute("subject")) {
            model.addAttribute("subject", new Subject());
        }
        return "subjects";
    }

    @GetMapping("/subjects/add")
    public String showAddSubjectForm(Model model) {
        return "redirect:/subjects";
    }

    @PostMapping("/subjects/add")
    public String addSubject(@Valid @ModelAttribute("subject") Subject subject,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("subjects", subjectService.getAllSubjects());
            return "subjects";
        }
        subjectService.addSubject(subject);
        return "redirect:/subjects";
    }
}
