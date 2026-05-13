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

    stage("Build Frontend") {
      steps {
        dir("Front") {
          sh """
            npm install --legacy-peer-deps
            ./node_modules/.bin/ng build --configuration=production
          """
        }
      }
    }

    stage("SonarQube") {
      steps {
        dir("Back") {
          sh """
            mvn sonar:sonar \
              -Dsonar.host.url=http://host.docker.internal:9000 \
              -Dsonar.login=squ_93c7014b0f93b700ea8484df5597cc4ed6ee8ae5 \
              -Dsonar.projectKey=PlatformePediatricBack \
              -DskipTests=true
          """
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
      echo "Webhook GitHub → Jenkins ✅"
    }
    failure {
      echo "Pipeline FAILED ❌"
    }
    always {
      echo "Done ✔"
    }
  }
}
