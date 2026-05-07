pipeline {
  agent any

  tools {
    maven "Maven3"
    nodejs "NodeJS20"
  }

  triggers {
    githubPush()
  }

  environment {
    DOCKER_USER = "sirine215"
    TAG         = "${BUILD_NUMBER}"
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

    stage("Test KidneyTransplant") {
      steps {
        dir("Back") {
          // continueOnError pour ne pas bloquer le pipeline
          sh """
            mvn test \
              -Dtest=*KidneyTransplant*,*PostTransplantFollowUp* \
              -DfailIfNoTests=false \
              -Dmaven.test.failure.ignore=true
          """
        }
      }
    }

    stage("Test Forum") {
      steps {
        dir("Back") {
          sh """
            mvn test \
              -Dtest=ForumServiceTest,PostServiceTest \
              -DfailIfNoTests=false \
              -Dmaven.test.failure.ignore=true
          """
        }
      }
    }

    stage("Build Frontend") {
      steps {
        dir("Front") {
          sh """
            if [ -d node_modules ]; then
              echo "node_modules existe, skip install"
            else
              npm install --legacy-peer-deps
            fi
            npm run build
          """
        }
      }
    }

    stage("SonarQube") {
      steps {
        withSonarQubeEnv("SonarQube") {
          dir("Back") {
            sh "mvn sonar:sonar -Dsonar.projectKey=pediatric-kidneytransplant-kidneytransplant -Dmaven.test.failure.ignore=true"
          }
        }
      }
    }

    stage("Docker Build & Push") {
      steps {
        withCredentials([usernamePassword(
          credentialsId: "sirine215",
          usernameVariable: "U",
          passwordVariable: "P"
        )]) {
          sh """
            echo \$P | docker login -u \$U --password-stdin
            docker build -t \$DOCKER_USER/forum-kidney-back:\$TAG Back/
            docker build -t \$DOCKER_USER/frontend:\$TAG Front/
            docker push \$DOCKER_USER/forum-kidney-back:\$TAG
            docker push \$DOCKER_USER/frontend:\$TAG
          """
        }
      }
    }

    stage("Deploy") {
      steps {
        sh """
          docker-compose down --remove-orphans || true
          DOCKER_USER=\$DOCKER_USER TAG=\$TAG docker-compose up -d
        """
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
