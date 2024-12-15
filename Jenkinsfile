pipeline {
    agent any
    environment {
        AWS_CREDENTIALS = 'aws'  // If you're using AWS credentials, you can add them here
    }
    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'debug', url: 'https://github.com/SixCosti/petclinic-automation.git'
            }
        }
        stage('Frontend Tests') {
            agent {
                docker { image 'node:18' }  // Use Node.js 18 Docker image (or a custom Angular image)
            }
            steps {
                dir('spring-petclinic-angular') {
                    sh '''
                    export NPM_CONFIG_CACHE=/tmp/.npm

                    npm cache clean --force
                    rm -rf node_modules package-lock.json

                    npm install --legacy-peer-deps

                    ng test --watch=false --browsers=ChromeHeadless
                    '''
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
            cleanWs()  // Clean workspace after build
        }
        success {
            echo 'Pipeline execution was successful!'
        }
        failure {
            echo 'Pipeline failed, please check the logs for errors.'
        }
    }
}
