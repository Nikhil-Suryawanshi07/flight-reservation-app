pipeline{
    agent any 

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
                    mvn sonar:sonar -Dsonar.projectKey=flight-reservation-app 
                '''
                }
            }
        }
    }
}