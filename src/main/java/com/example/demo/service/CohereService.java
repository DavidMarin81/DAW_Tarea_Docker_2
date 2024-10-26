package com.example.demo.service;

import com.cohere.api.Cohere;
import com.cohere.api.requests.ChatRequest;
import com.cohere.api.types.ChatMessage;
import com.cohere.api.types.Message;
import com.cohere.api.types.NonStreamedChatResponse;
import com.example.demo.Utils.FileLoader;
import com.example.demo.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class CohereService {
    private final Cohere cohere;
    private final FileLoader fileLoader;

    private List<String> uxuiDesignChatHistory;
    private List<String> frontendChatHistory;
    private List<String> backendChatHistory;

    private List<String> chatHistory;

    @Value("${client}")
    private String client;
    @Value("${final.prompt}")
    private String finalPrompt;

    @Autowired
    public CohereService(@Value("${cohere.api.token}") String apiKey, FileLoader promptLoader, FileLoader fileLoader, List<String> uxuiDesignChatHistory, List<String> frontendChatHistory, List<String> backendChatHistory) throws IOException {
        this.cohere = Cohere.builder().token(apiKey).clientName(client).build();
        this.fileLoader = fileLoader;
        this.uxuiDesignChatHistory = uxuiDesignChatHistory;
        this.frontendChatHistory = frontendChatHistory;
        this.backendChatHistory = backendChatHistory;
    }

    public Question getIAResponse(Question question) {

        try {
            String prompt = generatePrompt(fileLoader, question, "src/main/resources/prompts/prompts.json", "prompts");
            chatHistory = generateChatHistoryList(fileLoader, question);
            System.out.println("Lista de historia de " + question.getRole() + " = " + chatHistory.size());

            NonStreamedChatResponse response = cohere.chat(
                    ChatRequest.builder()
                            .message(prompt + finalPrompt)
                            .chatHistory(
                                    List.of(
                                            Message.user(ChatMessage.builder().message(chatHistory.get(chatHistory.size() - 5)).build()),
                                            Message.user(ChatMessage.builder().message(chatHistory.get(chatHistory.size() - 4)).build()),
                                            Message.user(ChatMessage.builder().message(chatHistory.get(chatHistory.size() - 3)).build()),
                                            Message.user(ChatMessage.builder().message(chatHistory.get(chatHistory.size() - 2)).build()),
                                            Message.user(ChatMessage.builder().message(chatHistory.get(chatHistory.size() - 1)).build())
                                    )
                            )
                            .build()
            );
            Question questionObj = splitResponse(question, response.getText());
            fillChatHistory(questionObj);

            return questionObj;

        } catch (Exception e) {
            // TODO - Recoger pregunta y comentario de la base de datos
            return question;
        }
    }

    public String getIAFeedback(String userResponse, Question question) {
        String userResponseLimited  = "";
        if(userResponse.length() > 120) {
            userResponseLimited = userResponse.substring(0, 120);
        } else {
            userResponseLimited = userResponse;
        }
        try {
            NonStreamedChatResponse response = cohere.chat(
                    ChatRequest.builder()
                            .message("Dame feedback sobre esta respuesta: " + userResponseLimited)
                            .chatHistory(
                                    List.of(
                                            Message.user(ChatMessage.builder().message("Es una respuesta para una entrevista de trabajo en el sector tecnologico").build()),
                                            Message.user(ChatMessage.builder().message("Respuesta de un candidato a un puesto de trabajo para un puesto tecnologico").build()),
                                            Message.user(ChatMessage.builder().message("La finalidad es poder dar feedback al entrevistado sobre su respuesta").build())
                                    )
                            )
                            .build()
            );

            return response.getText();

        } catch (Exception e) {
            // TODO - Recoger pregunta y comentario de la base de datos
            return null;
        }
    }

    private Question splitResponse(Question questionObj, String response) {
        // Buscar el índice del primer signo de apertura '¿'
        int firstQuestionMarkIndex = response.indexOf("¿");
        // Buscar el índice del primer signo de cierre '?' después del signo '¿'
        int lastQuestionMarkIndex = response.indexOf("?", firstQuestionMarkIndex);

        if (firstQuestionMarkIndex != -1 && lastQuestionMarkIndex != -1) {
            String question = response.substring(firstQuestionMarkIndex, lastQuestionMarkIndex + 1).trim();
            String comment = response.substring(lastQuestionMarkIndex + 1).trim();

            questionObj.setQuestion(question);
            questionObj.setComment(comment);
        }
        return questionObj;
    }



    private String generatePrompt(FileLoader fileLoader, Question question, String path, String jsonType) throws IOException {
        List<String> list = fileLoader.loadPromptsFromFile(path, jsonType);
        String prompt = fileLoader.getRandomPrompt(list);
        String formattedPrompt = prompt
                .replace("{role}", question.getRole())
                .replace("{experience}", question.getExperience())
                .replace("{tematica}", question.getTheme());
        System.out.println(formattedPrompt);
        return formattedPrompt;
    }

    private List<String> generateChatHistoryList(FileLoader fileLoader, Question question) throws IOException {
        switch(question.getRole()) {
            case "diseñador ux/ui":
                if(uxuiDesignChatHistory.isEmpty()){
                    return uxuiDesignChatHistory = fileLoader.loadPromptsFromFile("src/main/resources/questions/UXUIDesignQuestions.json", "ux/ui design");
                } else {
                    return uxuiDesignChatHistory;
                }
            case "frontend":
                if (frontendChatHistory.isEmpty()) {
                    return frontendChatHistory = fileLoader.loadPromptsFromFile("src/main/resources/questions/FrontendQuestions.json", "frontend");
                } else {
                    return frontendChatHistory;
                }
            case "backend":
                if (backendChatHistory.isEmpty()) {
                    return backendChatHistory = fileLoader.loadPromptsFromFile("src/main/resources/questions/BackendQuestions.json", "backend");
                } else {
                    return backendChatHistory;
                }
        }
        // TODO -> Revisar este return
        return List.of();
    }

    private void fillChatHistory(Question question) {
        switch(question.getRole()) {
            case "diseñador ux/ui":
                uxuiDesignChatHistory.add(question.getQuestion());
                break;
            case "frontend":
                frontendChatHistory.add(question.getQuestion());
                break;
            case "backend":
                backendChatHistory.add(question.getQuestion());
                break;
        }
    }


}
