package com.example.demo.Controller;

import com.example.demo.model.Question;
import com.example.demo.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    QuestionService questionService;

    @PostMapping
    public Question getQuestion(@RequestBody Question question) {
        String role = question.getRole();
        String experience = question.getExperience();
        String theme = question.getTheme();

        questionService.avoidEmptyFields(question);

        return questionService.showQuestion(question);
    }

    @PostMapping("/feedback")
    public String getIAFeedback (@RequestParam String userResponse, @RequestBody Question question) {
        return questionService.getIAFeedback(userResponse, question);
    }

}
