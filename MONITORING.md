# Monitoring & DevOps - Prometheus, Grafana & SonarQube

## Vue d'ensemble

Ce projet intègre trois outils DevOps dans le pipeline CI/CD :

- **SonarQube** : Analyse de qualité du code
- **Prometheus** : Collecte de métriques des microservices
- **Grafana** : Visualisation des métriques via des dashboards

---

## 1. Pipeline Jenkins

Le pipeline suit ces étapes :

```
Checkout → Build & Test → SonarQube → Quality Gate → Docker Build → Push → Deploy K8s → Deploy Monitoring
```

### Stages ajoutés

| Stage | Description |
|---|---|
| **SonarQube Analysis** | Analyse chaque service (Eureka, Gateway, Backend, Frontend) en parallèle |
| **Quality Gate** | Bloque le pipeline si la qualité du code ne passe pas les seuils |
| **Deploy Monitoring** | Déploie Prometheus et Grafana dans Kubernetes |

---

## 2. SonarQube

### Ce qu'il fait

Analyse le code pour détecter :
- Bugs potentiels
- Code smells (mauvaises pratiques)
- Vulnérabilités de sécurité
- Couverture des tests (test coverage)

### Configuration requise dans Jenkins

Aller dans **Manage Jenkins → Credentials** et ajouter :
- **ID** : `sonarqube-credentials`
- **Type** : Secret text (SonarQube token) ou Username/Password

Modifier l'URL SonarQube dans le `Jenkinsfile` :
```groovy
SONAR_HOST_URL = "http://sonarqube:9000"
```

---

## 3. Prometheus

### Ce qu'il fait

Collecte les métriques des microservices Spring Boot via les endpoints `/actuator/prometheus` :
- CPU usage
- Memory usage (JVM heap)
- HTTP request rate
- Response time
- Error rate (5xx)

### Fichiers Kubernetes

**`k8s/06-prometheus.yaml`** contient :
- **ConfigMap** : Configuration de scrape (quels services monitorer et à quelle fréquence)
- **Deployment** : Pod Prometheus avec l'image `prom/prometheus:v2.51.0`
- **Service** : Service ClusterIP exposant le port 9090

### Métriques collectées

```yaml
scrape_configs:
  - job_name: 'eureka-server'          # Eureka sur port 8761
  - job_name: 'api-gateway'            # API Gateway sur port 8080
  - job_name: 'kidneytransplant-forum-service'  # Backend sur port 8091
  - job_name: 'prometheus'             # Prometheus lui-même sur port 9090
```

---

## 4. Grafana

### Ce qu'il fait

Connecté à Prometheus, il affiche des dashboards avec :
- **Error Rate** : Taux d'erreurs HTTP 5xx
- **Average Response Time** : Temps de réponse moyen des requêtes
- **JVM Memory Usage** : Utilisation de la mémoire Java (heap)
- **CPU Usage** : Utilisation CPU des microservices

### Fichiers Kubernetes

**`k8s/07-grafana.yaml`** contient :
- **ConfigMap datasources** : Configure Prometheus comme source de données
- **ConfigMap dashboards** : Configure le dossier de dashboards
- **ConfigMap dashboard-spring-boot** : Dashboard pré-configuré avec 4 panels
- **Deployment** : Pod Grafana avec l'image `grafana/grafana:10.4.0`
- **Service** : Service NodePort exposant le port 3000

### Accès par défaut

- **URL** : http://localhost:3000
- **Username** : `admin`
- **Password** : `admin`

---

## 5. Comment démarrer et accéder aux outils

### Vérifier le déploiement

```powershell
# Vérifier que tous les pods sont running
kubectl -n pediatric get pods

# Vérifier les services
kubectl -n pediatric get svc
```

### Accéder avec port-forward (recommandé pour le local)

```powershell
# Prometheus - ouvrir dans le navigateur : http://localhost:9090
kubectl -n pediatric port-forward svc/prometheus 9090:9090

# Grafana - ouvrir dans le navigateur : http://localhost:3000
kubectl -n pediatric port-forward svc/grafana 3000:3000
```

Chaque commande doit être exécutée dans un terminal séparé.

### Accéder avec Minikube

```powershell
# Ouvrir automatiquement Grafana dans le navigateur
minikube service grafana -n pediatric

# Ouvrir automatiquement Prometheus dans le navigateur
minikube service prometheus -n pediatric

# Obtenir les URLs directement
minikube service grafana -n pediatric --url
minikube service prometheus -n pediatric --url
```

### Accéder avec NodePort

Si Grafana utilise NodePort, trouver le port assigné :

```powershell
kubectl -n pediatric get svc grafana
```

Puis accéder via : `http://<node-ip>:<node-port>`

---

## 6. Récapitulatif des URLs

| Outil | URL (port-forward) | URL (Minikube) | Credentials |
|---|---|---|---|
| Prometheus | http://localhost:9090 | `minikube service prometheus --url` | Aucun |
| Grafana | http://localhost:3000 | `minikube service grafana --url` | admin / admin |
| SonarQube | Configuré dans Jenkins | Dépend de votre setup | admin / admin |

---

## 7. Commandes utiles pour la démo

```powershell
# 1. Appliquer tous les manifests Kubernetes
kubectl apply -f k8s/

# 2. Vérifier les pods
kubectl -n pediatric get pods

# 3. Lancer le port-forward
kubectl -n pediatric port-forward svc/prometheus 9090:9090
kubectl -n pediatric port-forward svc/grafana 3000:3000

# 4. Vérifier les logs si un pod ne démarre pas
kubectl -n pediatric logs deploy/prometheus
kubectl -n pediatric logs deploy/grafana

# 5. Redémarrer un déploiement
kubectl -n pediatric rollout restart deploy/prometheus
kubectl -n pediatric rollout restart deploy/grafana
```

---

## 8. Architecture complète

```
┌─────────────────────────────────────────────────────┐
│                     Jenkins Pipeline                  │
│                                                       │
│  Checkout → Build/Test → SonarQube → Quality Gate    │
│      ↓                                                 │
│  Build Docker → Push → Deploy K8s → Deploy Monitoring │
└─────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────┐
│                   Kubernetes Cluster                  │
│                                                       │
│  ┌──────────┐  ┌──────────┐  ┌──────────────────┐   │
│  │ Eureka   │  │ Gateway  │  │ Backend Service  │   │
│  │ :8761    │  │ :8080    │  │ :8091            │   │
│  └────┬─────┘  └────┬─────┘  └────────┬─────────┘   │
│       │              │                 │              │
│       └──────────────┴─────────────────┘              │
│                          │ scrape /actuator/prometheus│
│                          ▼                            │
│                    ┌──────────┐    ┌──────────────┐   │
│                    │Prometheus│───►│   Grafana    │   │
│                    │ :9090    │    │   :3000      │   │
│                    └──────────┘    └──────────────┘   │
└─────────────────────────────────────────────────────┘
```
