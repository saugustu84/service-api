#!groovy

node {

    load "$JENKINS_HOME/jobvars.env"

    stage('Checkout') {
        checkout scm
    }
    stage('Prepare') {
        sh './gradlew wrapper clean resolveDependencies'
    }
    stage('Tests') {
        sh './gradlew test --full-stacktrace'
    }
    stage('Build') {
        sh './gradlew build'
    }
    stage('Docker Image') {
        sh "./gradlew buildDocker -PdockerServerUrl=$DOCKER_HOST"
    }
    stage('Deploy Container') {
        docker.withServer("$DOCKER_HOST") {
            sh "docker-compose -p reportportal5 -f $COMPOSE_FILE_RP_5 up -d --force-recreate api"
        }
    }

}
