pipeline {
    agent any

    environment environment environment {
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
                withSonarQubeEnv(credentialsId: 'sonar-cred', installationName: 'sonar') {
                    sh '''
                        export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
                    export PATH=$JAVA_HOME/bin:$PATH

                    java -version
                    javac -version
                    mvn -version

                    cd FlightReservationApplication
                    mvn clean package -DskipTests
                    '''
                }
            }
        }

        stage('Dockerbuild') {
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
            kubectl set image deployment/flight-reservation-app \
            flight-reservation-app=$REPONAME/$IMAGE_NAME:$BUILD_NUMBER

            kubectl rollout status deployment/flight-reservation-app
        '''
    }
}
    }
}