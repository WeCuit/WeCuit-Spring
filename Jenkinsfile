pipeline {
  agent any
  stages {

    stage('Prepare Resource File') {
      steps {
        sh 'cp -r /app/resources ./src/main'
      }
    }

    stage('Build Package') {
      steps {
        script {
          def maven = docker.image('registry.cn-hangzhou.aliyuncs.com/acs/maven:3-jdk-8')
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