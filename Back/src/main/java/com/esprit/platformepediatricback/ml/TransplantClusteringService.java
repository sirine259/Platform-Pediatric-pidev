package com.esprit.platformepediatricback.ml;

import com.esprit.platformepediatricback.Repository.KidneyTransplantRepository;
import com.esprit.platformepediatricback.entity.KidneyTransplant;
import com.esprit.platformepediatricback.ml.common.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransplantClusteringService {

    @Autowired
    private KidneyTransplantRepository kidneyTransplantRepository;

    private static final int NUM_CLUSTERS = 4;
    private KMeansClustering kMeans;
    private List<Long> transplantIds;
    private boolean trained = false;

    public Map<Integer, ClusterResult> clusterTransplantsByRisk() {
        List<KidneyTransplant> transplants = kidneyTransplantRepository.findAll();
        if (transplants.size() < NUM_CLUSTERS) {
            Map<Integer, ClusterResult> result = new HashMap<>();
            result.put(0, new ClusterResult(0,
                transplants.stream().map(KidneyTransplant::getId).collect(Collectors.toList()),
                List.of("insuffisant de donnes")));
            return result;
        }

        transplantIds = transplants.stream().map(KidneyTransplant::getId).collect(Collectors.toList());
        List<double[]> featureVectors = transplants.stream()
                .map(this::extractFeatures)
                .collect(Collectors.toList());

        int actualK = Math.min(NUM_CLUSTERS, transplants.size());
        kMeans = new KMeansClustering(actualK);
        Map<Integer, List<Integer>> clusters = kMeans.fit(featureVectors);
        trained = true;

        Map<Integer, ClusterResult> results = new HashMap<>();
        for (Map.Entry<Integer, List<Integer>> entry : clusters.entrySet()) {
            List<Long> clusterIds = entry.getValue().stream()
                    .map(transplantIds::get)
                    .collect(Collectors.toList());

            List<KidneyTransplant> clusterTransplants = entry.getValue().stream()
                    .map(transplants::get)
                    .collect(Collectors.toList());

            String riskLabel = determineRiskLabel(clusterTransplants);
            List<String> characteristics = extractCharacteristics(clusterTransplants);

            ClusterResult cr = new ClusterResult(entry.getKey(), clusterIds, characteristics);
            cr.setCentroid(Map.of("riskProfile", (double) entry.getKey(), "label", 0.0));
            results.put(entry.getKey(), cr);
        }

        return results;
    }

    public int predictCluster(Long transplantId) {
        Optional<KidneyTransplant> opt = kidneyTransplantRepository.findById(transplantId);
        if (opt.isEmpty() || !trained) return -1;

        double[] features = extractFeatures(opt.get());
        return kMeans.predict(features);
    }

    private double[] extractFeatures(KidneyTransplant t) {
        List<Double> features = new ArrayList<>();

        features.add(t.getColdIschemiaTime() != null ? normalize(t.getColdIschemiaTime(), 0, 60) : 0.5);
        features.add(t.getWarmIschemiaTime() != null ? normalize(t.getWarmIschemiaTime(), 0, 120) : 0.5);
        features.add(t.getEstimatedBloodLoss() != null ? normalize(t.getEstimatedBloodLoss(), 0, 2000) : 0.5);
        features.add(t.getHospitalStayDuration() != null ? normalize(t.getHospitalStayDuration(), 0, 60) : 0.5);
        features.add(t.getPeakCreatinineLevel() != null ? normalize(t.getPeakCreatinineLevel(), 0, 10) : 0.5);
        features.add(t.getBaselineCreatinineLevel() != null ? normalize(t.getBaselineCreatinineLevel(), 0, 5) : 0.5);
        features.add(t.getGraftSurvivalMonths() != null ? normalize(t.getGraftSurvivalMonths(), 0, 120) : 0.5);
        features.add(t.getDelayedGraftFunction() != null && t.getDelayedGraftFunction() ? 1.0 : 0.0);
        features.add(t.getAcuteRejection() != null && t.getAcuteRejection() ? 1.0 : 0.0);
        features.add(t.getGraftFailure() != null && t.getGraftFailure() ? 1.0 : 0.0);
        features.add(t.getSurgicalSiteInfection() != null && t.getSurgicalSiteInfection() ? 1.0 : 0.0);
        features.add(t.getAnastomosisTime() != null ? normalize(t.getAnastomosisTime(), 0, 120) : 0.5);

        return features.stream().mapToDouble(Double::doubleValue).toArray();
    }

    private double normalize(double value, double min, double max) {
        if (max - min == 0) return 0.5;
        return Math.min(1.0, Math.max(0.0, (value - min) / (max - min)));
    }

    private String determineRiskLabel(List<KidneyTransplant> clusterTransplants) {
        long failures = clusterTransplants.stream().filter(t -> t.getGraftFailure() != null && t.getGraftFailure()).count();
        long rejections = clusterTransplants.stream().filter(t -> t.getAcuteRejection() != null && t.getAcuteRejection()).count();
        double failureRate = (double) failures / clusterTransplants.size();
        double rejectionRate = (double) rejections / clusterTransplants.size();

        if (failureRate > 0.5) return "HAUT_RISQUE_ECHEC";
        if (rejectionRate > 0.3) return "RISQUE_REJET_ELEVE";
        if (failureRate < 0.1 && rejectionRate < 0.1) return "FAIBLE_RISQUE";
        return "RISQUE_MODERE";
    }

    private List<String> extractCharacteristics(List<KidneyTransplant> clusterTransplants) {
        List<String> chars = new ArrayList<>();
        double avgIschemia = clusterTransplants.stream()
                .filter(t -> t.getColdIschemiaTime() != null)
                .mapToInt(KidneyTransplant::getColdIschemiaTime)
                .average().orElse(0);
        double avgStay = clusterTransplants.stream()
                .filter(t -> t.getHospitalStayDuration() != null)
                .mapToInt(KidneyTransplant::getHospitalStayDuration)
                .average().orElse(0);
        double avgSurvival = clusterTransplants.stream()
                .filter(t -> t.getGraftSurvivalMonths() != null)
                .mapToInt(KidneyTransplant::getGraftSurvivalMonths)
                .average().orElse(0);

        chars.add(String.format("ischemie_froide_moy:%.1fmin", avgIschemia));
        chars.add(String.format("sejour_moy:%.1fjours", avgStay));
        chars.add(String.format("survie_greffon_moy:%.1fmois", avgSurvival));
        chars.add(String.format("taille:%d", clusterTransplants.size()));
        return chars;
    }

    public boolean isTrained() { return trained; }
}
