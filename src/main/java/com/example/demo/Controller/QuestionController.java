package com.example.demo.Controller;

import com.example.demo.Repository.QuestionRepository;
import com.example.demo.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private QuestionRepository questionRepository;

    @GetMapping
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    @PostMapping("/add")
    public Question addQuestion(@RequestBody Question question) {
        return questionRepository.save(question);
    }
}
