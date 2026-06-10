pipeline {
    agent any

    environment {
        REPONAME = 'nikhil6066'
        IMAGE_NAME = 'flight-reservation-cdec-b50'
    }

    stages {

        stage('checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Nikhil-Suryawanshi07/flight-reservation-app.git'
            }
        }

        stage('build') {
            steps {
                sh '''
                    cd FlightReservationApplication
                    mvn clean package -DskipTests
                '''
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonar') {
                    sh '''
                        cd FlightReservationApplication
                        mvn sonar:sonar
                    '''
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                sh '''
                    cd FlightReservationApplication

                    docker build -t $REPONAME/$IMAGE_NAME:$BUILD_NUMBER .
                    docker push $REPONAME/$IMAGE_NAME:$BUILD_NUMBER
                '''
            }
        }

        stage('Deploy to EKS') {
            steps {
                sh '''
                    cd FlightReservationApplication

                    sed -i "s|image: mayurwagh/flight-reservation-app:latest|image: $REPONAME/$IMAGE_NAME:$BUILD_NUMBER|g" k8s/deployment.yaml

                    kubectl apply -f k8s/deployment.yaml
                    kubectl apply -f k8s/service.yaml
                '''
            }
        }
    }
}