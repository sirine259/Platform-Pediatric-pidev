package com.esprit.platformepediatricback.dto;

public class ChatResponse {
    private String message;
    private String sessionId;
    private String timestamp;
    private boolean success;
    private String error;
    
    public ChatResponse() {}
    
    public ChatResponse(String message, String sessionId, String timestamp, boolean success, String error) {
        this.message = message;
        this.sessionId = sessionId;
        this.timestamp = timestamp;
        this.success = success;
        this.error = error;
    }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    
    public static ChatResponse success(String message, String sessionId) {
        ChatResponse response = new ChatResponse();
        response.setMessage(message);
        response.setSessionId(sessionId);
        response.setTimestamp(java.time.LocalDateTime.now().toString());
        response.setSuccess(true);
        return response;
    }
    
    public static ChatResponse error(String error) {
        ChatResponse response = new ChatResponse();
        response.setSuccess(false);
        response.setError(error);
        return response;
    }
}
