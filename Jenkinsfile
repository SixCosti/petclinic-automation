pipeline {
    agent any
    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'debug', url: 'https://github.com/SixCosti/petclinic-automation.git'
            }
        }
        stage('Frontend Tests') {
            agent {
                docker { image 'node:16' }  // Using Node.js Docker image
            }
            steps {
                dir('spring-petclinic-angular') {
                    sh '''
                    # Clean npm cache and fix any potential permission issues
                    npm cache clean --force
                    rm -rf node_modules package-lock.json

                    # Install dependencies
                    npm install --legacy-peer-deps

                    # Run Angular tests using Karma
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
