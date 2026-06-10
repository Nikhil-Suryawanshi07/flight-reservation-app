pipeline{
    agent any
    stages{
        stage('Code-Pull'){
            steps{
                git branch: 'main', url: 'https://github.com/Nikhil-Suryawanshi07/flight-reservation-app.git'    
            }
        }
        stage('Code-Build'){
            steps{
                sh '''
                    cd frontend
                    npm install
                    npm run build
                '''
            }
        }
       stage('Deploy') {
         steps {
            withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws-creds']]) {
                sh '''
                    cd frontend
                    aws s3 sync dist/ s3://cbz-frontend-project-nikhil/ 
                '''
            }
        }
    }
    }
}