# Guide ML/AI — Plateforme Pédiatrique de Néphrologie

## Architecture

```
Back/                            Front/
├── pom.xml                     └── src/app/
├── src/main/java/.../              ├── ml/                          ← Composants ML
│   ├── ml/                         │   ├── ml-dashboard/            ← Tableau de bord
│   │   ├── common/                 │   ├── ml-clustering/           ← Clustering
│   │   │   ├── TextPreprocessor    │   ├── ml-classification/       ← Classification
│   │   │   ├── TFIDFCalculator     │   ├── ml-prediction/           ← Prédiction
│   │   │   ├── KMeansClustering    │   └── ml-recommendation/       ← Recommandation
│   │   │   ├── NaiveBayesClassifier│   └── services/
│   │   │   ├── LinearRegression    │       └── ml.service.ts        ← 23 méthodes API
│   │   │   └── CosineSimilarity    │
│   │   ├── ForumClustering         └── notebooks/
│   │   ├── ForumClassification         └── ml_algos_demo.ipynb      ← Notebook Python
│   │   ├── ForumPrediction
│   │   ├── ForumRecommendation
│   │   ├── TransplantClustering
│   │   ├── TransplantClassification
│   │   ├── TransplantPrediction
│   │   └── TransplantRecommendation
│   └── Controller/
│       ├── ForumAIController          ← /api/forum/ai/*
│       └── KidneyTransplantAIController ← /api/kidney-transplants/ai/*
```

---

## 1. Algorithmes ML (common/)

Tous implémentés en Java pur — **zéro dépendance ML externe** (sauf commons-math3 pour la régression).

### TextPreprocessor
- **Fichier** : `ml/common/TextPreprocessor.java`
- **Fonction** : Tokenisation, nettoyage, stop words (français + anglais)
- **Méthodes** :
  - `tokenize(String text)` → Liste de mots nettoyés
  - `computeTf(List<String> tokens)` → Term Frequency
  - `toVector(List<String>, List<String> vocab)` → Vecteur TF
  - `toTfIdfVector(...)` → Vecteur TF-IDF

### TFIDFCalculator
- **Fichier** : `ml/common/TFIDFCalculator.java`
- **Fonction** : Calcule TF-IDF sur un corpus de documents
- **Usage** : Vectorisation du texte des posts forum pour similarité

### KMeansClustering
- **Fichier** : `ml/common/KMeansClustering.java`
- **Algorithme** : K-Means avec initialisation aléatoire
- **Paramètres** : `k` (nb clusters), `maxIterations`
- **Distance** : Euclidienne (commons-math3)
- **Usage** : Clustering posts forum / greffes rénales

### NaiveBayesClassifier
- **Fichier** : `ml/common/NaiveBayesClassifier.java`
- **Algorithme** : Naive Bayes multinomial avec lissage Laplace
- **Usage** : Classification posts (6 catégories) / outcome greffe

### LinearRegression
- **Fichier** : `ml/common/LinearRegression.java`
- **Algorithme** : Régression linéaire simple (commons-math3 SimpleRegression)
- **Usage** : Prédiction popularité / tendance créatinine

### CosineSimilarity
- **Fichier** : `ml/common/CosineSimilarity.java`
- **Fonction** : Similarité cosinus entre vecteurs
- **Usage** : Recommandation posts similaires / profil utilisateur

---

## 2. Module Forum (Forum*Service.java)

### ForumClusteringService
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `clusterForumPosts()` | `GET /api/forum/ai/clusters` | Clusterise tous les posts par thème (K-Means + TF-IDF) |
| `predictCluster(postId)` | `GET /api/forum/ai/posts/{id}/cluster` | Prédit le cluster d'un post |

### ForumClassificationService
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `classifyPost(postId)` | `GET /api/forum/ai/posts/{id}/classify` | Classifie un post existant |
| `classifyText(title, content)` | `POST /api/forum/ai/classify` | Classifie un texte arbitraire |

**Catégories** : `MEDICAL_QUESTION`, `EXPERIENCE_SHARING`, `SUPPORT`, `INFORMATION`, `DISCUSSION`, `OTHER`

### ForumPredictionService
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `predictPopularity(postId)` | `GET /api/forum/ai/posts/{id}/predict-popularity` | Score de popularité (vues + likes) |
| `predictEngagement(postId)` | `GET /api/forum/ai/posts/{id}/predict-engagement` | Score d'engagement (commentaires) |
| `predictTrendPotential(postId)` | `GET /api/forum/ai/posts/{id}/predict-trend` | Potentiel de tendance |

### ForumRecommendationService
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `recommendPostsForUser(userId, limit)` | `GET /api/forum/ai/recommendations/user/{id}?limit=5` | Posts recommandés pour un utilisateur |
| `getSimilarPosts(postId, limit)` | `GET /api/forum/ai/posts/{id}/similar?limit=5` | Posts similaires |
| `getTrendingPosts(limit)` | `GET /api/forum/ai/trending?limit=5` | Posts tendance |

---

## 3. Module Transplantation (Transplant*Service.java)

### TransplantClusteringService
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `clusterTransplantsByRisk()` | `GET /api/kidney-transplants/ai/clusters` | Clusterise les greffes par risque |
| `predictCluster(transplantId)` | `GET /api/kidney-transplants/ai/transplants/{id}/cluster` | Cluster d'une greffe |

**Features (12)** : ischémie froide/chaude, perte sanguine, durée séjour, créatinine pic/base, survie greffon, fonction retardée, rejet aigu, échec, infection, temps anastomose

### TransplantClassificationService
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `classifyOutcome(transplantId)` | `GET .../classify-outcome` | Succès/Échec/Rejet/Fonction retardée |
| `classifyRiskLevel(transplantId)` | `GET .../classify-risk` | Risque LOW/MEDIUM/HIGH |
| `classifyComplicationRisk(transplantId)` | `GET .../classify-complication` | Risque complication (0-6) |

### TransplantPredictionService
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `predictGraftSurvival(transplantId)` | `GET .../predict-survival` | Survie estimée en mois |
| `predictRejectionRisk(transplantId)` | `GET .../predict-rejection` | Risque de rejet (0-1) |
| `predictCreatinineTrend(transplantId)` | `GET .../predict-creatinine` | Tendance créatinine (mg/dL) |
| `predictHospitalReadmission(transplantId)` | `GET .../predict-readmission` | Risque réhospitalisation (0-1) |

### TransplantRecommendationService
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `recommendFollowUpSchedule(transplantId)` | `GET .../recommend-followup` | Planning de suivi personnalisé |
| `recommendImmunosuppressionPlan(transplantId)` | `GET .../recommend-immunosuppression` | Plan immunosuppresseur |
| `recommendLifestyleAdjustments(transplantId)` | `GET .../recommend-lifestyle` | Ajustements mode de vie |
| `findSimilarTransplants(transplantId, limit)` | `GET .../similar?limit=5` | Greffes similaires |

---

## 4. Frontend Angular

### Service ML (`services/ml.service.ts`)
23 méthodes réparties en 2 groupes :
- **Forum AI** : `getForumClusters()`, `classifyPost()`, `predictPostPopularity()`, `recommendPostsForUser()`, etc.
- **Transplant AI** : `getTransplantClusters()`, `classifyTransplantOutcome()`, `predictGraftSurvival()`, `recommendFollowUp()`, etc.

### Composants (`ml/`)
| Route | Component | Fonction |
|-------|-----------|----------|
| `/ml/dashboard` | MLDashboardComponent | Vue d'ensemble des clusters + tendances |
| `/ml/clustering` | MLClusteringComponent | Détail des clusters forum et greffe |
| `/ml/classification` | MLClassificationComponent | Classification de posts et greffes |
| `/ml/prediction` | MLPredictionComponent | Prédictions popularité, survie, rejet |
| `/ml/recommendation` | MLRecommendationComponent | Recommandations personnalisées |

---

## 5. Notebook Python

**Fichier** : `notebooks/ml_algos_demo.ipynb`

Implémente les mêmes algorithmes en Python avec visualisations (matplotlib) :
1. **TF-IDF** : Tokenisation, calcul, matrice de similarité
2. **K-Means** : Clustering avec visualisation 2D
3. **Naive Bayes** : Classification avec barres de probabilités
4. **Régression Linéaire** : Tendance + prédiction
5. **Survie Greffon** : Estimation par scénarios
6. **Clustering Greffes** : Analyse de clusters
7. **Recommandation** : Matrice de similarité heatmap

```bash
pip install notebook matplotlib numpy scikit-learn
jupyter notebook notebooks/ml_algos_demo.ipynb
```

---

## 6. Dépendances

### Backend (pom.xml)
```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-math3</artifactId>
    <version>3.6.1</version>
</dependency>
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-text</artifactId>
    <version>1.12.0</version>
</dependency>
```

### Frontend (package.json)
Aucune dépendance ML supplémentaire — tout utilise `HttpClient` Angular standard.

---

## 7. Tests / Validation

- **Backend** : `mvn compile` → ✅ compile sans erreur
- **Notebook** : Ouvrir `notebooks/ml_algos_demo.ipynb` dans Jupyter et exécuter toutes les cellules
- **API** : Lancer le backend et tester les endpoints avec Swagger :
  - `http://localhost:8080/swagger-ui.html` (si configuré)
  - Sinon, utiliser Postman/curl

---

## 8. Structure des DTOs

```
ClusterResult          ClassificationResult        PredictionResult
├── clusterId           ├── predictedClass         ├── predictionType
├── itemIds             ├── classProbabilities     ├── predictedValue
├── topTerms            ├── confidence             ├── confidence
├── size                └── details                ├── unit
└── centroid                                      └── details (Map)

RecommendationResult
├── recommendationType
├── items[]
│   ├── id
│   ├── name
│   ├── score
│   └── reason
└── metadata
```
