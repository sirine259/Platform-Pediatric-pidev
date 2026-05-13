package com.esprit.platformepediatricback.ml;

import com.esprit.platformepediatricback.Repository.PostRepository;
import com.esprit.platformepediatricback.entity.Post;
import com.esprit.platformepediatricback.ml.common.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ForumClassificationService {

    @Autowired
    private PostRepository postRepository;

    private NaiveBayesClassifier classifier;
    private boolean trained = false;

    private static final List<String> CATEGORIES = List.of(
        "MEDICAL_QUESTION", "EXPERIENCE_SHARING", "SUPPORT",
        "INFORMATION", "DISCUSSION", "OTHER"
    );

    public ClassificationResult classifyPost(Long postId) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) {
            return new ClassificationResult("UNKNOWN", Map.of("UNKNOWN", 1.0), 0.0);
        }

        Post post = postOpt.get();
        String text = buildPostText(post);
        List<String> tokens = TextPreprocessor.tokenize(text);

        if (!trained) {
            trainClassifier();
        }

        Map<String, Double> scores = classifier.predict(tokens);
        String predictedClass = classifier.getDominantClass(scores);
        double confidence = classifier.getConfidence(scores);

        ClassificationResult result = new ClassificationResult(predictedClass, scores, confidence);
        result.setDetails("Post classified using Naive Bayes on title + content");
        return result;
    }

    public ClassificationResult classifyText(String title, String content) {
        String text = (title != null ? title : "") + " " + (content != null ? content : "");
        List<String> tokens = TextPreprocessor.tokenize(text);

        if (!trained) {
            trainClassifier();
        }

        Map<String, Double> scores = classifier.predict(tokens);
        String predictedClass = classifier.getDominantClass(scores);
        double confidence = classifier.getConfidence(scores);

        return new ClassificationResult(predictedClass, scores, confidence);
    }

    private void trainClassifier() {
        List<Post> posts = postRepository.findByIsDeletedFalseOrderByCreatedAtDesc();
        Map<String, List<String>> labeledDocs = new HashMap<>();

        for (String cat : CATEGORIES) {
            labeledDocs.put(cat, new ArrayList<>());
        }

        for (Post post : posts) {
            String category = assignCategory(post);
            String text = buildPostText(post);
            List<String> tokens = TextPreprocessor.tokenize(text);
            labeledDocs.get(category).addAll(tokens);
        }

        classifier = new NaiveBayesClassifier();
        classifier.fit(labeledDocs);
        trained = true;
    }

    private String assignCategory(Post post) {
        String text = buildPostText(post).toLowerCase();

        if (text.contains("?") || text.contains("quel") || text.contains("comment") ||
            text.contains("pourquoi") || text.contains("est-ce")) {
            return "MEDICAL_QUESTION";
        }
        if (text.contains("experience") || text.contains("mon histoire") ||
            text.contains("j'ai vcu") || text.contains("tmoignage")) {
            return "EXPERIENCE_SHARING";
        }
        if (text.contains("soutien") || text.contains("support") ||
            text.contains("encouragement") || text.contains("aide")) {
            return "SUPPORT";
        }
        if (text.contains("information") || text.contains("actualit") ||
            text.contains("nouveau") || text.contains("tude")) {
            return "INFORMATION";
        }
        if (post.getPostType() == Post.PostType.FORUM) {
            return "DISCUSSION";
        }
        return "OTHER";
    }

    private String buildPostText(Post post) {
        return (post.getTitle() != null ? post.getTitle() : "") + " " +
               (post.getSubject() != null ? post.getSubject() : "") + " " +
               (post.getContent() != null ? post.getContent() : "");
    }

    public boolean isTrained() { return trained; }
    public List<String> getCategories() { return CATEGORIES; }
}
