pipeline {
  agent any

  tools {
    maven "Maven3"
    nodejs "NodeJS20"   // ✅ important pour npm
  }

  environment {
    DOCKER_USER = "sirine215"
    TAG = "${BUILD_NUMBER}"
  }

  stages {

    stage("Checkout") {
      steps {
        checkout scm
      }
    }

    // 🔵 BACKEND (forum + kidney ensemble)
    stage("Build Backend") {
      steps {
        dir("Back") {
          bat "mvn clean install -DskipTests"
        }
      }
    }

    // 🟢 FRONTEND
    stage("Build Frontend") {
      steps {
        dir("Front") {
          bat "npm install --legacy-peer-deps"
          bat "npm run build"
        }
      }
    }

    // 🔍 SONAR (backend uniquement)
    stage("SonarQube") {
      steps {
        withSonarQubeEnv('SonarQube') {   // ⚠️ nom serveur pas credentials
          dir("Back") {
            bat "mvn sonar:sonar -Dsonar.projectKey=forum-kidney"
          }
        }
      }
    }

    // 🐳 DOCKER
    stage("Docker Build & Push") {
      steps {
        withCredentials([usernamePassword(credentialsId: "sirine215", usernameVariable: "U", passwordVariable: "P")]) {
          bat """
            docker build -t %DOCKER_USER%/forum-kidney-back:%TAG% Back/
            docker build -t %DOCKER_USER%/frontend:%TAG% Front/
            
            echo %P% | docker login -u %U% --password-stdin
            
            docker push %DOCKER_USER%/forum-kidney-back:%TAG%
            docker push %DOCKER_USER%/frontend:%TAG%
          """
        }
      }
    }

    // ☸️ DEPLOY
    stage("Deploy Kubernetes") {
      steps {
        withCredentials([file(credentialsId: "pediatric medical", variable: "K8S")]) {
          bat "kubectl --kubeconfig=%K8S% apply -f k8s/"
        }
      }
    }

  }

  post {
    success {
      echo "Pipeline SUCCESS ✅"
    }
    failure {
      echo "Pipeline FAILED ❌"
    }
    always {
      echo "Done ✔"
    }
  }
}
