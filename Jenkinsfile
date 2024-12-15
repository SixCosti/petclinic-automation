pipeline {
    agent any
    tools {
        nodejs 'NodeJS 18'  // NodeJS 18 tool configured in Jenkins
    }
    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'debug', url: 'https://github.com/SixCosti/petclinic-automation.git'
            }
        }

        // stage('Install Dependencies') {
        //     steps {
        //         dir('spring-petclinic-angular') {
        //             sh '''
        //             sudo yum install -y chromium

        //             export CHROME_BIN=/usr/bin/chromium-browser

        //             npm cache clean --force
        //             rm -rf node_modules package-lock.json

        //             npm install --legacy-peer-deps
        //             '''
        //         }
        //     }
        // }

        // stage('Frontend Tests') {
        //     steps {
        //         dir('spring-petclinic-angular') {
        //             sh '''
        //             export CHROME_BIN=/usr/bin/chromium-browser
        //             ng test --watch=false --browsers=ChromeHeadless
        //             '''
        //         }
        //     }
        // }

        // stage('Backend Tests') {
        //     steps {
        //         dir('spring-petclinic-rest') {
        //             sh './mvnw test'
        //         }
        //     }
        // }

        stage('Copy terraform.tfvars from Secret File') {
            steps {
                script {
                    def tfvarsFile = credentialsFile('tfvars')  // 'tfvars' is the secret file ID
                    
                    sh "cp ${tfvarsFile} petclinic-infra/terraform.tfvars"
                    echo 'Copied terraform.tfvars to petclinic-infra folder.'
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
