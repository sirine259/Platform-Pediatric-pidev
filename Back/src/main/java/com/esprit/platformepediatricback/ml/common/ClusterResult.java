package com.esprit.platformepediatricback.ml.common;

import java.util.List;
import java.util.Map;

public class ClusterResult {
    private int clusterId;
    private List<Long> itemIds;
    private List<String> topTerms;
    private int size;
    private Map<String, Double> centroid;

    public ClusterResult() {}

    public ClusterResult(int clusterId, List<Long> itemIds, List<String> topTerms) {
        this.clusterId = clusterId;
        this.itemIds = itemIds;
        this.topTerms = topTerms;
        this.size = itemIds != null ? itemIds.size() : 0;
    }

    public int getClusterId() { return clusterId; }
    public void setClusterId(int clusterId) { this.clusterId = clusterId; }
    public List<Long> getItemIds() { return itemIds; }
    public void setItemIds(List<Long> itemIds) { this.itemIds = itemIds; }
    public List<String> getTopTerms() { return topTerms; }
    public void setTopTerms(List<String> topTerms) { this.topTerms = topTerms; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public Map<String, Double> getCentroid() { return centroid; }
    public void setCentroid(Map<String, Double> centroid) { this.centroid = centroid; }
}
