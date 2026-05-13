package com.esprit.platformepediatricback.ml.common;

import java.util.*;
import java.util.stream.Collectors;

public class NaiveBayesClassifier {

    private final Set<String> classes = new LinkedHashSet<>();
    private final Map<String, Double> priors = new HashMap<>();
    private final Map<String, Map<String, Double>> wordProbs = new HashMap<>();
    private final Map<String, Double> classDocCounts = new HashMap<>();
    private final Set<String> vocabulary = new HashSet<>();
    private double totalDocs = 0;

    public void fit(Map<String, List<String>> labeledDocs) {
        classes.addAll(labeledDocs.keySet());
        Map<String, Map<String, Integer>> wordCounts = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : labeledDocs.entrySet()) {
            String label = entry.getKey();
            List<String> tokens = entry.getValue();
            classDocCounts.merge(label, (double) tokens.size(), Double::sum);
            totalDocs += tokens.size();
            vocabulary.addAll(tokens);

            wordCounts.putIfAbsent(label, new HashMap<>());
            for (String token : tokens) {
                wordCounts.get(label).merge(token, 1, Integer::sum);
            }
        }

        for (String label : classes) {
            priors.put(label, classDocCounts.get(label) / totalDocs);
            Map<String, Integer> counts = wordCounts.get(label);
            double totalWords = counts.values().stream().mapToInt(Integer::intValue).sum();
            double vocabSize = vocabulary.size();
            Map<String, Double> probs = new HashMap<>();
            for (String word : vocabulary) {
                double wordCount = counts.getOrDefault(word, 0);
                probs.put(word, (wordCount + 1) / (totalWords + vocabSize));
            }
            wordProbs.put(label, probs);
        }
    }

    public Map<String, Double> predict(List<String> tokens) {
        Map<String, Double> scores = new HashMap<>();
        for (String label : classes) {
            double logProb = Math.log(priors.get(label));
            Map<String, Double> probs = wordProbs.get(label);
            for (String token : tokens) {
                logProb += Math.log(probs.getOrDefault(token, 1.0 / (vocabulary.size() + 1)));
            }
            scores.put(label, logProb);
        }
        return scores;
    }

    public String predictClass(List<String> tokens) {
        return predict(tokens).entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("unknown");
    }

    public String getDominantClass(Map<String, Double> scores) {
        return scores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("unknown");
    }

    public double getConfidence(Map<String, Double> scores) {
        double max = scores.values().stream().mapToDouble(Double::doubleValue).max().orElse(0);
        double total = scores.values().stream().mapToDouble(Math::exp).sum();
        return total > 0 ? Math.exp(max) / total : 0;
    }

    public Set<String> getClasses() {
        return classes;
    }
}
