pipeline {
    agent any
    environment {
        // Set AWS credentials for Terraform to use S3 bucket
        AWS_ACCESS_KEY_ID     = credentials('aws')  // Assume 'aws' is the ID of your AWS credentials in Jenkins
        AWS_SECRET_ACCESS_KEY = credentials('aws')  // Same as above for the secret key
        AWS_DEFAULT_REGION    = 'eu-west-1'          // Set the region for AWS (adjust as needed)
        S3_BUCKET            = 'terraform-state-bucket-00'  // S3 bucket where inventory.ini is stored
        INVENTORY_FILE_PATH  = 'petclinic-infra/ansible/inventory.ini' // Path to inventory file in the workspace
    }
    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'debug', url: 'https://github.com/SixCosti/petclinic-automation.git'
            }
        }

        stage('Download Inventory from S3') {
            steps {
                script {
                    // Check if the inventory.ini exists in S3 and download it
                    sh """
                        aws s3 cp s3://${S3_BUCKET}/inventory.ini ${INVENTORY_FILE_PATH} || echo "No inventory.ini found, will create one."
                    """
                }
            }
        }

        stage('Setup PEM Key') {
            steps {
                withCredentials([file(credentialsId: 'test-pem-key', variable: 'PEM_FILE')]) {
                    sh '''
                    sudo mkdir -p ~/.ssh
                    sudo cp $PEM_FILE ~/.ssh/test.pem
                    sudo chmod 400 ~/.ssh/test.pem
                    echo "PEM key has been copied and permissions set."
                    '''
                }
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
                        sh 'terraform init' 
                        // Run Terraform apply
                        sh 'terraform apply -auto-approve'
                    }
                }
            }
        }

        stage('Ansible Configuration') {
            steps {
                script {
                    dir('petclinic-infra/ansible') {  // Change to the correct directory
                        ansiblePlaybook(
                            playbook: 'main.yaml',  // Now the relative path is from 'petclinic-infra/ansible'
                            inventory: 'inventory.ini',  // Same for the inventory
                            extraVars: [ansible_verbosity: '-v']  // Add verbosity if needed
                        )
                    }
                }
            }
        }

        stage('Upload Inventory to S3') {
            steps {
                script {
                    // After Terraform and Ansible run, upload the updated inventory.ini to S3
                    sh "aws s3 cp ${INVENTORY_FILE_PATH} s3://${S3_BUCKET}/inventory.ini"
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
