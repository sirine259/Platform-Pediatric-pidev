package com.esprit.platformepediatricback.Controller;

import com.esprit.platformepediatricback.Service.ChatbotService;
import com.esprit.platformepediatricback.dto.ChatRequest;
import com.esprit.platformepediatricback.dto.ChatResponse;
import com.esprit.platformepediatricback.entity.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/chatbot")
@CrossOrigin(origins = "*")
public class ChatbotController {
    
    @Autowired
    private ChatbotService chatbotService;
    
    @PostMapping("/message")
    public ResponseEntity<ChatResponse> sendMessage(@RequestBody ChatRequest request) {
        ChatResponse response = chatbotService.sendMessage(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/history/{sessionId}")
    public ResponseEntity<List<ChatMessage>> getChatHistory(@PathVariable String sessionId) {
        List<ChatMessage> history = chatbotService.getChatHistory(sessionId);
        return ResponseEntity.ok(history);
    }
    
    @DeleteMapping("/history/{sessionId}")
    public ResponseEntity<Void> clearChatHistory(@PathVariable String sessionId) {
        chatbotService.clearChatHistory(sessionId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("{\"status\":\"UP\",\"service\":\"Chatbot\"}");
    }
}
