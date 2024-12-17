pipeline {
    agent any
    tools {
        nodejs 'NodeJS 18'  // Use of NodeJS 18 tool configured in Jenkins
    }
    environment {
        // Set AWS credentials for Terraform to use S3 bucket
        AWS_ACCESS_KEY_ID     = credentials('aws')  
        AWS_SECRET_ACCESS_KEY = credentials('aws')  
        AWS_DEFAULT_REGION    = 'eu-west-1'          
        S3_BUCKET            = 'terraform-state-bucket-00'  
        INVENTORY_FILE_PATH  = 'petclinic-infra/ansible/inventory.ini'
        DOCKERHUB_CREDENTIALS = credentials('docker-credentials') 
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

        // stage('Build and Push Frontend Image') {
        //     steps {
        //         dir('spring-petclinic-angular') {
        //             withCredentials([usernamePassword(credentialsId: 'docker-credentials', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
        //                 sh '''
        //                 docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD
        //                 docker build -t costi0/pet-clinic-frontend:latest .
        //                 docker push costi0/pet-clinic-frontend:latest
        //                 '''
        //             }
        //         }
        //     }
        // }

        // stage('Build and Push Backend Image') {
        //     steps {
        //         dir('spring-petclinic-rest') {
        //             withCredentials([usernamePassword(credentialsId: 'docker-credentials', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
        //                 sh '''
        //                 docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD
        //                 docker build -t costi0/pet-clinic-backend:latest .
        //                 docker push costi0/pet-clinic-backend:latest
        //                 '''
        //             }
        //         }
        //     }
        // }        

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
                        sh 'terraform init' 
                        sh 'terraform apply -auto-approve'
                    }
                }
            }
        }

        stage('Upload Inventory to S3') {
            steps {
                script {
                    // Upload the updated inventory.ini back to S3
                    sh "aws s3 cp ${INVENTORY_FILE_PATH} s3://${S3_BUCKET}/inventory.ini"
                }
            }
        }

        stage('Ansible Configuration') {
            steps {
                script {
                    dir('petclinic-infra/ansible') {  
                        // Disable host key checking using ANSIBLE_SSH_ARGS
                        withEnv(['ANSIBLE_SSH_ARGS=-o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null']) {
                            ansiblePlaybook(
                                playbook: 'main.yaml',  
                                inventory: 'inventory.ini',  
                                extraVars: [ansible_verbosity: '-v']  
                            )
                    }
                }
            }
        }
    }

stage('Security Scan with OWASP ZAP') {
    steps {
        script {
            def appServerIP = sh(script: "awk '/\\[app\\]/ {getline; print}' ${INVENTORY_FILE_PATH} | cut -d' ' -f1", returnStdout: true).trim()

            def frontendURL = "http://${appServerIP}:8080"
            def backendURL = "http://${appServerIP}:9966"

            def frontendExitCode = sh(script: """
                sudo docker run --rm -v /tmp/zap-reports:/zap/wrk:rw \
                zaproxy/zap-stable zap-baseline.py -t ${frontendURL} || echo \$?
            """, returnStdout: true).trim()

            def backendExitCode = sh(script: """
                sudo docker run --rm -v /tmp/zap-reports:/zap/wrk:rw \
                zaproxy/zap-stable zap-baseline.py -t ${backendURL} || echo \$?
            """, returnStdout: true).trim()

            if (frontendExitCode == '2' || backendExitCode == '2') {
                echo "Scan completed with warnings, but pipeline will continue."
            } else if (frontendExitCode != '0' || backendExitCode != '0') {
                error "One of the scans failed. Check the logs for details."
            }
        }
    }    
    
    post {
        always {
            echo 'OWASP ZAP Scan completed.'
        }
        // success {
        //     echo 'OWASP ZAP Scan completed successfully.'
        // }
        // failure {
        //     echo 'OWASP ZAP Scan failed. Check the logs for details.'
        // }
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
