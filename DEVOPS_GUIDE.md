# Guide Complet DevOps - Plateforme Pédiatrique

## Table des matières
1. [Architecture DevOps](#architecture-devops)
2. [SonarQube - Analyse Automatique](#sonarqube---analyse-automatique)
3. [Grafana & Prometheus - Monitoring](#grafana--prometheus---monitoring)
4. [Jenkins & Webhooks - Automatisation](#jenkins--webhooks---automatisation)
5. [Pipeline CI/CD Complet](#pipeline-cicd-complet)
6. [Commandes Utiles](#commandes-utiles)

---

## Architecture DevOps

```
┌─────────────────────────────────────────────────────────────┐
│                     DÉVELOPPEMENT                           │
│  Git Push → GitHub/GitLab                                │
└────────────────────────┬────────────────────────────────────┘
                         │ Webhook
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                     JENKINS PIPELINE                       │
│                                                             │
│  1. Checkout                                                │
│  2. Build & Test (avec JaCoCo Coverage)                    │
│  3. SonarQube Analysis (Bugs, Vulns, Coverage)            │
│  4. Quality Gate (Bloque si échec)                         │
│  5. Build Docker Images (Front + Back)                     │
│  6. Push to DockerHub                                      │
│  7. Deploy to Kubernetes                                   │
│  8. Deploy Monitoring (Prometheus + Grafana)               │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                   KUBERNETES CLUSTER                       │
│                                                             │
│  ┌──────────┐  ┌──────────┐  ┌──────────────────┐       │
│  │ Eureka   │  │ Gateway  │  │ Backend Service  │       │
│  │ :8761    │  │ :8080    │  │ :8091            │       │
│  └────┬─────┘  └────┬─────┘  └────────┬─────────┘       │
│       │              │                 │                    │
│       └──────────────┴─────────────────┘                    │
│                          │ scrape /actuator/prometheus      │
│                          ▼                                  │
│                    ┌──────────┐    ┌──────────────┐        │
│                    │Prometheus│───►│   Grafana    │        │
│                    │ :9090    │    │   :3000      │        │
│                    └──────────┘    └──────────────┘        │
└─────────────────────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                   SONARQUBE :9000                          │
│  - Analyse qualité code                                    │
│  - Detection bugs & vulnérabilités                         │
│  - Couverture de tests (JaCoCo + LCOV)                    │
└─────────────────────────────────────────────────────────────┘
```

---

## SonarQube - Analyse Automatique

### 1. Démarrer SonarQube avec Docker Compose

```powershell
# Démarrer tous les services (SonarQube inclus maintenant)
docker-compose up -d

# Vérifier que SonarQube est prêt
docker logs sonarqube -f
# Attendre "SonarQube is operational"

# Accès : http://localhost:9000
# Credentials par défaut : admin / admin (changer au premier login)
```

### 2. Configuration SonarQube

#### Étape 1 : Créer un token dans SonarQube
1. Connectez-vous à `http://localhost:9000`
2. Allez dans **Administration → Security → Users**
3. Créez un token pour Jenkins : **My Account → Security → Generate Tokens**
4. Copiez le token (ex: `squ_xxxxxxxxxxxxxxxx`)

#### Étape 2 : Configurer le token dans Jenkins
1. Allez dans **Jenkins → Manage Jenkins → Credentials**
2. Ajoutez une credential de type **Secret text**
3. **ID** : `sonarqube-credentials`
4. **Secret** : collez le token SonarQube

### 3. Ce qui est analysé automatiquement

| Type d'analyse | Outil | Frontend | Backend |
|----------------|-------|----------|---------|
| **Bugs** | SonarQube | ✅ | ✅ |
| **Vulnérabilités** | SonarQube | ✅ | ✅ |
| **Code Smells** | SonarQube | ✅ | ✅ |
| **Coverage** | JaCoCo (Back) + LCOV (Front) | ✅ | ✅ |
| **Duplications** | SonarQube | ✅ | ✅ |
| **Complexité** | SonarQube | ✅ | ✅ |

### 4. Voir les résultats dans SonarQube

Après chaque pipeline, allez sur `http://localhost:9000/projects` :
- **pediatric-platform-eureka** : Analyse Eureka Server
- **pediatric-platform-gateway** : Analyse API Gateway
- **pediatric-platform-backend** : Analyse Backend Service
- **pediatric-platform-frontend** : Analyse Frontend Angular

---

## Grafana & Prometheus - Monitoring

### 1. Accéder aux dashboards

#### Avec Docker Compose (Local)
```powershell
# Prometheus
http://localhost:9090

# Grafana
http://localhost:3000
# Login: admin / admin
```

#### Avec Kubernetes (Minikube)
```powershell
# Port-forward Prometheus
kubectl -n pediatric port-forward svc/prometheus 9090:9090
# Puis ouvrir : http://localhost:9090

# Port-forward Grafana
kubectl -n pediatric port-forward svc/grafana 3000:3000
# Puis ouvrir : http://localhost:3000
```

#### Avec NodePort (Accès direct)
```powershell
# Obtenir l'IP du node
minikube ip
# Exemple: 192.168.49.2

# Accès direct
http://192.168.49.2:30090  # Prometheus (NodePort 30090)
http://192.168.49.2:30xxx   # Grafana (vérifier le port avec kubectl)
```

### 2. Dashboards disponibles dans Grafana

| Dashboard | Description |
|-----------|-------------|
| **Spring Boot Services** | Error Rate, Response Time, JVM Memory, CPU Usage |

### 3. Métriques collectées par Prometheus

Les microservices Spring Boot exposent `/actuator/prometheus` :
- `http_server_requests_seconds_count` : Nombre de requêtes HTTP
- `http_server_requests_seconds_sum` : Temps de réponse total
- `jvm_memory_used_bytes` : Mémoire JVM utilisée
- `process_cpu_usage` : Utilisation CPU
- `jvm_gc_pause_seconds` : Pauses Garbage Collector

### 4. Requêtes PromQL utiles

```promql
# Taux d'erreurs 5xx (sur 5 minutes)
rate(http_server_requests_seconds_count{status=~"5.."}[5m])

# Temps de réponse moyen
rate(http_server_requests_seconds_sum[5m]) / rate(http_server_requests_seconds_count[5m])

# Mémoire JVM Heap utilisée
jvm_memory_used_bytes{area="heap"}

# CPU Usage
process_cpu_usage * 100
```

---

## Jenkins & Webhooks - Automatisation

### 1. Configuration des Webhooks (Git → Jenkins)

#### Pour GitHub
1. Allez dans votre repo GitHub : **Settings → Webhooks → Add webhook**
2. **Payload URL** : `http://<JENKINS_URL>/github-webhook/`
   - Si Jenkins est local : utilisez un outil comme `ngrok` pour exposer : `ngrok http 8080`
   - Exemple : `https://abc123.ngrok.io/github-webhook/`
3. **Content type** : `application/json`
4. **Events** : Sélectionnez **Just the push event**
5. Cliquez sur **Add webhook**

#### Pour GitLab
1. Allez dans votre repo GitLab : **Settings → Webhooks**
2. **URL** : `http://<JENKINS_URL>/project/<JOB_NAME>`
3. **Secret Token** : Générez dans Jenkins → Job → Configure → Build Triggers → GitHub hook trigger
4. Sélectionnez **Push events**
5. Cliquez sur **Add webhook**

#### Pour Gitea (local)
```powershell
# URL du webhook
http://<JENKINS_IP>:8080/github-webhook/
```

### 2. Configuration Jenkins pour les Webhooks

Dans Jenkins, pour chaque Job/Pipeline :

1. Allez dans **Configure**
2. Section **Build Triggers**
3. Cochez **GitHub hook trigger for GITScm polling** (ou **Poll SCM** avec schedule vide)
4. Sauvegardez

Le `Jenkinsfile` contient déjà :
```groovy
triggers {
  githubPush()
}
```

### 3. Test du Webhook

```powershell
# Faire un petit changement et pousser
echo "test" >> README.md
git add .
git commit -m "Test webhook"
git push

# Vérifier dans Jenkins que le pipeline se déclenche automatiquement
```

### 4. Automatisation Frontend et Backend

Le pipeline est configuré pour builder et déployer AUTOMATIQUEMENT les deux parties :

| Partie | Déclenchement | Étapes |
|--------|---------------|--------|
| **Backend** (Eureka, Gateway, Service) | Push sur `Back/**` | Build → Test → SonarQube → Docker → K8s |
| **Frontend** (Angular) | Push sur `Front/**` | npm ci → Test → SonarQube → Docker → K8s |

---

## Pipeline CI/CD Complet

### Vue d'ensemble du Pipeline

```
┌─────────────────────────────────────────────────────────────┐
│  DÉCLENCHEMENT : Git Push (automatique via webhook)        │
└────────────────────────┬────────────────────────────────────┘
                         ▼
┌─────────────────────────────────────────────────────────────┐
│  STAGE 1 : CHECKOUT                                       │
│  → Récupération du code depuis Git                         │
└────────────────────────┬────────────────────────────────────┘
                         ▼
┌─────────────────────────────────────────────────────────────┐
│  STAGE 2 : BUILD & TEST (PARALLÈLE)                       │
│                                                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐    │
│  │ Eureka       │  │ Gateway      │  │ Backend      │    │
│  │ mvn verify  │  │ mvn verify  │  │ mvn verify  │    │
│  │ + JaCoCo    │  │ + JaCoCo    │  │ + JaCoCo    │    │
│  └──────────────┘  └──────────────┘  └──────────────┘    │
│                                                             │
│  ┌──────────────┐                                         │
│  │ Frontend     │                                         │
│  │ npm test     │                                         │
│  │ + coverage  │                                         │
│  └──────────────┘                                         │
└────────────────────────┬────────────────────────────────────┘
                         ▼
┌─────────────────────────────────────────────────────────────┐
│  STAGE 3 : SONARQUBE ANALYSIS (PARALLÈLE)                 │
│                                                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐    │
│  │ Eureka       │  │ Gateway      │  │ Backend      │    │
│  │ Sonar:sonar │  │ Sonar:sonar │  │ Sonar:sonar │    │
│  │ + coverage  │  │ + coverage  │  │ + coverage  │    │
│  └──────────────┘  └──────────────┘  └──────────────┘    │
│                                                             │
│  ┌──────────────┐                                         │
│  │ Frontend     │                                         │
│  │ sonar-scanner│                                         │
│  │ + lcov.info │                                         │
│  └──────────────┘                                         │
└────────────────────────┬────────────────────────────────────┘
                         ▼
┌─────────────────────────────────────────────────────────────┐
│  STAGE 4 : QUALITY GATE                                   │
│  → Attend les résultats des 4 analyses SonarQube          │
│  → BLOQUE le pipeline si Quality Gate != OK               │
└────────────────────────┬────────────────────────────────────┘
                         ▼
┌─────────────────────────────────────────────────────────────┐
│  STAGE 5 : BUILD DOCKER IMAGES (PARALLÈLE)                │
│  → Build des images Frontend et Backend                   │
└────────────────────────┬────────────────────────────────────┘
                         ▼
┌─────────────────────────────────────────────────────────────┐
│  STAGE 6 : DOCKER LOGIN & PUSH                            │
│  → Push vers DockerHub avec tag BUILD_NUMBER et latest     │
└────────────────────────┬────────────────────────────────────┘
                         ▼
┌─────────────────────────────────────────────────────────────┐
│  STAGE 7 : DEPLOY TO KUBERNETES                           │
│  → kubectl apply -f k8s/                                  │
│  → kubectl set image pour chaque déploiement               │
│  → kubectl rollout status (attendre déploiement)          │
└────────────────────────┬────────────────────────────────────┘
                         ▼
┌─────────────────────────────────────────────────────────────┐
│  STAGE 8 : DEPLOY MONITORING                              │
│  → Déploie Prometheus et Grafana dans K8s                  │
└────────────────────────┬────────────────────────────────────┘
                         ▼
┌─────────────────────────────────────────────────────────────┐
│  POST : SUCCESS/FAILURE                                   │
│  → Email de notification                                  │
│  → Nettoyage du workspace (cleanWs)                       │
└─────────────────────────────────────────────────────────────┘
```

### Rapports disponibles dans Jenkins après chaque build

| Rapport | Emplacement | Description |
|---------|-------------|-------------|
| **JaCoCo Eureka Coverage** | Build → JaCoCo Eureka Coverage | Coverage Eureka Server |
| **JaCoCo Gateway Coverage** | Build → JaCoCo Gateway Coverage | Coverage API Gateway |
| **JaCoCo Backend Coverage** | Build → JaCoCo Backend Coverage | Coverage Backend Service |
| **Frontend Coverage** | Build → Frontend Coverage | Coverage Angular (Istanbul) |

---

## Commandes Utiles

### Démarrer l'infrastructure complète

```powershell
# Docker Compose (Local développement)
docker-compose up -d
docker-compose ps

# Vérifier les logs
docker-compose logs -f sonarqube
docker-compose logs -f prometheus
docker-compose logs -f grafana
```

### Kubernetes (Minikube)

```powershell
# Démarrer Minikube
minikube start --cpus=4 --memory=8192

# Activer l'ingress (optionnel)
minikube addons enable ingress

# Appliquer tous les manifests
kubectl apply -f k8s/

# Voir les pods
kubectl -n pediatric get pods -w

# Voir les logs d'un pod
kubectl -n pediatric logs -f deploy/backend-service

# Port-forward pour accès local
kubectl -n pediatric port-forward svc/frontend 80:80
kubectl -n pediatric port-forward svc/eureka-server 8761:8761
kubectl -n pediatric port-forward svc/prometheus 9090:9090
kubectl -n pediatric port-forward svc/grafana 3000:3000
```

### Jenkins CLI

```powershell
# Déclencher manuellement un build
curl -X POST http://localhost:8080/job/Pediatric-Platform/build

# Voir les logs du dernier build
curl http://localhost:8080/job/Pediatric-Platform/lastBuild/console

# Installer les plugins nécessaires dans Jenkins
# Manage Jenkins → Plugins → Available:
# - SonarQube Scanner
# - JaCoCo
# - Docker Pipeline
# - Kubernetes CLI
# - GitHub Integration
# - NodeJS
```

### Accès rapide aux outils

| Outil | URL (Local Docker) | URL (K8s + port-forward) | Credentials |
|-------|-------------------|--------------------------|-------------|
| **Jenkins** | http://localhost:8080 | `minikube service jenkins -n pediatric --url` | admin / (voir initialAdminPassword) |
| **SonarQube** | http://localhost:9000 | `minikube service sonarqube -n pediatric --url` | admin / admin |
| **Prometheus** | http://localhost:9090 | http://localhost:9090 (port-forward) | Aucun |
| **Grafana** | http://localhost:3000 | http://localhost:3000 (port-forward) | admin / admin |
| **Eureka** | http://localhost:8761 | http://localhost:8761 (port-forward) | Aucun |
| **Frontend** | http://localhost | http://localhost (port-forward) | - |

---

## Dépannage (Troubleshooting)

### Problème : SonarQube Quality Gate échoue

```powershell
# Vérifier les détails dans SonarQube
# Aller sur le projet → Quality Gate → Voir les conditions non remplies

# Conditions par défaut à ajuster selon votre projet :
# - Coverage < 80% → Échec
# - Duplications > 3% → Échec
# - Maintainability Rating > A → Échec
```

### Problème : Webhook ne déclenche pas Jenkins

```powershell
# Vérifier les logs Jenkins
docker logs jenkins | grep -i webhook

# Vérifier la configuration du webhook côté Git
# Faire un test manuel :
curl -X POST http://localhost:8080/github-webhook/

# Vérifier que le plugin GitHub est installé dans Jenkins
```

### Problème : Prometheus ne scrape pas les métriques

```powershell
# Vérifier que /actuator/prometheus est accessible
curl http://localhost:8091/actuator/prometheus

# Si erreur 404, vérifier que les dépendances sont dans pom.xml :
# - spring-boot-starter-actuator
# - micrometer-registry-prometheus

# Vérifier la configuration Prometheus
kubectl -n pediatric exec -it deploy/prometheus -- cat /etc/prometheus/prometheus.yml
```

### Problème : Grafana dit "Datasource not found"

```powershell
# Vérifier que Prometheus est accessible depuis Grafana
kubectl -n pediatric exec -it deploy/grafana -- wget -qO- http://prometheus:9090/-/healthy

# Vérifier la ConfigMap datasources
kubectl -n pediatric get configmap grafana-datasources -o yaml
```

---

## Résumé des URLs pour la démo

```powershell
# 1. Démarrer tout
docker-compose up -d

# 2. Accéder aux outils
# SonarQube : http://localhost:9000 (admin/admin)
# Prometheus : http://localhost:9090
# Grafana : http://localhost:3000 (admin/admin)
# Eureka : http://localhost:8761
# Frontend : http://localhost

# 3. Déclencher un pipeline (après config webhook)
git commit --allow-empty -m "Trigger pipeline"
git push

# 4. Voir le pipeline
# Jenkins : http://localhost:8080
```

---

**Auteur :** DevOps Team  
**Dernière mise à jour :** Mai 2026
