package com.esprit.platformepediatricback.Service;

import com.esprit.platformepediatricback.Repository.ChatMessageRepository;
import com.esprit.platformepediatricback.dto.ChatRequest;
import com.esprit.platformepediatricback.dto.ChatResponse;
import com.esprit.platformepediatricback.entity.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;

@Service
public class ChatbotService {

    private final ChatMessageRepository chatMessageRepository;
    private final RestTemplate restTemplate;

    @Value("${openai.api.key:}")
    private String openAiApiKey;

    @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}")
    private String openAiUrl;

    private static final String SYSTEM_PROMPT = "Tu es NephroBot, un assistant médical spécialisé en néphrologie pédiatrique. " +
        "Tu peux aider les parents et patients à comprendre les maladies rénales chez les enfants, les traitements (dialyse, transplantation), " +
        "la nutrition adaptée, et répondre à leurs questions de manière claire et accessible. " +
        "Rappelle toujours que tu n'es pas un médecin et qu'ils doivent consulter un professionnel de santé pour des conseils médicaux. " +
        "Réponds en français de préférence. " +
        "Contexte: Plateforme Pédiatrique - Forum de discussion pour parents d'enfants atteints de maladies rénales.";

    @Autowired
    public ChatbotService(ChatMessageRepository chatMessageRepository, RestTemplate restTemplate) {
        this.chatMessageRepository = chatMessageRepository;
        this.restTemplate = restTemplate;
    }

    public ChatResponse sendMessage(ChatRequest request) {
        try {
            if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
                return ChatResponse.error("Le message ne peut pas être vide");
            }

            String sessionId = request.getSessionId();
            if (sessionId == null || sessionId.isEmpty()) {
                sessionId = UUID.randomUUID().toString();
            }

            String response = getOpenAIResponse(request.getMessage());
            
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setUserMessage(request.getMessage());
            chatMessage.setBotResponse(response);
            chatMessage.setSessionId(sessionId);
            chatMessage.setCategory(request.getCategory() != null ? request.getCategory() : "nephrologie");
            chatMessageRepository.save(chatMessage);

            return ChatResponse.success(response, sessionId);
        } catch (Exception e) {
            return ChatResponse.error("Erreur lors de la génération de la réponse: " + e.getMessage());
        }
    }

    private String getOpenAIResponse(String userMessage) {
        if (openAiApiKey == null || openAiApiKey.trim().isEmpty()) {
            return getFallbackResponse(userMessage);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(createMessage("system", SYSTEM_PROMPT));
        messages.add(createMessage("user", userMessage));
        requestBody.put("messages", messages);
        requestBody.put("max_tokens", 500);
        requestBody.put("temperature", 0.7);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(openAiUrl, entity, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            return getFallbackResponse(userMessage);
        }
    }

    private Map<String, String> createMessage(String role, String content) {
        Map<String, String> message = new HashMap<>();
        message.put("role", role);
        message.put("content", content);
        return message;
    }

    private String getFallbackResponse(String userMessage) {
        String lowerMessage = userMessage.toLowerCase();
        
        if (lowerMessage.contains("dialyse") || lowerMessage.contains("dialysis")) {
            return "La dialyse est un traitement qui remplace la fonction des reins lorsqu'ils ne fonctionnent plus correctement. Chez les enfants, on utilise principalement la dialyse péritonéale (à domicile) ou l'hémodialyse (en centre). Le choix dépend de l'état de santé de l'enfant et de l'avis de l'équipe médicale.";
        } else if (lowerMessage.contains("transplant") || lowerMessage.contains("greffe")) {
            return "La transplantation rénale est souvent le meilleur traitement pour les enfants atteints d'insuffisance rénale terminale. Elle offre une meilleure qualité de vie que la dialyse à long terme. La greffe peut provenir d'un donneur vivant (famille) ou décédé. L'équipe de transplantation vous guidera à travers tout le processus.";
        } else if (lowerMessage.contains("nutrition") || lowerMessage.contains("régime") || lowerMessage.contains("alimentation")) {
            return "La nutrition est cruciale pour les enfants atteints de maladie rénale. Un régime spécial peut être nécessaire, limitant le sel, le potassium, ou les protéines selon le stade de la maladie. Consultez toujours un néphrologue et un nutritionniste spécialisé.";
        } else if (lowerMessage.contains("symptôme") || lowerMessage.contains("symptom")) {
            return "Les symptômes des maladies rénales chez les enfants peuvent inclure: fatigue, gonflement des jambes ou du visage, changements urinaires, perte d'appétit, et difficultés de croissance. Si vous remarquez ces symptômes, consultez un néphrologue pédiatrique.";
        } else if (lowerMessage.contains("merci") || lowerMessage.contains("thanks")) {
            return "De rien ! N'hésitez pas à poser d'autres questions sur la néphrologie pédiatrique. Si vous avez des préoccupations médicales spécifiques, pensez à consulter l'équipe médicale de votre enfant.";
        } else if (lowerMessage.contains("bonjour") || lowerMessage.contains("salut") || lowerMessage.contains("hello")) {
            return "Bonjour ! Je suis NephroBot, votre assistant en néphrologie pédiatrique. Comment puis-je vous aider aujourd'hui ? Vous pouvez me poser des questions sur les maladies rénales, la dialyse, la transplantation, ou la nutrition.";
        } else {
            return "Je suis NephroBot, spécialisé en néphrologie pédiatrique. Je peux vous aider avec des questions sur:\n- Les maladies rénales chez les enfants\n- La dialyse (péritonéale et hémodialyse)\n- La transplantation rénale\n- La nutrition adaptée\n- Les soins et le quotidien\n\nQue souhaitez-vous savoir ?";
        }
    }

    public List<ChatMessage> getChatHistory(String sessionId) {
        return chatMessageRepository.findBySessionIdOrderByTimestampAsc(sessionId);
    }

    public void clearChatHistory(String sessionId) {
        chatMessageRepository.deleteBySessionId(sessionId);
    }
}
