package com.esprit.platformepediatricback.ml;

import com.esprit.platformepediatricback.Repository.KidneyTransplantRepository;
import com.esprit.platformepediatricback.Repository.PostTransplantFollowUpRepository;
import com.esprit.platformepediatricback.entity.*;
import com.esprit.platformepediatricback.ml.common.RecommendationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransplantRecommendationService {

    @Autowired
    private KidneyTransplantRepository kidneyTransplantRepository;

    @Autowired
    private PostTransplantFollowUpRepository followUpRepository;

    public List<RecommendationResult.RecommendedItem> recommendFollowUpSchedule(Long transplantId) {
        Optional<KidneyTransplant> opt = kidneyTransplantRepository.findById(transplantId);
        if (opt.isEmpty()) return List.of();

        KidneyTransplant t = opt.get();
        List<RecommendationResult.RecommendedItem> recommendations = new ArrayList<>();

        if (t.getRiskLevel().equals("HIGH")) {
            recommendations.add(new RecommendationResult.RecommendedItem(1L,
                "Consultation de suivi intensif: toutes les 2 semaines", 9.0));
            recommendations.add(new RecommendationResult.RecommendedItem(2L,
                "Bilan biologique hebdomadaire (cratinine, GFR, lectrolytes)", 8.5));
        } else if (t.getRiskLevel().equals("MEDIUM")) {
            recommendations.add(new RecommendationResult.RecommendedItem(1L,
                "Consultation mensuelle pendant 6 mois", 8.0));
            recommendations.add(new RecommendationResult.RecommendedItem(2L,
                "Bilan biologique toutes les 2 semaines", 7.5));
        } else {
            recommendations.add(new RecommendationResult.RecommendedItem(1L,
                "Consultation trimestrielle", 7.0));
            recommendations.add(new RecommendationResult.RecommendedItem(2L,
                "Bilan biologique mensuel", 6.5));
        }

        if (t.getDelayedGraftFunction() != null && t.getDelayedGraftFunction()) {
            recommendations.add(new RecommendationResult.RecommendedItem(3L,
                "Surveillance rapproche de la reprise de fonction", 9.5));
        }
        if (t.getAcuteRejection() != null && t.getAcuteRejection()) {
            recommendations.add(new RecommendationResult.RecommendedItem(4L,
                "Adaptation du traitement immunosuppresseur", 9.0));
        }

        return recommendations;
    }

    public List<RecommendationResult.RecommendedItem> recommendImmunosuppressionPlan(Long transplantId) {
        Optional<KidneyTransplant> opt = kidneyTransplantRepository.findById(transplantId);
        if (opt.isEmpty()) return List.of();

        KidneyTransplant t = opt.get();
        List<RecommendationResult.RecommendedItem> recommendations = new ArrayList<>();

        recommendations.add(new RecommendationResult.RecommendedItem(1L,
            "Tacrolimus + Mycophnolate moftil + Corticostrodes (standard)", 7.0));
        recommendations.add(new RecommendationResult.RecommendedItem(2L,
            "Surveillance des niveaux de tacrolimus (cible 5-15 ng/mL)", 8.0));

        if (t.getAcuteRejection() != null && t.getAcuteRejection()) {
            recommendations.add(new RecommendationResult.RecommendedItem(3L,
                "Traitement anti-rejet: Bolus de mthylprednisolone", 9.0));
            recommendations.add(new RecommendationResult.RecommendedItem(4L,
                "Envisager la conversion  l'everolimus", 7.5));
        }

        if (t.getPanelReactiveAntibodies() != null && !t.getPanelReactiveAntibodies().isEmpty()) {
            recommendations.add(new RecommendationResult.RecommendedItem(5L,
                "Dsensibilisation: Changes plasmatiques + IVIG", 8.5));
        }

        return recommendations;
    }

    public List<RecommendationResult.RecommendedItem> recommendLifestyleAdjustments(Long transplantId) {
        Optional<KidneyTransplant> opt = kidneyTransplantRepository.findById(transplantId);
        if (opt.isEmpty()) return List.of();

        KidneyTransplant t = opt.get();
        List<RecommendationResult.RecommendedItem> recommendations = new ArrayList<>();

        recommendations.add(new RecommendationResult.RecommendedItem(1L,
            "Rgime pauvre en sel (< 5g/jour)", 7.0));
        recommendations.add(new RecommendationResult.RecommendedItem(2L,
            "Hydratation abondante (1.5-2L/jour)", 6.5));
        recommendations.add(new RecommendationResult.RecommendedItem(3L,
            "Activit physique modre et rgulire", 7.0));

        if (t.getBloodProductsUsed() != null && t.getBloodProductsUsed() > 2) {
            recommendations.add(new RecommendationResult.RecommendedItem(4L,
                "Supplmentation en fer si anmie", 7.5));
        }

        if (t.getHospitalStayDuration() != null && t.getHospitalStayDuration() > 14) {
            recommendations.add(new RecommendationResult.RecommendedItem(5L,
                "Programme de rhabilitation renforc", 8.0));
        }

        recommendations.add(new RecommendationResult.RecommendedItem(6L,
            "Viter les AINS et nphrotoxiques", 9.0));

        return recommendations;
    }

    public List<RecommendationResult.RecommendedItem> findSimilarTransplants(Long transplantId, int limit) {
        Optional<KidneyTransplant> opt = kidneyTransplantRepository.findById(transplantId);
        if (opt.isEmpty()) return List.of();

        KidneyTransplant source = opt.get();
        List<KidneyTransplant> all = kidneyTransplantRepository.findAll();

        return all.stream()
                .filter(t -> !t.getId().equals(transplantId))
                .map(t -> {
                    double score = calculateSimilarity(source, t);
                    return new RecommendationResult.RecommendedItem(t.getId(),
                        "Transplant #" + t.getId() + " (" + t.getTransplantType() + ")", score);
                })
                .filter(r -> r.getScore() > 0.3)
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    private double calculateSimilarity(KidneyTransplant a, KidneyTransplant b) {
        double score = 0;
        int factors = 0;

        if (a.getTransplantType() == b.getTransplantType()) { score += 1; factors++; }
        if (a.getSurgeryApproach() == b.getSurgeryApproach()) { score += 0.8; factors++; }
        if (a.getKidneySource() == b.getKidneySource()) { score += 0.7; factors++; }
        if (a.getDelayedGraftFunction() == b.getDelayedGraftFunction()) { score += 0.5; factors++; }
        if (a.getAcuteRejection() == b.getAcuteRejection()) { score += 0.5; factors++; }
        if (a.getGraftFailure() == b.getGraftFailure()) { score += 0.5; factors++; }

        if (a.getColdIschemiaTime() != null && b.getColdIschemiaTime() != null) {
            double diff = Math.abs(a.getColdIschemiaTime() - b.getColdIschemiaTime());
            if (diff < 5) { score += 0.5; factors++; }
        }
        if (a.getHospital() != null && a.getHospital().equals(b.getHospital())) {
            score += 0.3; factors++;
        }

        return factors > 0 ? score / factors : 0;
    }
}
