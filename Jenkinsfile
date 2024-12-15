pipeline {
    agent any
    // tools {
    //     nodejs 'NodeJS 18'  // NodeJS 18 tool configured in Jenkins
    // }
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
                withCredentials([file(credentialsId: 'tfvars', variable: 'TFVARS_FILE')]) {
                    sh '''
                    cp $TFVARS_FILE petclinic-infra/terraform.tfvars
                    echo 'Copied terraform.tfvars to petclinic-infra folder.'
                    '''
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

        stage('Setup PEM Key') {
            steps {
                withCredentials([file(credentialsId: 'test-pem-key', variable: 'PEM_FILE')]) {
                    sh '''
                    mkdir -p ~/.ssh
                    cp $PEM_FILE ~/.ssh/test.pem
                    chmod 400 ~/.ssh/test.pem
                    echo "PEM key has been copied and permissions set."
                    '''
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
