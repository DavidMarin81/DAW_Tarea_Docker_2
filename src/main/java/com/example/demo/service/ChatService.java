package com.example.demo.service;

import com.cohere.api.Cohere;
import com.cohere.api.core.CohereApiError;
import com.cohere.api.requests.ChatRequest;
import com.cohere.api.types.ChatMessage;
import com.cohere.api.types.Message;
import com.cohere.api.types.NonStreamedChatResponse;
import com.example.demo.model.Chat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class ChatService {
    private final Cohere cohere;
    private List<String> promptQuestionList;
    private List<String> chatBackendQuestionHistory = new ArrayList<>();
    private List<String> chatFrontendQuestionHistory = new ArrayList<>();
    private List<String> chatUXUIDesginQuestionHistory = new ArrayList<>();

    public ChatService(@Value("${cohere.api.token}") String apiKey) {
        this.cohere = Cohere.builder().token(apiKey).clientName("Pildoras UX").build();
        chatBackendQuestionHistory = generateBackendQuestion();
        chatFrontendQuestionHistory = generateFrontendQuestion();
        chatUXUIDesginQuestionHistory = generateUXUIDesignQuestion();
    }

    public Chat getChatResponse(Chat chat) {
        try {
            promptQuestionList = new ArrayList<>();
            String randomPrompt = generatePrompt(chat, promptQuestionList);

            List<String> questionsListByRole = generateQuestionListByRole(chat);

            NonStreamedChatResponse response = cohere.chat(
                    ChatRequest.builder()
                            .message(randomPrompt + ". Genera una pregunta entre ¿? + Feedback para el entrevistado sobre que debería responder")
                            .chatHistory(
                                    List.of(
                                            Message.user(ChatMessage.builder().message(questionsListByRole.get(questionsListByRole.size() - 5)).build()),
                                            Message.user(ChatMessage.builder().message(questionsListByRole.get(questionsListByRole.size() - 4)).build()),
                                            Message.user(ChatMessage.builder().message(questionsListByRole.get(questionsListByRole.size() - 3)).build()),
                                            Message.user(ChatMessage.builder().message(questionsListByRole.get(questionsListByRole.size() - 2)).build()),
                                            Message.user(ChatMessage.builder().message(questionsListByRole.get(questionsListByRole.size() - 1)).build())
                                    )
                            )
                            .build()
            );

            List<String> chatResponseSplitted = splitResponse(response.getText());
            chat.setPrompt(chatResponseSplitted.get(0));
            chat.setFeedback(chatResponseSplitted.get(1));
            fillChatHistory(questionsListByRole, chat);

            return chat;

        } catch (Exception e) {
            chat.setPrompt("No se pudo realizar la pregunta");
            return chat;
        }
    }

    public String getFeedback(Chat chat) {
        try {
            NonStreamedChatResponse response = cohere.chat(
                    ChatRequest.builder()
                            .message("Eres un entrevistador de it ¿Puedes darme feedback para esta respuesta?" + chat.getPrompt())
                            .build()
            );
            return response.getText();

        } catch (CohereApiError e) {
            return "Error de comunicación";
        } catch (Exception e) {
            return "Se ha producido un error";
        }
    }

    public String generatePrompt(Chat chat, List<String> promptQuestions) {
        if (promptQuestions.isEmpty()){
            promptQuestions.add("Imagina que eres un reclutador y estás realizando una entrevista para un puesto de " + chat.getRole() + " a un candidato " + chat.getExperience() + ". ¿Cuál sería una de las preguntas que le harías?");
            promptQuestions.add("Supongamos que eres un reclutador experimentado. ¿Qué pregunta plantearías a un candidato " + chat.getExperience() + " para un puesto de " + chat.getRole() + "?");
            promptQuestions.add("Como reclutador con experiencia, ¿qué pregunta le harías a un postulante " + chat.getExperience() + " para un trabajo de " + chat.getRole() + "?\"");
            promptQuestions.add("Si fueras un reclutador en una entrevista para un puesto de " + chat.getRole() + " " + chat.getExperience() + ", ¿cuál sería una pregunta que consideras clave?");
            promptQuestions.add("Eres un reclutador experimentado. ¿Cuál sería una pregunta que harías a un candidato " + chat.getExperience() + " que está solicitando un puesto en " + chat.getRole() + "?");
            promptQuestions.add("Simula que eres un reclutador que está entrevistando a un candidato " + chat.getExperience() + " para un puesto de " + chat.getRole() + ". ¿Qué pregunta le harías?");
            promptQuestions.add("Imagina que eres un reclutador buscando un desarrollador " + chat.getRole() + " " + chat.getExperience() + ". ¿Cuál sería tu primera pregunta para el candidato?");
            promptQuestions.add("Como un reclutador con experiencia, ¿qué pregunta considerarías importante para un puesto de " + chat.getRole() + " " + chat.getExperience() + "?");
            promptQuestions.add("Supongamos que estás llevando a cabo entrevistas para un puesto de " + chat.getRole() + " " + chat.getExperience() + ". ¿Qué pregunta te gustaría hacer al candidato?");
            promptQuestions.add("Eres un reclutador experimentado en la búsqueda de un desarrollador " + chat.getRole() + " " + chat.getExperience() + ". ¿Cuál sería una pregunta que le harías en la entrevista?");
        }
        return getRandomQuestion(promptQuestionList);
    }

    public List<String> generateBackendQuestion() {
        chatBackendQuestionHistory.add("¿Puedes explicar cómo gestionas la conexión a bases de datos en tus aplicaciones backend?");
        chatBackendQuestionHistory.add("¿Qué patrones de diseño utilizas en tus proyectos backend y por qué son importantes?");
        chatBackendQuestionHistory.add("Descríbeme un momento en el que tuviste que optimizar el rendimiento de una API. ¿Qué pasos seguiste?");
        chatBackendQuestionHistory.add("¿Cómo manejas la autenticación y autorización de usuarios en tus aplicaciones?");
        chatBackendQuestionHistory.add("¿Qué herramientas y tecnologías utilizas para realizar pruebas unitarias en tu código backend?");

        return chatBackendQuestionHistory;
    }

    public List<String> generateFrontendQuestion() {
        chatFrontendQuestionHistory.add("¿Cómo aseguras la compatibilidad de tu aplicación en diferentes navegadores y dispositivos?");
        chatFrontendQuestionHistory.add("Descríbeme tu proceso para diseñar una interfaz de usuario intuitiva. ¿Qué factores consideras más importantes?");
        chatFrontendQuestionHistory.add("¿Qué herramientas utilizas para optimizar el rendimiento de tus aplicaciones web?");
        chatFrontendQuestionHistory.add("¿Cómo manejas el control de versiones y la colaboración en tus proyectos frontend?");
        chatFrontendQuestionHistory.add("¿Qué técnicas sigues para garantizar que tu código CSS sea escalable y mantenible?");

        return chatFrontendQuestionHistory;
    }

    public List<String> generateUXUIDesignQuestion() {
        ArrayList<String> questions = new ArrayList<>();
        chatUXUIDesginQuestionHistory.add("¿Cómo investigas y obtienes información sobre las necesidades de los usuarios antes de diseñar una interfaz?");
        chatUXUIDesginQuestionHistory.add("Descríbeme tu proceso para crear prototipos y wireframes. ¿Qué herramientas sueles utilizar?");
        chatUXUIDesginQuestionHistory.add("¿Cómo evalúas la usabilidad de una interfaz una vez que ha sido implementada?");
        chatUXUIDesginQuestionHistory.add("¿Qué consideraciones sigues para asegurarte de que tu diseño sea accesible para todos los usuarios?");
        chatUXUIDesginQuestionHistory.add("¿Cómo manejas los comentarios y críticas sobre tu trabajo de diseño de los equipos de desarrollo y usuarios finales?");

        return chatUXUIDesginQuestionHistory;
    }

    public List<String> generateQuestionListByRole(Chat chat) {
        switch (chat.getRole()) {
            case "backend":
                return chatBackendQuestionHistory;
            case "frontend":
                return chatFrontendQuestionHistory;
            case "diseñador ux/ui":
                return  chatUXUIDesginQuestionHistory;
            default:
                return null;
        }
    }

    public String getRandomQuestion(List<String> questionList) {
        Random random = new Random();
        int randomIndex = random.nextInt(questionList.size());
        return questionList.get(randomIndex);
    }

    public void fillChatHistory(List<String> chatHistory, Chat chat) {
        switch (chat.getRole()) {
            case "backend":
                chatBackendQuestionHistory.add(chat.getPrompt());
                break;
            case "frontend":
                chatFrontendQuestionHistory.add(chat.getPrompt());
                break;
            case "diseñador ux/ui":
                chatUXUIDesginQuestionHistory.add(chat.getPrompt());
                break;
        }
    }

    public List<String> splitResponse(String response) {
        String question = "";
        String feedback = "";

        // Buscar el índice del primer signo de apertura '¿'
        int firstQuestionMarkIndex = response.indexOf("¿");
        // Buscar el índice del primer signo de cierre '?' después del signo '¿'
        int lastQuestionMarkIndex = response.indexOf("?", firstQuestionMarkIndex);

        if (firstQuestionMarkIndex != -1 && lastQuestionMarkIndex != -1) {
            question = response.substring(firstQuestionMarkIndex, lastQuestionMarkIndex + 1).trim();
            feedback = response.substring(lastQuestionMarkIndex + 1).trim();
        } else {
            question = "No se encontró una pregunta válida.";
        }
        List<String> chatResponseSplitted = new ArrayList<>();
        chatResponseSplitted.add(question);
        chatResponseSplitted.add(feedback);
        return chatResponseSplitted;
    }

    // Only to check the backend
    public String checkApp(Chat chat) {
        NonStreamedChatResponse response = cohere.chat(
                ChatRequest.builder()
                        .message(chat.getPrompt())
                        .build()
        );
        return response.getText();
    }
}
