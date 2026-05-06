pipeline {
  agent any

  tools {
    maven "Maven3"
    nodejs "NodeJS20"
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

    stage("Build Backend") {
      steps {
        dir("Back") {
          sh "mvn clean install -DskipTests"
        }
      }
    }

    stage("Build Frontend") {
      steps {
        dir("Front") {
          sh "npm install --legacy-peer-deps"
          sh "npm run build"
        }
      }
    }

    stage("SonarQube") {
      steps {
        withSonarQubeEnv('SonarQube') {
          dir("Back") {
            sh "mvn sonar:sonar -Dsonar.projectKey=forum-kidney"
          }
        }
      }
    }

    stage("Docker Build & Push") {
      steps {
        withCredentials([
          usernamePassword(
            credentialsId: "sirine215",
            usernameVariable: "U",
            passwordVariable: "P"
          )
        ]) {
          sh '''
            docker build -t $DOCKER_USER/forum-kidney-back:$TAG Back/
            docker build -t $DOCKER_USER/frontend:$TAG Front/

            echo $P | docker login -u $U --password-stdin

            docker push $DOCKER_USER/forum-kidney-back:$TAG
            docker push $DOCKER_USER/frontend:$TAG
          '''
        }
      }
    }

    stage("Deploy Kubernetes") {
      steps {
        withCredentials([
          file(credentialsId: "pediatric medical", variable: "K8S")
        ]) {
          sh "kubectl --kubeconfig=$K8S apply -f k8s/"
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
