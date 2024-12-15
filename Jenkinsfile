pipeline {
    agent any
    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'debug', url: 'https://github.com/SixCosti/petclinic-automation.git'
            }
        }
        stage('Install Dependencies') {
            agent {
                docker { image 'node:16.14-alpine' }  // Use the same Node.js version as in your Dockerfile
            }
            steps {
                dir('spring-petclinic-angular') {
                    sh '''
                    # Install necessary dependencies
                    apk update && apk add --no-cache python3 py3-pip build-base
                    npm install -g node-gyp
                    npm install -g @angular/cli@16  # Install Angular CLI globally

                    # Clean npm cache and node_modules to ensure a fresh start
                    npm cache clean --force
                    rm -rf node_modules package-lock.json

                    # Install project dependencies
                    npm install --legacy-peer-deps
                    '''
                }
            }
        }
        stage('Frontend Tests') {
            agent {
                docker { image 'node:16.14-alpine' }  // Use the same Node.js version as in your Dockerfile
            }
            steps {
                dir('spring-petclinic-angular') {
                    sh '''
                    # Run Angular tests using Karma and ChromeHeadless
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
