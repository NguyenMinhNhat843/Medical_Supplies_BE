pipeline {
  agent any

  stages {
    stage('Clone source') {
      steps {
        echo 'Cloning source code...'
        // Nếu dùng “Pipeline script from SCM”, đoạn này có thể bỏ
      }
    }

    stage('Build toàn bộ Docker images') {
      steps {
        echo 'Building Docker images using docker-compose...'
        bat 'docker compose build'
      }
    }

    stage('Restart toàn bộ hệ thống') {
      steps {
        echo 'Stopping old containers and restarting the system...'
        bat 'docker compose down'
        bat 'docker compose up -d'
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
