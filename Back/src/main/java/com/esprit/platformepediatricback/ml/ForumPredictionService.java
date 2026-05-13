package com.esprit.platformepediatricback.ml;

import com.esprit.platformepediatricback.Repository.PostRepository;
import com.esprit.platformepediatricback.entity.Post;
import com.esprit.platformepediatricback.ml.common.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class ForumPredictionService {

    @Autowired
    private PostRepository postRepository;

    private LinearRegression viewRegression;
    private LinearRegression likeRegression;
    private boolean trained = false;

    public PredictionResult predictPopularity(Long postId) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) {
            return new PredictionResult("POPULARITY", 0, 0, "score");
        }

        Post post = postOpt.get();
        double predictedViews = predictViews(post);
        double predictedLikes = predictLikes(post);
        double popularityScore = predictedViews * 0.1 + predictedLikes * 3.0;

        PredictionResult result = new PredictionResult("POPULARITY", popularityScore,
                calculateConfidence(post), "score");
        Map<String, Object> details = new HashMap<>();
        details.put("predictedViews", predictedViews);
        details.put("predictedLikes", predictedLikes);
        details.put("currentViews", post.getViewCount());
        details.put("currentLikes", post.getLikeCount());
        details.put("trending", popularityScore > 5.0);
        result.setDetails(details);
        return result;
    }

    public PredictionResult predictEngagement(Long postId) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) {
            return new PredictionResult("ENGAGEMENT", 0, 0, "comments");
        }

        Post post = postOpt.get();
        double predictedComments = predictCommentCount(post);
        double engagementScore = predictedComments * 2.0;

        PredictionResult result = new PredictionResult("ENGAGEMENT", engagementScore,
                0.7, "score");
        Map<String, Object> details = new HashMap<>();
        details.put("predictedComments", predictedComments);
        details.put("currentComments", post.getCommentCount());
        details.put("engagementLevel", engagementScore > 10 ? "HIGH" :
                                      engagementScore > 5 ? "MEDIUM" : "LOW");
        result.setDetails(details);
        return result;
    }

    public PredictionResult predictTrendPotential(Long postId) {
        PredictionResult popularity = predictPopularity(postId);
        PredictionResult engagement = predictEngagement(postId);

        double trendScore = (popularity.getPredictedValue() * 0.6 +
                            engagement.getPredictedValue() * 0.4);
        double confidence = (popularity.getConfidence() + engagement.getConfidence()) / 2;

        PredictionResult result = new PredictionResult("TREND_POTENTIAL", trendScore,
                confidence, "score");
        Map<String, Object> details = new HashMap<>();
        details.put("willBeTrending", trendScore > 5.0);
        details.put("peakTime", estimatePeakTime());
        result.setDetails(details);
        return result;
    }

    private double predictViews(Post post) {
        if (!trained) train();
        if (post.getViewCount() == null) return 0;
        double[] input = new double[]{post.getViewCount()};
        return viewRegression.predict(input);
    }

    private double predictLikes(Post post) {
        if (!trained) train();
        if (post.getLikeCount() == null) return 0;
        double[] input = new double[]{post.getLikeCount()};
        return likeRegression.predict(input);
    }

    private double predictCommentCount(Post post) {
        long commentCount = post.getCommentCount();
        double hoursSinceCreation = 0;
        if (post.getCreatedAt() != null) {
            hoursSinceCreation = ChronoUnit.HOURS.between(post.getCreatedAt(), LocalDateTime.now());
        }
        double baseRate = commentCount > 0 ? (double) commentCount / Math.max(1, hoursSinceCreation) : 0.1;
        return commentCount + baseRate * 24;
    }

    private double calculateConfidence(Post post) {
        if (post.getViewCount() == null || post.getViewCount() == 0) return 0.3;
        if (post.getViewCount() > 100) return 0.85;
        if (post.getViewCount() > 50) return 0.7;
        return 0.5;
    }

    private String estimatePeakTime() {
        return "Les posts ont gnralement le plus d'engagement entre 18h et 22h";
    }

    private void train() {
        List<Post> posts = postRepository.findByIsDeletedFalseOrderByCreatedAtDesc();
        if (posts.size() < 5) {
            trained = true;
            return;
        }

        List<Post> withViews = posts.stream()
                .filter(p -> p.getViewCount() != null && p.getLikeCount() != null)
                .toList();

        if (withViews.size() >= 5) {
            double[] viewData = withViews.stream().mapToDouble(p -> (double) p.getViewCount()).toArray();
            double[] likeData = withViews.stream().mapToDouble(p -> (double) p.getLikeCount()).toArray();

            LinearRegression lr = new LinearRegression();
            lr.fit(lr.prepareUnivariate(viewData), likeData);
            likeRegression = lr;

            LinearRegression vr = new LinearRegression();
            double[] timeData = withViews.stream()
                    .mapToDouble(p -> p.getCreatedAt() != null ?
                            (double) ChronoUnit.HOURS.between(p.getCreatedAt(), LocalDateTime.now()) : 0)
                    .toArray();
            vr.fit(vr.prepareUnivariate(timeData), viewData);
            viewRegression = vr;
        }

        trained = true;
    }
}
