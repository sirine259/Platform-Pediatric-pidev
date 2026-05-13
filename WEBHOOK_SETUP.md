# Configuration Webhook GitHub -> Jenkins

## Vue d'ensemble
Ce projet utilise l'automatisation CI/CD avec déclenchement automatique du pipeline Jenkins à chaque push sur GitHub.

## Configuration du Webhook GitHub

1. Aller sur votre repository GitHub
2. Settings -> Webhooks -> Add webhook
3. Configuration:
   - **Payload URL**: `http://<JENKINS_URL>/github-webhook/`
   - **Content type**: `application/json`
   - **Events**: Select "Just the push event"
   - **Active**: ✓

## Modules configurés dans le pipeline

### KidneyTransplant Module
- **Backend tests**: `*KidneyTransplant*,*PostTransplantFollowUp*`
- **SonarQube project**: `pediatric-kidneytransplant-kidneytransplant`
- **Packages analysés**:
  - `com.esprit.platformepediatricback.entity.KidneyTransplant`
  - `com.esprit.platformepediatricback.Controller.KidneyTransplantController`
  - `com.esprit.platformepediatricback.Service.KidneyTransplantService`
  - `com.esprit.platformepediatricback.Repository.KidneyTransplantRepository`

### Forum Module
- **Backend tests**: `ForumServiceTest,PostServiceTest`
- **SonarQube project**: `pediatric-kidneytransplant-forum`
- **Packages analysés**:
  - `com.esprit.platformepediatricback.entity.Forum`
  - `com.esprit.platformepediatricback.entity.Post`
  - `com.esprit.platformepediatricback.Controller.ForumController`
  - `com.esprit.platformepediatricback.Service.ForumService`

### Frontend
- **SonarQube project**: `pediatric-kidneytransplant-frontend`
- **Modules inclus**: `**/kidney-transplant/**`, `**/forum/**`

## Pipeline Stages

1. **Checkout** - Récupération du code
2. **Build Backend** - Compilation rapide
3. **Test KidneyTransplant Module** - Tests fonctionnels rapides
4. **Test Forum Module** - Tests fonctionnels rapides
5. **Full Backend Test with Coverage** - Tests complets avec JaCoCo
6. **Build and Test Frontend** - Tests Angular avec couverture
7. **SonarQube & Quality Gate** - Analyse et validation pour chaque module
8. **Build Docker Images** - Construction des images
9. **Docker Login and Push** - Publication sur DockerHub
10. **Deploy to Kubernetes** - Déploiement sur K8s
11. **Deploy Monitoring** - Prometheus & Grafana
12. **Smoke Test** - Vérification du déploiement

## Déclenchement automatique

Le pipeline se déclenche automatiquement à chaque `git push` grâce à:
- `triggers { githubPush() }` dans le Jenkinsfile
- Webhook GitHub configuré

## Test manuel du pipeline

```bash
# Modifier un fichier des modules
echo "test" >> Back/src/main/java/com/esprit/platformepediatricback/Controller/KidneyTransplantController.java
git add .
git commit -m "Test kidneytransplant module"
git push origin main
```

Le pipeline se déclenchera automatiquement et testera spécifiquement les modules kidneytransplant et forum.
