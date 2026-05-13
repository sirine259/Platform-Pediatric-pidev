package com.esprit.platformepediatricback.ml.common;

import java.util.*;
import java.util.stream.Collectors;

public class CosineSimilarity {

    public static double compute(double[] vecA, double[] vecB) {
        if (vecA.length != vecB.length) return 0;
        double dotProduct = 0, normA = 0, normB = 0;
        for (int i = 0; i < vecA.length; i++) {
            dotProduct += vecA[i] * vecB[i];
            normA += vecA[i] * vecA[i];
            normB += vecB[i] * vecB[i];
        }
        if (normA == 0 || normB == 0) return 0;
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    public static double compute(Map<String, Double> vecA, Map<String, Double> vecB) {
        Set<String> allKeys = new HashSet<>(vecA.keySet());
        allKeys.addAll(vecB.keySet());
        double dotProduct = 0, normA = 0, normB = 0;
        for (String key : allKeys) {
            double a = vecA.getOrDefault(key, 0.0);
            double b = vecB.getOrDefault(key, 0.0);
            dotProduct += a * b;
            normA += a * a;
            normB += b * b;
        }
        if (normA == 0 || normB == 0) return 0;
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    public static List<Integer> topKSimilarities(double[] queryVec, List<double[]> documentVectors, int k) {
        List<Map.Entry<Integer, Double>> similarities = new ArrayList<>();
        for (int i = 0; i < documentVectors.size(); i++) {
            double sim = compute(queryVec, documentVectors.get(i));
            similarities.add(Map.entry(i, sim));
        }
        similarities.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        return similarities.stream()
                .limit(k)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
