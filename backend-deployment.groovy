pipeline{
    agent any 
    environment {
        REPONAME = 'mayurmwagh/'
        IMAGE_NAME = 'flight-reservation-CDEC-B50'
    }

    stages{
        stage('checkout'){
            steps{
                 git branch: 'main', url: 'https://github.com/mayurmwagh/flight-reservation-app.git' 
            }

        }
        stage('build'){
            steps{
                sh '''
                    cd FlightReservationApplication
                    mvn clean package 
                '''
            }
        }
        stage('SonarQube Analysis'){
            steps{
                withSonarQubeEnv(credentialsId: 'sonar-cred', installationName: 'sonar') {
                sh '''
                    cd FlightReservationApplication
                    mvn sonar:sonar -Dsonar.projectKey=flight-reservation
                '''
                }
            }
        }
        stage('Dockerbuild'){
            steps{
                sh '''
                    cd FlightReservationApplication
                    docker build -t $REPONAME/$IMAGE_NAME:$BUILD_NUMBER .
                    docker push $REPONAME/$IMAGE_NAME:$BUILD_NUMBER
                '''
            }
        }
        stage('Deploy to EKS'){
            steps{
                sh '''
                    cd FlightReservationApplication
                    kubectl apply -f k8s/*
                '''
            }
        }
    }
}