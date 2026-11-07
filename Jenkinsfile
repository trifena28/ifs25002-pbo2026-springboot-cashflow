 pipeline {
    agent any

    environment {
        MAVEN_OPTS = '-Dmaven.repo.local=.m2/repository'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build') {
            steps {
                sh 'chmod +x mvnw && ./mvnw -Dmaven.repo.local=$WORKSPACE/.m2/repository clean package -DskipTests'
            }
        }
        stage('Test') {
            steps {
                sh './mvnw -Dmaven.repo.local=$WORKSPACE/.m2/repository test'
            }
        }
    }

    post {
        success {
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        }
    }
}
