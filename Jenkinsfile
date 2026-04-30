pipeline {
  agent any
  options {
    timestamps()
    disableConcurrentBuilds()
    buildDiscarder(logRotator(numToKeepStr: "10"))
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
    stage("Checkout") {
      steps {
        checkout scm
      }
    }

    stage("Build & Test Backend") {
      parallel {
        stage("Build & Test Eureka") {
          steps {
            sh "cd Back/eureka-server && mvn clean test"
          }
        }
        stage("Build & Test Gateway") {
          steps {
            sh "cd Back/api-gateway && mvn clean test"
          }
        }
        stage("Build & Test Backend Service") {
          steps {
            sh "cd Back && mvn clean test"
          }
        }
      }
    }

    stage("Build & Test Frontend") {
      steps {
        sh "cd Front && npm ci && npm run test -- --watch=false --browsers=ChromeHeadless"
      }
    }

    stage("Build Docker Images (parallel)") {
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

    stage("Docker Login & Push") {
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
              sh """
                kubectl --kubeconfig=$KUBECONFIG_FILE -n ${NAMESPACE} delete secret dockerhub-creds --ignore-not-found
                kubectl --kubeconfig=$KUBECONFIG_FILE -n ${NAMESPACE} create secret docker-registry dockerhub-creds \
                  --docker-server=https://index.docker.io/v1/ \
                  --docker-username=$DOCKER_USER \
                  --docker-password=$DOCKER_PASS
              """
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
  }

  post {
    success {
      echo "Pipeline reussi - Version ${TAG} deployee dans ${NAMESPACE}"
    }
    failure {
      echo "Pipeline echoue - Verifier les logs"
    }
  }
}
