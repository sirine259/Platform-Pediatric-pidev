package com.esprit.platformepediatricback.ml.common;

import java.util.List;
import java.util.Map;

public class RecommendationResult {
    private String recommendationType;
    private Long targetItemId;
    private String targetItemName;
    private double relevanceScore;
    private String reason;
    private List<RecommendedItem> items;
    private Map<String, Object> metadata;

    public RecommendationResult() {}

    public String getRecommendationType() { return recommendationType; }
    public void setRecommendationType(String recommendationType) { this.recommendationType = recommendationType; }
    public Long getTargetItemId() { return targetItemId; }
    public void setTargetItemId(Long targetItemId) { this.targetItemId = targetItemId; }
    public String getTargetItemName() { return targetItemName; }
    public void setTargetItemName(String targetItemName) { this.targetItemName = targetItemName; }
    public double getRelevanceScore() { return relevanceScore; }
    public void setRelevanceScore(double relevanceScore) { this.relevanceScore = relevanceScore; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public List<RecommendedItem> getItems() { return items; }
    public void setItems(List<RecommendedItem> items) { this.items = items; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    public static class RecommendedItem {
        private Long id;
        private String name;
        private double score;
        private String reason;

        public RecommendedItem() {}

        public RecommendedItem(Long id, String name, double score) {
            this.id = id;
            this.name = name;
            this.score = score;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public double getScore() { return score; }
        public void setScore(double score) { this.score = score; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
}
