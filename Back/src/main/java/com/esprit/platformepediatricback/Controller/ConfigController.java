package com.esprit.platformepediatricback.Controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/config")
@CrossOrigin(origins = "*")
public class ConfigController {
    
    @Value("${google.maps.api.key:}")
    private String googleMapsApiKey;
    
    @GetMapping("/google-maps-key")
    public ResponseEntity<Map<String, String>> getGoogleMapsKey() {
        Map<String, String> response = new HashMap<>();
        if (googleMapsApiKey != null && !googleMapsApiKey.isEmpty() && !googleMapsApiKey.equals("VOTRE_GOOGLE_MAPS_API_KEY_ICI")) {
            response.put("key", googleMapsApiKey);
            response.put("enabled", "true");
        } else {
            response.put("key", "");
            response.put("enabled", "false");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/google-maps-key")
    public ResponseEntity<Map<String, String>> setGoogleMapsKey(@RequestBody Map<String, String> payload) {
        String key = payload.get("key");
        if (key == null || key.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Clé vide"));
        }

        this.googleMapsApiKey = key.trim();

        return ResponseEntity.ok(Map.of("key", this.googleMapsApiKey, "enabled", "true"));
    }
}
