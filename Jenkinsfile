pipeline {
  agent any
  options {
    timestamps()
    disableConcurrentBuilds()
    buildDiscarder(logRotator(numToKeepStr: "10"))
  }

  triggers {
    githubPush()
  }

  tools {
    maven "Maven3"
    nodejs "NodeJS"
  }

  environment {
    NAMESPACE = "pediatric"
    TAG = "${env.BUILD_NUMBER}"
    DOCKERHUB_USER = "sirine215"
    IMAGE_EUREKA = "${DOCKERHUB_USER}/pediatric-eureka-server"
    IMAGE_GATEWAY = "${DOCKERHUB_USER}/pediatric-api-gateway"
    IMAGE_BACKEND = "${DOCKERHUB_USER}/pediatric-kidneytransplant-forum-service"
    IMAGE_FRONTEND = "${DOCKERHUB_USER}/pediatric-frontend"
    SONAR_HOST_URL = "http://sonarqube:9000"
    SONAR_PROJECT_KEY = "pediatric-platform"
  }

  stages {
    stage("Checkout") {
      steps {
        checkout scm
      }
    }

    stage("Build and Test Backend with Coverage") {
      parallel {
        stage("Build and Test Eureka") {
          steps {
            sh "cd Back/eureka-server && mvn clean verify jacoco:report -DskipTests=false"
          }
          post {
            success {
              publishHTML(target: [
                allowMissing: false,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'Back/eureka-server/target/site/jacoco',
                reportFiles: 'index.html',
                reportName: 'JaCoCo Eureka Coverage',
                reportTitles: ''
              ])
            }
          }
        }
        stage("Build and Test Gateway") {
          steps {
            sh "cd Back/api-gateway && mvn clean verify jacoco:report -DskipTests=false"
          }
          post {
            success {
              publishHTML(target: [
                allowMissing: false,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'Back/api-gateway/target/site/jacoco',
                reportFiles: 'index.html',
                reportName: 'JaCoCo Gateway Coverage',
                reportTitles: ''
              ])
            }
          }
        }
        stage("Build and Test Backend Service") {
          steps {
            sh "cd Back && mvn clean verify jacoco:report -DskipTests=false"
          }
          post {
            success {
              publishHTML(target: [
                allowMissing: false,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'Back/target/site/jacoco',
                reportFiles: 'index.html',
                reportName: 'JaCoCo Backend Coverage',
                reportTitles: ''
              ])
            }
          }
        }
      }
    }

    stage("Build and Test Frontend") {
      steps {
        sh "cd Front && npm ci --legacy-peer-deps"
        sh "cd Front && npm run test -- --watch=false --code-coverage --browsers=ChromeHeadless"
      }
      post {
        success {
          publishHTML(target: [
            allowMissing: false,
            alwaysLinkToLastBuild: true,
            keepAll: true,
            reportDir: 'Front/coverage/pediatric-nephrology-platform',
            reportFiles: 'index.html',
            reportName: 'Frontend Coverage',
            reportTitles: ''
          ])
        }
      }
    }

    stage("SonarQube Analysis") {
      parallel {
        stage("SonarQube - Eureka") {
          steps {
            withSonarQubeEnv(credentialsId: 'sonarqube-credentials') {
              sh "cd Back/eureka-server && mvn sonar:sonar -Dsonar.projectKey=${SONAR_PROJECT_KEY}-eureka -Dsonar.projectName='Eureka Server' -Dsonar.host.url=${SONAR_HOST_URL} -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml"
            }
          }
        }
        stage("SonarQube - Gateway") {
          steps {
            withSonarQubeEnv(credentialsId: 'sonarqube-credentials') {
              sh "cd Back/api-gateway && mvn sonar:sonar -Dsonar.projectKey=${SONAR_PROJECT_KEY}-gateway -Dsonar.projectName='API Gateway' -Dsonar.host.url=${SONAR_HOST_URL} -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml"
            }
          }
        }
        stage("SonarQube - Backend Service") {
          steps {
            withSonarQubeEnv(credentialsId: 'sonarqube-credentials') {
              sh "cd Back && mvn sonar:sonar -Dsonar.projectKey=${SONAR_PROJECT_KEY}-backend -Dsonar.projectName='Backend Service' -Dsonar.host.url=${SONAR_HOST_URL} -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml"
            }
          }
        }
        stage("SonarQube - Frontend") {
          steps {
            script {
              withSonarQubeEnv(credentialsId: 'sonarqube-credentials') {
                sh "cd Front && npx sonar-scanner -Dsonar.projectKey=${SONAR_PROJECT_KEY}-frontend -Dsonar.projectName='Frontend' -Dsonar.sources=src -Dsonar.host.url=${SONAR_HOST_URL} -Dsonar.exclusions=**/node_modules/**,**/*.spec.ts -Dsonar.javascript.lcov.reportPaths=coverage/pediatric-nephrology-platform/lcov.info"
              }
            }
          }
        }
      }
    }

    stage("Quality Gate - All Projects") {
      steps {
        timeout(time: 15, unit: 'MINUTES') {
          script {
            def qgEureka = waitForQualityGate()
            if (qgEureka.status != 'OK') {
              error "Quality Gate EUREKA failed: ${qgEureka.status}"
            }
            def qgGateway = waitForQualityGate()
            if (qgGateway.status != 'OK') {
              error "Quality Gate GATEWAY failed: ${qgGateway.status}"
            }
            def qgBackend = waitForQualityGate()
            if (qgBackend.status != 'OK') {
              error "Quality Gate BACKEND failed: ${qgBackend.status}"
            }
            def qgFrontend = waitForQualityGate()
            if (qgFrontend.status != 'OK') {
              error "Quality Gate FRONTEND failed: ${qgFrontend.status}"
            }
            echo "Tous les Quality Gates passes avec succes!"
          }
        }
      }
    }

    stage("Build Docker Images") {
      parallel {
        stage("Build Backend Images") {
          steps {
            script {
              sh "docker build -t ${IMAGE_EUREKA}:${TAG} -f Back/eureka-server/Dockerfile Back/eureka-server"
              sh "docker build -t ${IMAGE_GATEWAY}:${TAG} -f Back/api-gateway/Dockerfile Back/api-gateway"
              sh "docker build -t ${IMAGE_BACKEND}:${TAG} -f Back/Dockerfile Back"
            }
          }
        }
        stage("Build Frontend Image") {
          steps {
            script {
              sh "docker build -t ${IMAGE_FRONTEND}:${TAG} -f Front/Dockerfile Front"
            }
          }
        }
      }
    }

    stage("Docker Login and Push") {
      steps {
        withCredentials([usernamePassword(credentialsId: "sirine215", usernameVariable: "DOCKER_USER", passwordVariable: "DOCKER_PASS")]) {
          script {
            sh "echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin"
            sh "docker push ${IMAGE_EUREKA}:${TAG}"
            sh "docker push ${IMAGE_GATEWAY}:${TAG}"
            sh "docker push ${IMAGE_BACKEND}:${TAG}"
            sh "docker push ${IMAGE_FRONTEND}:${TAG}"
            sh "docker tag ${IMAGE_EUREKA}:${TAG} ${IMAGE_EUREKA}:latest"
            sh "docker tag ${IMAGE_GATEWAY}:${TAG} ${IMAGE_GATEWAY}:latest"
            sh "docker tag ${IMAGE_BACKEND}:${TAG} ${IMAGE_BACKEND}:latest"
            sh "docker tag ${IMAGE_FRONTEND}:${TAG} ${IMAGE_FRONTEND}:latest"
            sh "docker push ${IMAGE_EUREKA}:latest"
            sh "docker push ${IMAGE_GATEWAY}:latest"
            sh "docker push ${IMAGE_BACKEND}:latest"
            sh "docker push ${IMAGE_FRONTEND}:latest"
          }
        }
      }
    }

    stage("Deploy to Kubernetes") {
      steps {
        withCredentials([file(credentialsId: "pediatric medical", variable: "KUBECONFIG_FILE")]) {
          script {
            sh "kubectl --kubeconfig=$KUBECONFIG_FILE apply -f k8s/00-namespace.yaml"
            withCredentials([usernamePassword(credentialsId: "sirine215", usernameVariable: "DOCKER_USER", passwordVariable: "DOCKER_PASS")]) {
              sh "kubectl --kubeconfig=$KUBECONFIG_FILE -n ${NAMESPACE} delete secret dockerhub-creds --ignore-not-found"
              sh "kubectl --kubeconfig=$KUBECONFIG_FILE -n ${NAMESPACE} create secret docker-registry dockerhub-creds --docker-server=https://index.docker.io/v1/ --docker-username=$DOCKER_USER --docker-password=$DOCKER_PASS"
            }
            sh "kubectl --kubeconfig=$KUBECONFIG_FILE apply -f k8s/"
            sh "kubectl --kubeconfig=$KUBECONFIG_FILE -n ${NAMESPACE} set image deployment/eureka-server eureka-server=${IMAGE_EUREKA}:${TAG}"
            sh "kubectl --kubeconfig=$KUBECONFIG_FILE -n ${NAMESPACE} rollout status deployment/eureka-server --timeout=180s"
            sh "kubectl --kubeconfig=$KUBECONFIG_FILE -n ${NAMESPACE} set image deployment/api-gateway api-gateway=${IMAGE_GATEWAY}:${TAG}"
            sh "kubectl --kubeconfig=$KUBECONFIG_FILE -n ${NAMESPACE} rollout status deployment/api-gateway --timeout=180s"
            sh "kubectl --kubeconfig=$KUBECONFIG_FILE -n ${NAMESPACE} set image deployment/kidneytransplant-forum-service kidneytransplant-forum-service=${IMAGE_BACKEND}:${TAG}"
            sh "kubectl --kubeconfig=$KUBECONFIG_FILE -n ${NAMESPACE} rollout status deployment/kidneytransplant-forum-service --timeout=180s"
            sh "kubectl --kubeconfig=$KUBECONFIG_FILE -n ${NAMESPACE} set image deployment/frontend frontend=${IMAGE_FRONTEND}:${TAG}"
            sh "kubectl --kubeconfig=$KUBECONFIG_FILE -n ${NAMESPACE} rollout status deployment/frontend --timeout=180s"
          }
        }
      }
    }

    stage("Deploy Monitoring (Prometheus + Grafana)") {
      steps {
        withCredentials([file(credentialsId: "pediatric medical", variable: "KUBECONFIG_FILE")]) {
          script {
            sh "kubectl --kubeconfig=$KUBECONFIG_FILE apply -f k8s/06-prometheus.yaml"
            sh "kubectl --kubeconfig=$KUBECONFIG_FILE -n ${NAMESPACE} rollout status deployment/prometheus --timeout=120s"
            sh "kubectl --kubeconfig=$KUBECONFIG_FILE apply -f k8s/07-grafana.yaml"
            sh "kubectl --kubeconfig=$KUBECONFIG_FILE -n ${NAMESPACE} rollout status deployment/grafana --timeout=120s"
            echo "Prometheus disponible sur: http://<node-ip>:9090"
            echo "Grafana disponible sur: http://<node-ip>:3000 (admin/admin)"
          }
        }
      }
    }

    stage("Smoke Test - Verify Deployment") {
      steps {
        script {
          echo "Verification du déploiement..."
          sh "curl -f http://localhost:8761/actuator/health || echo 'Eureka check failed'"
          sh "curl -f http://localhost:8080/actuator/health || echo 'Gateway check failed'"
          sh "curl -f http://localhost:8091/actuator/health || echo 'Backend check failed'"
        }
      }
    }
  }

  post {
    success {
      echo "Pipeline reussi - Version ${TAG} deployee dans ${NAMESPACE}"
      emailext (
        subject: "SUCCESS: Pipeline ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}",
        body: "Le pipeline a reussi.\nVersion: ${TAG}\nNamespace: ${NAMESPACE}\n\nAcces:\n- Frontend: http://<node-ip>\n- Eureka: http://<node-ip>:8761\n- Prometheus: http://<node-ip>:9090\n- Grafana: http://<node-ip>:3000",
        to: "${env.CHANGE_AUTHOR_EMAIL ?: 'admin@example.com'}"
      )
    }
    failure {
      echo "Pipeline echoue - Verifier les logs"
      emailext (
        subject: "FAILURE: Pipeline ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}",
        body: "Le pipeline a echoue.\nVerifier les logs: ${env.BUILD_URL}console",
        to: "${env.CHANGE_AUTHOR_EMAIL ?: 'admin@example.com'}"
      )
    }
    always {
      cleanWs()
    }
  }
}


