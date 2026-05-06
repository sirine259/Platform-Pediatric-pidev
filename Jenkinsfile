pipeline {
  agent any

  tools {
    maven "Maven3"
    nodejs "NodeJS"
  }

  environment {
    DOCKERHUB_USER = "sirine215"
    IMAGE_FORUM = "${DOCKERHUB_USER}/forum-service"
    IMAGE_KIDNEY = "${DOCKERHUB_USER}/kidneytransplant-service"
    IMAGE_FRONTEND = "${DOCKERHUB_USER}/pediatric-frontend"
    TAG = "${BUILD_NUMBER}"
    NAMESPACE = "pediatric"
  }

  stages {

    stage("Checkout") {
      steps {
        checkout scm
      }
    }

    // 🔵 BUILD FORUM
    stage("Build Forum") {
      steps {
        dir("Back/forum-service") {
          bat "mvn clean install -DskipTests=true"
        }
      }
    }

    // 🔵 BUILD KIDNEY
    stage("Build Kidney") {
      steps {
        dir("Back/kidneytransplant-service") {
          bat "mvn clean install -DskipTests=true"
        }
      }
    }

    // 🟢 BUILD FRONT
    stage("Build Frontend") {
      steps {
        dir("Front") {
          bat "npm install --legacy-peer-deps"
          bat "npm run build"
        }
      }
    }

    // 🔍 SONAR FORUM
    stage("Sonar Forum") {
      steps {
        withSonarQubeEnv('SonarQube') {
          dir("Back/forum-service") {
            bat "mvn sonar:sonar -Dsonar.projectKey=forum"
          }
        }
      }
    }

    // 🔍 SONAR KIDNEY
    stage("Sonar Kidney") {
      steps {
        withSonarQubeEnv('SonarQube') {
          dir("Back/kidneytransplant-service") {
            bat "mvn sonar:sonar -Dsonar.projectKey=kidney"
          }
        }
      }
    }

    // 🐳 DOCKER BUILD
    stage("Docker Build") {
      steps {
        bat "docker build -t ${IMAGE_FORUM}:${TAG} Back/forum-service"
        bat "docker build -t ${IMAGE_KIDNEY}:${TAG} Back/kidneytransplant-service"
        bat "docker build -t ${IMAGE_FRONTEND}:${TAG} Front"
      }
    }

    // 🐳 PUSH
    stage("Docker Push") {
      steps {
        withCredentials([usernamePassword(credentialsId: "sirine215", usernameVariable: "USER", passwordVariable: "PASS")]) {
          bat "echo %PASS% | docker login -u %USER% --password-stdin"

          bat "docker push ${IMAGE_FORUM}:${TAG}"
          bat "docker push ${IMAGE_KIDNEY}:${TAG}"
          bat "docker push ${IMAGE_FRONTEND}:${TAG}"
        }
      }
    }

  }

  post {
    success {
      echo "Pipeline OK ✅ Version ${TAG}"
    }
    failure {
      echo "Pipeline FAILED ❌"
    }
  }
}
