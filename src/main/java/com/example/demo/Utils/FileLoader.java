package com.example.demo.Utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class FileLoader {

    private List<String> list;

    public List<String> loadPromptsFromFile(String jsonFilePath, String role) throws IOException {
        list = new ArrayList<>();
        try {
            loadPrompts(jsonFilePath, role);

        } catch (IOException e) {
            // TODO
            System.err.println("Error al cargar los prompts desde: " + jsonFilePath + ". Se cargan prompts por defecto.");
            return list;
        }
        return list;
    }

    private void loadPrompts(String jsonFilePath, String role) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(new File(jsonFilePath));
        list = new ArrayList<>();

        if (rootNode.has(role)) {
            rootNode.path(role).forEach(prompt -> list.add(prompt.asText()));
        } else {
            rootNode.path("prompts").forEach(prompt -> list.add(prompt.asText()));
        }
    }

    public String getRandomPrompt(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "No hay prompts cargados.";
        }
        Random random = new Random();
        int indexRandom = random.nextInt(list.size());
        return list.get(indexRandom);
    }
}
