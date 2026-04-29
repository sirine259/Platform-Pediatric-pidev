pipeline {
  agent any
  options {
    timestamps()
    disableConcurrentBuilds()
    buildDiscarder(logRotator(numToKeepStr: '10'))
  }

  environment {
    NAMESPACE = "pediatric"
    TAG = "${env.BUILD_NUMBER}"
    DOCKERHUB_USER = "sirine215"

    IMAGE_EUREKA = "${DOCKERHUB_USER}/pediatric-eureka-server"
    IMAGE_GATEWAY = "${DOCKERHUB_USER}/pediatric-api-gateway"
    IMAGE_BACKEND = "${DOCKERHUB_USER}/pediatric-kidneytransplant-forum-service"
    IMAGE_FRONTEND = "${DOCKERHUB_USER}/pediatric-frontend"
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build & Test Backend') {
      parallel {
        stage('Build & Test Eureka') {
          steps {
            bat "cd Back\\eureka-server && mvn clean test"
          }
        }
        stage('Build & Test Gateway') {
          steps {
            bat "cd Back\\api-gateway && mvn clean test"
          }
        }
        stage('Build & Test Backend Service') {
          steps {
            bat "cd Back && mvn clean test"
          }
        }
      }
    }

    stage('Build & Test Frontend') {
      steps {
        bat "cd Front && npm ci && npm run test -- --watch=false --browsers=ChromeHeadless"
      }
    }

    stage('Build Docker Images (parallel)') {
      parallel {
        stage('Build Backend Images') {
          steps {
            script {
              bat "docker build -t %IMAGE_EUREKA%:%TAG% -f \"Back/eureka-server/Dockerfile\" \"Back/eureka-server\""
              bat "docker build -t %IMAGE_GATEWAY%:%TAG% -f \"Back/api-gateway/Dockerfile\" \"Back/api-gateway\""
              bat "docker build -t %IMAGE_BACKEND%:%TAG% -f \"Back/Dockerfile\" \"Back\""
            }
          }
        }
        stage('Build Frontend Image') {
          steps {
            script {
              bat "docker build -t %IMAGE_FRONTEND%:%TAG% -f \"Front/Dockerfile\" \"Front\""
            }
          }
        }
      }
    }

    stage('Docker Login & Push') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'sirine215', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
          script {
            bat "echo %DOCKER_PASS% | docker login -u %DOCKER_USER% --password-stdin"

            bat "docker push %IMAGE_EUREKA%:%TAG%"
            bat "docker push %IMAGE_GATEWAY%:%TAG%"
            bat "docker push %IMAGE_BACKEND%:%TAG%"
            bat "docker push %IMAGE_FRONTEND%:%TAG%"

            // Also tag and push as latest
            bat "docker tag %IMAGE_EUREKA%:%TAG% %IMAGE_EUREKA%:latest"
            bat "docker tag %IMAGE_GATEWAY%:%TAG% %IMAGE_GATEWAY%:latest"
            bat "docker tag %IMAGE_BACKEND%:%TAG% %IMAGE_BACKEND%:latest"
            bat "docker tag %IMAGE_FRONTEND%:%TAG% %IMAGE_FRONTEND%:latest"

            bat "docker push %IMAGE_EUREKA%:latest"
            bat "docker push %IMAGE_GATEWAY%:latest"
            bat "docker push %IMAGE_BACKEND%:latest"
            bat "docker push %IMAGE_FRONTEND%:latest"
          }
        }
      }
    }

    stage('Deploy to Kubernetes') {
      steps {
        withCredentials([file(credentialsId: 'pediatric medical', variable: 'KUBECONFIG_FILE')]) {
          script {
            // Apply namespace and secrets
            bat "kubectl --kubeconfig=%KUBECONFIG_FILE% apply -f \"k8s/00-namespace.yaml\""

            withCredentials([usernamePassword(credentialsId: 'sirine215', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
              bat """
                kubectl --kubeconfig=%KUBECONFIG_FILE% -n %NAMESPACE% delete secret dockerhub-creds --ignore-not-found
                kubectl --kubeconfig=%KUBECONFIG_FILE% -n %NAMESPACE% create secret docker-registry dockerhub-creds ^
                  --docker-server=https://index.docker.io/v1/ ^
                  --docker-username=%DOCKER_USER% ^
                  --docker-password=%DOCKER_PASS%
              """
            }

            // Apply all K8s manifests
            bat "kubectl --kubeconfig=%KUBECONFIG_FILE% apply -f k8s/"

            // Update images with rollout status
            bat "kubectl --kubeconfig=%KUBECONFIG_FILE% -n %NAMESPACE% set image deployment/eureka-server eureka-server=%IMAGE_EUREKA%:%TAG% --record"
            bat "kubectl --kubeconfig=%KUBECONFIG_FILE% -n %NAMESPACE% rollout status deployment/eureka-server --timeout=180s"

            bat "kubectl --kubeconfig=%KUBECONFIG_FILE% -n %NAMESPACE% set image deployment/api-gateway api-gateway=%IMAGE_GATEWAY%:%TAG% --record"
            bat "kubectl --kubeconfig=%KUBECONFIG_FILE% -n %NAMESPACE% rollout status deployment/api-gateway --timeout=180s"

            bat "kubectl --kubeconfig=%KUBECONFIG_FILE% -n %NAMESPACE% set image deployment/kidneytransplant-forum-service kidneytransplant-forum-service=%IMAGE_BACKEND%:%TAG% --record"
            bat "kubectl --kubeconfig=%KUBECONFIG_FILE% -n %NAMESPACE% rollout status deployment/kidneytransplant-forum-service --timeout=180s"

            bat "kubectl --kubeconfig=%KUBECONFIG_FILE% -n %NAMESPACE% set image deployment/frontend frontend=%IMAGE_FRONTEND%:%TAG% --record"
            bat "kubectl --kubeconfig=%KUBECONFIG_FILE% -n %NAMESPACE% rollout status deployment/frontend --timeout=180s"
          }
        }
      }
    }
  }

  post {
    success {
      echo "✅ Pipeline succeeded - Version ${TAG} deployed to ${NAMESPACE}"
    }
    failure {
      echo "❌ Pipeline failed - Check logs above"
    }
  }
}
