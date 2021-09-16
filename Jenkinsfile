pipeline {
  agent any
  stages {
    stage('Build Package') {
      steps {
        script {
          def maven = docker.image('maven:ibmjava-alpine')
          maven.pull() // make sure we have the latest available from Docker Hub
          maven.inside('-v /root/.m2:/root/.m2') {
            // …as above
            sh 'ls -l'
            sh 'mvn -B -DskipTests clean package'
          }
        }

      }
    }

    stage('Copy Jar File') {
      steps {
        sh 'cp ./target/*.jar /app/app.jar'
      }
    }

    stage('Restart Docker') {
      steps {
        sh 'docker restart server_spring'
      }
    }

  }
}