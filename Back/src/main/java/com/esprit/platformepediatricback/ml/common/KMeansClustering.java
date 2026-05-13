package com.esprit.platformepediatricback.ml.common;

import org.apache.commons.math3.ml.distance.EuclideanDistance;
import java.util.*;

public class KMeansClustering {

    private final int k;
    private final int maxIterations;
    private final EuclideanDistance distance;
    private List<double[]> centroids;
    private Map<Integer, List<Integer>> clusters;

    public KMeansClustering(int k) {
        this(k, 100);
    }

    public KMeansClustering(int k, int maxIterations) {
        this.k = k;
        this.maxIterations = maxIterations;
        this.distance = new EuclideanDistance();
    }

    public Map<Integer, List<Integer>> fit(List<double[]> data) {
        if (data.isEmpty() || data.get(0).length == 0) {
            clusters = new HashMap<>();
            return clusters;
        }

        int n = data.size();
        int dim = data.get(0).length;

        centroids = initializeCentroids(data, n, dim);

        for (int iter = 0; iter < maxIterations; iter++) {
            clusters = new HashMap<>();
            for (int i = 0; i < k; i++) clusters.put(i, new ArrayList<>());

            for (int i = 0; i < n; i++) {
                int nearest = nearestCentroid(data.get(i));
                clusters.get(nearest).add(i);
            }

            List<double[]> newCentroids = new ArrayList<>();
            boolean converged = true;

            for (int i = 0; i < k; i++) {
                List<Integer> clusterPoints = clusters.get(i);
                if (clusterPoints.isEmpty()) {
                    newCentroids.add(centroids.get(i));
                    continue;
                }
                double[] newCent = new double[dim];
                for (int idx : clusterPoints) {
                    double[] point = data.get(idx);
                    for (int j = 0; j < dim; j++) {
                        newCent[j] += point[j];
                    }
                }
                for (int j = 0; j < dim; j++) {
                    newCent[j] /= clusterPoints.size();
                }
                newCentroids.add(newCent);
                if (distance.compute(centroids.get(i), newCent) > 1e-6) {
                    converged = false;
                }
            }

            centroids = newCentroids;
            if (converged) break;
        }

        return clusters;
    }

    public int predict(double[] point) {
        return nearestCentroid(point);
    }

    public List<double[]> getCentroids() {
        return centroids;
    }

    public Map<Integer, List<Integer>> getClusters() {
        return clusters;
    }

    private int nearestCentroid(double[] point) {
        int nearest = 0;
        double minDist = distance.compute(point, centroids.get(0));
        for (int i = 1; i < centroids.size(); i++) {
            double dist = distance.compute(point, centroids.get(i));
            if (dist < minDist) {
                minDist = dist;
                nearest = i;
            }
        }
        return nearest;
    }

    private List<double[]> initializeCentroids(List<double[]> data, int n, int dim) {
        List<double[]> initial = new ArrayList<>();
        Random rand = new Random(42);
        Set<Integer> used = new HashSet<>();
        while (initial.size() < k && used.size() < n) {
            int idx = rand.nextInt(n);
            if (used.add(idx)) {
                initial.add(data.get(idx).clone());
            }
        }
        while (initial.size() < k) {
            double[] randomCent = new double[dim];
            for (int j = 0; j < dim; j++) {
                double min = Double.MAX_VALUE, max = -Double.MAX_VALUE;
                for (double[] d : data) {
                    if (d[j] < min) min = d[j];
                    if (d[j] > max) max = d[j];
                }
                randomCent[j] = min + rand.nextDouble() * (max - min);
            }
            initial.add(randomCent);
        }
        return initial;
    }
}
