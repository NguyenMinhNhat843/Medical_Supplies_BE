pipeline {
  agent any

  stages {
    stage('Clone source') {
      steps {
        echo 'Cloning source code...'
      }
    }

    stage('Build toàn bộ Docker images') {
      steps {
        script {
          if (isUnix()) {
            sh 'docker compose build'
          } else {
            bat 'docker compose build'
          }
        }
      }
    }

    stage('Restart toàn bộ hệ thống') {
      steps {
        script {
          if (isUnix()) {
            sh 'docker compose down'
            sh 'docker compose up -d'
          } else {
            bat 'docker compose down'
            bat 'docker compose up -d'
          }
        }
      }
    }
  }

  post {
    success {
      echo '✅ Hệ thống đã triển khai thành công!'
    }
    failure {
      echo '❌ Có lỗi xảy ra trong quá trình CI/CD!'
    }
  }
}
