pipeline {
    agent any
    tools {
        gradle 'gradle7'
        jdk 'jdk17'
    }
    stages {
        stage('Clone Git Repo') {
            steps {
                cleanWs()
                git branch: 'master',
                url: 'https://gitlab.com/KonChoo/spring-boot-empty-project.git'
            }
        }
        stage('Build and Push Docker Image') {
                steps {
                sh 'env'
                sh 'pwd'
                sh 'ls'
                sh 'gradle build -b build.gradle -x test'
                sh 'docker --version'
                sh 'docker build --build-arg JAR_FILE=build/libs/*.jar -t 192.168.0.111:8050/spring-boot-empty-project .'
                sh 'docker push 192.168.0.111:8050/spring-boot-empty-project'  
            }
        }
        stage('Deploy to Kubernetes Cluster') {
            steps {
                    sh 'kubectl apply -f deployment.yaml'
            }
        }
    }
}
