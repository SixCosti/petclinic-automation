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
            agent {
                docker { image 'trion/ng-cli:14.0.0' }  // Angular CLI Docker image
            }
            steps {
                dir('spring-petclinic-angular') {
                    sh '''
                    # Run Angular unit tests
                    ng test --watch=false
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
