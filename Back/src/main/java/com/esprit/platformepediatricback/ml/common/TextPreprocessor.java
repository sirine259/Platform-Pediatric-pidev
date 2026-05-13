package com.esprit.platformepediatricback.ml.common;

import java.util.*;
import java.util.stream.Collectors;

public class TextPreprocessor {

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
        "le", "la", "les", "de", "du", "des", "un", "une", "et", "est", "sont",
        "dans", "pour", "sur", "avec", "par", "pas", "plus", "moins",
        "a", "ont", "etre", "avoir", "faire", "je", "tu", "il", "elle",
        "nous", "vous", "ils", "elles", "ce", "cette", "ces", "mon", "ton", "son",
        "mes", "tes", "ses", "notre", "votre", "leur", "nos", "vos", "leurs",
        "the", "an", "and", "or", "but", "in", "on", "at", "to", "for",
        "of", "by", "with", "from", "is", "are", "was", "were", "be", "been",
        "being", "have", "has", "had", "do", "does", "did", "will", "would",
        "could", "should", "may", "might", "shall", "can", "need", "dare",
        "this", "that", "these", "those", "i", "you", "he", "she", "it", "we",
        "they", "me", "him", "her", "us", "them", "my", "your", "his", "its",
        "our", "their"
    ));

    public static List<String> tokenize(String text) {
        if (text == null || text.isBlank()) return List.of();
        return Arrays.stream(text.toLowerCase()
                .replaceAll("[^a-zA-Z0-9\\s]", " ")
                .split("\\s+"))
                .filter(w -> w.length() > 2)
                .filter(w -> !STOP_WORDS.contains(w))
                .collect(Collectors.toList());
    }

    public static Map<String, Double> computeTf(List<String> tokens) {
        Map<String, Double> tf = new HashMap<>();
        double total = tokens.size();
        for (String token : tokens) {
            tf.merge(token, 1.0 / total, Double::sum);
        }
        return tf;
    }

    public static Map<String, Double> computeTfIdf(List<String> tokens, Map<String, Double> idf) {
        Map<String, Double> tf = computeTf(tokens);
        Map<String, Double> tfidf = new HashMap<>();
        for (Map.Entry<String, Double> entry : tf.entrySet()) {
            double idfVal = idf.getOrDefault(entry.getKey(), Math.log(2));
            tfidf.put(entry.getKey(), entry.getValue() * idfVal);
        }
        return tfidf;
    }

    public static double[] toVector(List<String> tokens, List<String> vocabulary) {
        Map<String, Double> tf = computeTf(tokens);
        double[] vector = new double[vocabulary.size()];
        for (int i = 0; i < vocabulary.size(); i++) {
            vector[i] = tf.getOrDefault(vocabulary.get(i), 0.0);
        }
        return vector;
    }

    public static double[] toTfIdfVector(List<String> tokens, List<String> vocabulary, Map<String, Double> idf) {
        double[] vector = new double[vocabulary.size()];
        Map<String, Double> tf = computeTf(tokens);
        for (int i = 0; i < vocabulary.size(); i++) {
            String term = vocabulary.get(i);
            double tfVal = tf.getOrDefault(term, 0.0);
            double idfVal = idf.getOrDefault(term, Math.log(2));
            vector[i] = tfVal * idfVal;
        }
        return vector;
    }
}