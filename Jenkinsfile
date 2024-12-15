pipeline {
    agent any
    environment {
        AWS_CREDENTIALS = 'aws'
    }
    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'debug', url: 'https://github.com/SixCosti/petclinic-automation.git'
            }
        }
        stage('Frontend Tests') {
            steps {
                dir('spring-petclinic-angular') {
                    sh 'apk update && apk add --no-cache'
                    sh 'python3'
                    sh 'py3-pip'
                    sh 'build-base'
                    sh 'npm install -g node-gyp'
                    sh 'npm install -g @angular/cli@16'
                    sh 'npm install'
                    sh 'ng test --watch=false'
                }
            }
        }
        stage('Backend Tests') {
            steps {
                dir('spring-petclinic-rest') {
                    sh './mvnw test'
                }
            }
        }
        stage('Terraform Init & Apply') {
            steps {
                dir('petclinic-infra') {
                script {
                    sh 'terraform init'
                    sh 'terraform apply -auto-approve'
                }
            }
        }
    }    
        stage('Ansible Configuration') {
            steps {
                script {
                    ansiblePlaybook playbook: 'petclinic-infra/ansible/main.yml'
                }
            }
        }
    }
    post {
        always {
            cleanWs()
        }
        success {
            echo 'Pipeline execution was successful!'
        }
        failure {
            echo 'Pipeline failed, please check the logs for errors.'
        }
    }
}
