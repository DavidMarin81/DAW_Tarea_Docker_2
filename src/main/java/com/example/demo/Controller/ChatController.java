package com.example.demo.Controller;

import com.example.demo.Repository.QuestionRepository;
import com.example.demo.model.Chat;
import com.example.demo.model.Question;
import com.example.demo.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    private ChatService chatService;

    @Autowired
    QuestionRepository questionRepository;

    @PostMapping("/ask")
    public Chat ask(@RequestBody Chat chat) {

        Chat response = chatService.getChatResponse(chat);

        //Convertir el chat en question y guardarla en la DDBB
        Question question = new Question();
        question.setQuestion(chat.getPrompt());
        question.setComment(chat.getFeedback());
        question.setRole(chat.getRole());
        question.setExperience(chat.getExperience());

        questionRepository.save(question);


        return chatService.getChatResponse(chat);
    }

    @PostMapping("/feedback")
    public String getFeedback(@RequestBody Chat chat) {
        return chatService.getFeedback(chat);
    }

    // Only to check the backend
    @PostMapping("/check")
    public String checkApp(@RequestBody Chat chat) {
        return chatService.checkApp(chat);
    }
}
