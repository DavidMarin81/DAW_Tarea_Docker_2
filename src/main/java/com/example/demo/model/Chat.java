package com.example.demo.model;

public class Chat {
    private String role;
    private String experience;
    private String userResponse;
    private String prompt;
    private String feedback;

    public Chat() {
    }

    public Chat(String prompt, String level, String experience, String feedback) {
        this.prompt = prompt;
        this.role = level;
        this.experience = experience;
        this.feedback = feedback;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getUserResponse() {
        return userResponse;
    }

    public void setUserResponse(String userResponse) {
        this.userResponse = userResponse;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
