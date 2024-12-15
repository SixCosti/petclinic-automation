pipeline {
    agent any
    environment {
        // Set AWS credentials for Terraform to use S3 bucket
        AWS_ACCESS_KEY_ID     = credentials('aws')  // Assume 'aws' is the ID of your AWS credentials in Jenkins
        AWS_SECRET_ACCESS_KEY = credentials('aws')  // Same as above for the secret key
        AWS_DEFAULT_REGION    = 'eu-west-1'          // Set the region for AWS (adjust as needed)
    }
    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'debug', url: 'https://github.com/SixCosti/petclinic-automation.git'
            }
        }

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
                        // Initialize Terraform to use the S3 bucket as the backend
                        sh '''
                        terraform init \
                          -backend-config="bucket=terraform-state-bucket-00" \
                          -backend-config="key=terraform-tfstate/terraform.tfstate" \
                          -backend-config="region=${AWS_DEFAULT_REGION}" \
                          -backend-config="encrypt=true" \
                        '''
                        // Run Terraform apply
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
