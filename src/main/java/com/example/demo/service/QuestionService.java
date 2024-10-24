package com.example.demo.service;

import com.example.demo.Repository.QuestionRepository;
import com.example.demo.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {

    @Value("${default.role}")
    private String defaultRole;

    @Value("${default.experience}")
    private String defaultExperience;

    @Value("${default.theme}")
    private String defaultTheme;

    private final CohereService cohereService;
    private final QuestionRepository questionRepository;

    @Autowired
    public QuestionService(CohereService cohereService, QuestionRepository questionRepository) {
        this.cohereService = cohereService;
        this.questionRepository = questionRepository;
    }

    public Question showQuestion(Question question) {
        Question cohereQuestion = cohereService.getIAResponse(question);
        question.setQuestion(cohereQuestion.getQuestion());
        question.setComment(cohereQuestion.getComment());

        questionRepository.save(question);

        return question;
    }

    public Question avoidEmptyFields(Question question) {
        if(question.getRole().isEmpty()) {
            question.setRole(defaultRole);
        }
        if(question.getExperience().isEmpty()) {
            question.setExperience(defaultExperience);
        }
        if(question.getTheme().isEmpty()) {
            question.setTheme(defaultTheme);
        }
        return question;
    }
}
