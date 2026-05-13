package com.esprit.platformepediatricback.ml;

import com.esprit.platformepediatricback.Repository.KidneyTransplantRepository;
import com.esprit.platformepediatricback.Repository.PostTransplantFollowUpRepository;
import com.esprit.platformepediatricback.entity.KidneyTransplant;
import com.esprit.platformepediatricback.entity.PostTransplantFollowUp;
import com.esprit.platformepediatricback.ml.common.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class TransplantPredictionService {

    @Autowired
    private KidneyTransplantRepository kidneyTransplantRepository;

    @Autowired
    private PostTransplantFollowUpRepository followUpRepository;

    private boolean survivalTrained = false;

    public PredictionResult predictGraftSurvival(Long transplantId) {
        Optional<KidneyTransplant> opt = kidneyTransplantRepository.findById(transplantId);
        if (opt.isEmpty()) {
            return new PredictionResult("GRAFT_SURVIVAL", 0, 0, "months");
        }
        KidneyTransplant t = opt.get();
        double predictedMonths = estimateGraftSurvivalMonths(t);
        double confidence = calculateSurvivalConfidence(t);
        PredictionResult result = new PredictionResult("GRAFT_SURVIVAL", predictedMonths, confidence, "months");
        Map<String, Object> details = new HashMap<>();
        details.put("currentSurvivalMonths", t.getGraftSurvivalMonths());
        details.put("riskLevel", t.getRiskLevel());
        details.put("isSuccessful", t.isSuccessfulTransplant());
        details.put("predictedLongTerm", predictedMonths > 60 ? "EXCELLENT" : predictedMonths > 24 ? "BON" : "RESERVE");
        result.setDetails(details);
        return result;
    }

    public PredictionResult predictRejectionRisk(Long transplantId) {
        Optional<KidneyTransplant> opt = kidneyTransplantRepository.findById(transplantId);
        if (opt.isEmpty()) {
            return new PredictionResult("REJECTION_RISK", 0, 0, "probability");
        }
        KidneyTransplant t = opt.get();
        double risk = calculateRejectionRisk(t);
        double confidence = 0.75;
        PredictionResult result = new PredictionResult("REJECTION_RISK", risk, confidence, "probability");
        Map<String, Object> details = new HashMap<>();
        details.put("riskLevel", risk > 0.5 ? "ELEVE" : risk > 0.2 ? "MODERE" : "FAIBLE");
        details.put("contributingFactors", getContributingFactors(t));
        result.setDetails(details);
        return result;
    }

    public PredictionResult predictCreatinineTrend(Long transplantId) {
        Optional<KidneyTransplant> opt = kidneyTransplantRepository.findById(transplantId);
        if (opt.isEmpty()) {
            return new PredictionResult("CREATININE_TREND", 0, 0, "mg/dL");
        }
        KidneyTransplant t = opt.get();

        List<PostTransplantFollowUp> followUps = followUpRepository.findByKidneyTransplantOrderByFollowUpDateAsc(t);

        double predictedNext = 1.0;
        double confidence = 0.5;

        if (followUps.size() >= 2) {
            double[] times = new double[followUps.size()];
            double[] values = new double[followUps.size()];
            LocalDateTime base = followUps.get(0).getFollowUpDate();
            for (int i = 0; i < followUps.size(); i++) {
                times[i] = ChronoUnit.DAYS.between(base, followUps.get(i).getFollowUpDate());
                values[i] = followUps.get(i).getCreatinineLevel() != null ?
                        followUps.get(i).getCreatinineLevel() : 1.0;
            }
            LinearRegression lr = new LinearRegression();
            lr.fit(lr.prepareUnivariate(times), values);
            double nextTime = times[times.length - 1] + 30;
            predictedNext = lr.predict(new double[]{nextTime});
            confidence = Math.min(0.9, lr.getRSquared());
        } else if (t.getBaselineCreatinineLevel() != null) {
            predictedNext = t.getBaselineCreatinineLevel();
            confidence = 0.4;
        }

        PredictionResult result = new PredictionResult("CREATININE_TREND", predictedNext, confidence, "mg/dL");
        Map<String, Object> details = new HashMap<>();
        details.put("baseline", t.getBaselineCreatinineLevel());
        details.put("peak", t.getPeakCreatinineLevel());
        details.put("trend", predictedNext > 1.5 ? "HAUSSE" : "STABLE");
        details.put("dataPoints", followUps.size());
        result.setDetails(details);
        return result;
    }

    public PredictionResult predictHospitalReadmission(Long transplantId) {
        Optional<KidneyTransplant> opt = kidneyTransplantRepository.findById(transplantId);
        if (opt.isEmpty()) {
            return new PredictionResult("READMISSION_RISK", 0, 0, "probability");
        }
        KidneyTransplant t = opt.get();
        double risk = 0.0;
        int factors = 0;

        if (t.getDelayedGraftFunction() != null && t.getDelayedGraftFunction()) { risk += 0.3; factors++; }
        if (t.getAcuteRejection() != null && t.getAcuteRejection()) { risk += 0.3; factors++; }
        if (t.getSurgicalSiteInfection() != null && t.getSurgicalSiteInfection()) { risk += 0.2; factors++; }
        if (t.getHospitalStayDuration() != null && t.getHospitalStayDuration() > 14) { risk += 0.15; factors++; }
        if (t.getEstimatedBloodLoss() != null && t.getEstimatedBloodLoss() > 1000) { risk += 0.1; factors++; }

        risk = Math.min(0.95, risk);
        double confidence = Math.min(0.85, 0.5 + factors * 0.1);

        PredictionResult result = new PredictionResult("READMISSION_RISK", risk, confidence, "probability");
        Map<String, Object> details = new HashMap<>();
        details.put("riskLevel", risk > 0.5 ? "ELEVE" : risk > 0.2 ? "MODERE" : "FAIBLE");
        details.put("riskFactors", factors);
        result.setDetails(details);
        return result;
    }

    private double estimateGraftSurvivalMonths(KidneyTransplant t) {
        double base = 60;

        if (t.getTransplantType() == KidneyTransplant.TransplantType.LIVING_DONOR) base += 24;
        if (t.getTransplantType() == KidneyTransplant.TransplantType.DECEASED_DONOR) base -= 12;
        if (t.getColdIschemiaTime() != null) base -= t.getColdIschemiaTime() * 0.5;
        if (t.getWarmIschemiaTime() != null) base -= t.getWarmIschemiaTime() * 0.3;
        if (t.getDelayedGraftFunction() != null && t.getDelayedGraftFunction()) base -= 18;
        if (t.getAcuteRejection() != null && t.getAcuteRejection()) base -= 24;
        if (t.getGraftFailure() != null && t.getGraftFailure()) base = t.getGraftSurvivalMonths() != null ?
                t.getGraftSurvivalMonths() : 6;
        if (t.getPrimaryGraftFunction() != null && t.getPrimaryGraftFunction()) base += 12;
        if (t.getSurgicalSiteInfection() != null && t.getSurgicalSiteInfection()) base -= 6;
        if (t.getPeakCreatinineLevel() != null && t.getPeakCreatinineLevel() > 3) base -= 12;

        return Math.max(1, base);
    }

    private double calculateRejectionRisk(KidneyTransplant t) {
        double risk = 0.1;
        if (t.getDelayedGraftFunction() != null && t.getDelayedGraftFunction()) risk += 0.25;
        if (t.getPanelReactiveAntibodies() != null && !t.getPanelReactiveAntibodies().isEmpty()) risk += 0.2;
        if (t.getHlaTyping() != null && t.getHlaTyping().toLowerCase().contains("mismatch")) risk += 0.15;
        if (t.getColdIschemiaTime() != null && t.getColdIschemiaTime() > 30) risk += 0.1;
        if (t.getAcuteRejection() != null && t.getAcuteRejection()) risk += 0.3;
        return Math.min(0.95, risk);
    }

    private List<String> getContributingFactors(KidneyTransplant t) {
        List<String> factors = new ArrayList<>();
        if (t.getDelayedGraftFunction() != null && t.getDelayedGraftFunction())
            factors.add("Reprise retarde de fonction");
        if (t.getPanelReactiveAntibodies() != null && !t.getPanelReactiveAntibodies().isEmpty())
            factors.add("Anticorps ractifs levs");
        if (t.getColdIschemiaTime() != null && t.getColdIschemiaTime() > 30)
            factors.add("Ischmie froide prolonge");
        if (t.getAcuteRejection() != null && t.getAcuteRejection())
            factors.add("Antcdent de rejet aigu");
        return factors;
    }

    private double calculateSurvivalConfidence(KidneyTransplant t) {
        if (t.getGraftSurvivalMonths() != null && t.getGraftSurvivalMonths() > 12) return 0.85;
        if (t.getGraftSurvivalMonths() != null) return 0.7;
        return 0.5;
    }
}
