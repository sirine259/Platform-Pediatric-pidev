package com.esprit.platformepediatricback.ml;

import com.esprit.platformepediatricback.Repository.ForumCommentRepository;
import com.esprit.platformepediatricback.Repository.PostRepository;
import com.esprit.platformepediatricback.Repository.UserRepository;
import com.esprit.platformepediatricback.entity.ForumComment;
import com.esprit.platformepediatricback.entity.Post;
import com.esprit.platformepediatricback.entity.User;
import com.esprit.platformepediatricback.ml.common.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ForumRecommendationService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ForumCommentRepository forumCommentRepository;

    @Autowired
    private UserRepository userRepository;

    private TFIDFCalculator tfidf;
    private List<Long> postIds;
    private List<String> postTexts;
    private boolean trained = false;

    public List<RecommendationResult.RecommendedItem> recommendPostsForUser(Long userId, int limit) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) return List.of();

        User user = userOpt.get();
        ensureTrained();

        String userText = buildUserInterestProfile(user);
        List<String> userTokens = TextPreprocessor.tokenize(userText);
        double[] userVector = tfidf.transform(userTokens);

        List<Integer> similar = CosineSimilarity.topKSimilarities(userVector, tfidf.getDocumentVectors(), limit);

        return similar.stream()
                .map(idx -> {
                    Post post = postRepository.findById(postIds.get(idx)).orElse(null);
                    if (post == null) return null;
                    double score = CosineSimilarity.compute(userVector, tfidf.getDocumentVectors().get(idx));
                    return new RecommendationResult.RecommendedItem(post.getId(),
                            post.getTitle() != null ? post.getTitle() : post.getSubject(), score);
                })
                .filter(Objects::nonNull)
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .collect(Collectors.toList());
    }

    public List<RecommendationResult.RecommendedItem> getSimilarPosts(Long postId, int limit) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) return List.of();

        Post post = postOpt.get();
        ensureTrained();

        String text = buildPostText(post);
        List<String> tokens = TextPreprocessor.tokenize(text);
        double[] queryVec = tfidf.transform(tokens);

        List<Integer> similar = CosineSimilarity.topKSimilarities(queryVec, tfidf.getDocumentVectors(), limit + 1);

        return similar.stream()
                .filter(idx -> !postIds.get(idx).equals(postId))
                .limit(limit)
                .map(idx -> {
                    Post similarPost = postRepository.findById(postIds.get(idx)).orElse(null);
                    if (similarPost == null) return null;
                    double score = CosineSimilarity.compute(queryVec, tfidf.getDocumentVectors().get(idx));
                    return new RecommendationResult.RecommendedItem(similarPost.getId(),
                            similarPost.getTitle() != null ? similarPost.getTitle() : similarPost.getSubject(), score);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<RecommendationResult.RecommendedItem> getTrendingPosts(int limit) {
        List<Post> popular = postRepository.findPopularPosts();
        return popular.stream()
                .limit(limit)
                .map(p -> {
                    double score = (p.getViewCount() != null ? p.getViewCount() * 0.1 : 0) +
                                   (p.getLikeCount() != null ? p.getLikeCount() * 2 : 0);
                    return new RecommendationResult.RecommendedItem(p.getId(),
                            p.getTitle() != null ? p.getTitle() : p.getSubject(), score);
                })
                .collect(Collectors.toList());
    }

    private String buildUserInterestProfile(User user) {
        StringBuilder profile = new StringBuilder();
        List<Post> userPosts = postRepository.findByAuthorAndIsDeletedFalse(user);
        for (Post p : userPosts) {
            profile.append(buildPostText(p)).append(" ");
        }
        List<ForumComment> comments = forumCommentRepository.findByAuthorAndIsDeletedFalse(user);
        for (ForumComment c : comments) {
            if (c.getContent() != null) profile.append(c.getContent()).append(" ");
            if (c.getPost() != null) profile.append(buildPostText(c.getPost())).append(" ");
        }
        return profile.toString();
    }

    private void ensureTrained() {
        if (!trained) {
            List<Post> posts = postRepository.findByIsDeletedFalseOrderByCreatedAtDesc();
            postIds = posts.stream().map(Post::getId).collect(Collectors.toList());
            postTexts = posts.stream().map(this::buildPostText).collect(Collectors.toList());

            List<List<String>> tokenizedDocs = postTexts.stream()
                    .map(TextPreprocessor::tokenize)
                    .collect(Collectors.toList());

            tfidf = new TFIDFCalculator();
            tfidf.fit(tokenizedDocs);
            trained = true;
        }
    }

    private String buildPostText(Post post) {
        return (post.getTitle() != null ? post.getTitle() : "") + " " +
               (post.getSubject() != null ? post.getSubject() : "") + " " +
               (post.getContent() != null ? post.getContent() : "");
    }
}
