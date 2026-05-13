package com.esprit.platformepediatricback.ml.common;

import java.util.*;
import java.util.stream.Collectors;

public class TFIDFCalculator {

    private List<String> vocabulary;
    private Map<String, Double> idf;
    private List<double[]> documentVectors;

    public TFIDFCalculator() {
        this.vocabulary = new ArrayList<>();
        this.idf = new HashMap<>();
        this.documentVectors = new ArrayList<>();
    }

    public void fit(List<List<String>> tokenizedDocs) {
        Map<String, Integer> df = new HashMap<>();
        for (List<String> doc : tokenizedDocs) {
            Set<String> unique = new HashSet<>(doc);
            for (String term : unique) {
                df.merge(term, 1, Integer::sum);
            }
        }
        int totalDocs = tokenizedDocs.size();
        vocabulary = new ArrayList<>(df.keySet());
        for (Map.Entry<String, Integer> entry : df.entrySet()) {
            idf.put(entry.getKey(), Math.log((double) totalDocs / (1 + entry.getValue())));
        }
        documentVectors = tokenizedDocs.stream()
                .map(doc -> TextPreprocessor.toTfIdfVector(doc, vocabulary, idf))
                .collect(Collectors.toList());
    }

    public double[] transform(List<String> tokens) {
        return TextPreprocessor.toTfIdfVector(tokens, vocabulary, idf);
    }

    public List<String> getVocabulary() {
        return vocabulary;
    }

    public Map<String, Double> getIdf() {
        return idf;
    }

    public List<double[]> getDocumentVectors() {
        return documentVectors;
    }

    public int vocabularySize() {
        return vocabulary.size();
    }
}
