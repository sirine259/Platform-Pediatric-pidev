package com.esprit.platformepediatricback.ml;

import com.esprit.platformepediatricback.Repository.KidneyTransplantRepository;
import com.esprit.platformepediatricback.entity.KidneyTransplant;
import com.esprit.platformepediatricback.ml.common.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransplantClassificationService {

    @Autowired
    private KidneyTransplantRepository kidneyTransplantRepository;

    private NaiveBayesClassifier outcomeClassifier;
    private boolean trained = false;

    public ClassificationResult classifyOutcome(Long transplantId) {
        Optional<KidneyTransplant> opt = kidneyTransplantRepository.findById(transplantId);
        if (opt.isEmpty()) {
            return new ClassificationResult("UNKNOWN", Map.of("UNKNOWN", 1.0), 0);
        }
        ensureTrained();
        KidneyTransplant t = opt.get();
        List<String> features = extractFeatureTokens(t);
        Map<String, Double> scores = outcomeClassifier.predict(features);
        String predicted = outcomeClassifier.getDominantClass(scores);
        double confidence = outcomeClassifier.getConfidence(scores);
        return new ClassificationResult(predicted, scores, confidence);
    }

    public ClassificationResult classifyRiskLevel(Long transplantId) {
        Optional<KidneyTransplant> opt = kidneyTransplantRepository.findById(transplantId);
        if (opt.isEmpty()) {
            return new ClassificationResult("UNKNOWN", Map.of("UNKNOWN", 1.0), 0);
        }
        KidneyTransplant t = opt.get();
        String riskLevel = t.getRiskLevel();

        Map<String, Double> probs = new HashMap<>();
        probs.put("LOW", riskLevel.equals("LOW") ? 0.8 : 0.1);
        probs.put("MEDIUM", riskLevel.equals("MEDIUM") ? 0.8 : 0.1);
        probs.put("HIGH", riskLevel.equals("HIGH") ? 0.8 : 0.1);

        return new ClassificationResult(riskLevel, probs, 0.7);
    }

    public ClassificationResult classifyComplicationRisk(Long transplantId) {
        Optional<KidneyTransplant> opt = kidneyTransplantRepository.findById(transplantId);
        if (opt.isEmpty()) return new ClassificationResult("UNKNOWN", Map.of("UNKNOWN", 1.0), 0);

        KidneyTransplant t = opt.get();
        int riskScore = 0;

        if (t.getColdIschemiaTime() != null && t.getColdIschemiaTime() > 24) riskScore++;
        if (t.getEstimatedBloodLoss() != null && t.getEstimatedBloodLoss() > 500) riskScore++;
        if (t.getDelayedGraftFunction() != null && t.getDelayedGraftFunction()) riskScore += 2;
        if (t.getPeakCreatinineLevel() != null && t.getPeakCreatinineLevel() > 3) riskScore++;
        if (t.getSurgeryDuration() != null && t.getSurgeryDuration() > 240) riskScore++;

        String risk;
        double confidence;
        if (riskScore >= 4) {
            risk = "HAUT_RISQUE_COMPLICATION";
            confidence = 0.8;
        } else if (riskScore >= 2) {
            risk = "RISQUE_MODERE";
            confidence = 0.6;
        } else {
            risk = "FAIBLE_RISQUE";
            confidence = 0.8;
        }

        Map<String, Double> probs = Map.of(
            "FAIBLE_RISQUE", risk.equals("FAIBLE_RISQUE") ? 0.7 : 0.15,
            "RISQUE_MODERE", risk.equals("RISQUE_MODERE") ? 0.7 : 0.15,
            "HAUT_RISQUE_COMPLICATION", risk.equals("HAUT_RISQUE_COMPLICATION") ? 0.7 : 0.15
        );

        ClassificationResult result = new ClassificationResult(risk, probs, confidence);
        result.setDetails("Score de risque: " + riskScore + "/6");
        return result;
    }

    private void ensureTrained() {
        if (!trained) {
            List<KidneyTransplant> transplants = kidneyTransplantRepository.findAll();
            Map<String, List<String>> labeled = new HashMap<>();
            labeled.put("SUCCESS", new ArrayList<>());
            labeled.put("FAILURE", new ArrayList<>());
            labeled.put("REJECTION", new ArrayList<>());
            labeled.put("DELAYED_FUNCTION", new ArrayList<>());

            for (KidneyTransplant t : transplants) {
                List<String> tokens = extractFeatureTokens(t);
                String label = determineOutcomeLabel(t);
                labeled.get(label).addAll(tokens);
            }

            outcomeClassifier = new NaiveBayesClassifier();
            outcomeClassifier.fit(labeled);
            trained = true;
        }
    }

    private List<String> extractFeatureTokens(KidneyTransplant t) {
        List<String> tokens = new ArrayList<>();
        if (t.getTransplantType() != null) tokens.add("type_" + t.getTransplantType().name());
        if (t.getSurgeryApproach() != null) tokens.add("approach_" + t.getSurgeryApproach().name());
        if (t.getKidneySource() != null) tokens.add("source_" + t.getKidneySource().name());
        if (t.getSurgicalTechnique() != null) tokens.add("technique_" + t.getSurgicalTechnique());
        if (t.getHospital() != null) tokens.add("hospital_" + t.getHospital());
        if (t.getDelayedGraftFunction() != null)
            tokens.add("delayed_" + t.getDelayedGraftFunction());
        if (t.getSurgicalSiteInfection() != null)
            tokens.add("infection_" + t.getSurgicalSiteInfection());
        if (t.getColdIschemiaTime() != null) {
            tokens.add("ischemia_" + (t.getColdIschemiaTime() > 24 ? "long" : "short"));
        }
        if (t.getGraftSurvivalMonths() != null) {
            tokens.add("survival_" + (t.getGraftSurvivalMonths() > 12 ? "long" : "short"));
        }
        return tokens;
    }

    private String determineOutcomeLabel(KidneyTransplant t) {
        if (t.getGraftFailure() != null && t.getGraftFailure()) return "FAILURE";
        if (t.getAcuteRejection() != null && t.getAcuteRejection()) return "REJECTION";
        if (t.getDelayedGraftFunction() != null && t.getDelayedGraftFunction()) return "DELAYED_FUNCTION";
        return "SUCCESS";
    }
}
