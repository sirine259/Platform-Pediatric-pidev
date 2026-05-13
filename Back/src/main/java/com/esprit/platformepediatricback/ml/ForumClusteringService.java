package com.esprit.platformepediatricback.ml;

import com.esprit.platformepediatricback.Repository.PostRepository;
import com.esprit.platformepediatricback.entity.Post;
import com.esprit.platformepediatricback.ml.common.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ForumClusteringService {

    @Autowired
    private PostRepository postRepository;

    private static final int NUM_CLUSTERS = 5;
    private TFIDFCalculator tfidf;
    private KMeansClustering kMeans;
    private List<Long> postIds;
    private List<String> postTitles;
    private boolean trained = false;

    public Map<Integer, ClusterResult> clusterForumPosts() {
        List<Post> posts = postRepository.findAllForumPostsOrderByDate();
        if (posts.size() < NUM_CLUSTERS) {
            Map<Integer, ClusterResult> result = new HashMap<>();
            ClusterResult cr = new ClusterResult(0, posts.stream().map(Post::getId).collect(Collectors.toList()),
                List.of("posts", "forum", "discussion"));
            result.put(0, cr);
            return result;
        }

        postIds = posts.stream().map(Post::getId).collect(Collectors.toList());
        postTitles = posts.stream().map(p -> (p.getTitle() != null ? p.getTitle() : "") + " " +
            (p.getContent() != null ? p.getContent() : "")).collect(Collectors.toList());

        List<List<String>> tokenizedDocs = postTitles.stream()
                .map(TextPreprocessor::tokenize)
                .collect(Collectors.toList());

        tfidf = new TFIDFCalculator();
        tfidf.fit(tokenizedDocs);

        List<double[]> vectors = tfidf.getDocumentVectors();

        int actualK = Math.min(NUM_CLUSTERS, posts.size());
        kMeans = new KMeansClustering(actualK);
        Map<Integer, List<Integer>> clusters = kMeans.fit(vectors);
        trained = true;

        Map<Integer, ClusterResult> results = new HashMap<>();
        for (Map.Entry<Integer, List<Integer>> entry : clusters.entrySet()) {
            List<Long> clusterPostIds = entry.getValue().stream()
                    .map(postIds::get)
                    .collect(Collectors.toList());

            List<String> clusterTexts = entry.getValue().stream()
                    .map(postTitles::get)
                    .collect(Collectors.toList());

            List<String> topTerms = extractTopTerms(clusterTexts, 5);

            ClusterResult cr = new ClusterResult(entry.getKey(), clusterPostIds, topTerms);
            results.put(entry.getKey(), cr);
        }

        return results;
    }

    public int predictCluster(Long postId) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty() || !trained) return -1;

        Post post = postOpt.get();
        String text = (post.getTitle() != null ? post.getTitle() : "") + " " +
                      (post.getContent() != null ? post.getContent() : "");
        List<String> tokens = TextPreprocessor.tokenize(text);
        double[] vector = tfidf.transform(tokens);
        return kMeans.predict(vector);
    }

    private List<String> extractTopTerms(List<String> texts, int topN) {
        Map<String, Integer> termFreq = new HashMap<>();
        for (String text : texts) {
            List<String> tokens = TextPreprocessor.tokenize(text);
            for (String token : tokens) {
                termFreq.merge(token, 1, Integer::sum);
            }
        }
        return termFreq.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public boolean isTrained() { return trained; }
}
