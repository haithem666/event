pipeline {
    agent any

    tools {
        maven 'M2_HOME'       // Ensure "M2_HOME" is configured in Jenkins
        jdk 'JAVA_HOME'       // Ensure "JAVA_HOME" is configured in Jenkins
    }

    environment {
        GIT_URL = 'https://github.com/haithem666/event.git'
        GIT_BRANCH = 'devops'
        CREDENTIALS_ID = 'GitHub_Cred'
        SONAR_TOKEN = credentials('SonarQube_Token')
        DOCKER_IMAGE_NAME = 'haithem801/alpine'
        DOCKER_CREDENTIALS_ID = 'Docker_Cred'
    }

    stages {
        // --------- CI: Continuous Integration ---------
        stage('Checkout Code') {  // Récupération du code source
            steps {
                git branch: "${env.GIT_BRANCH}",
                    url: "${env.GIT_URL}",
                    credentialsId: "${env.CREDENTIALS_ID}"
            }
        }

        stage('Get Version') {  // Extraction de la version de l'application
            steps {
                script {
                    env.APP_VERSION = sh(script: "mvn help:evaluate -Dexpression=project.version -q -DforceStdout", returnStdout: true).trim()
                    echo "Application version: ${env.APP_VERSION}"
                }
            }
        }

        stage('Build') {  // Compilation et création du package
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Mockito Tests') {  // Exécution des tests unitaires
            steps {
                sh 'mvn test'
            }
        }

        stage('SonarQube Analysis') {  // Analyse du code avec SonarQube
            steps {
                sh 'mvn sonar:sonar'
            }
        }

        stage('Deploy to Nexus') {  // Déploiement des artefacts dans Nexus
            steps {
                script {
                    withEnv(["PATH+MAVEN=${MAVEN_HOME}/bin"]) {
                        sh 'mvn deploy -s /usr/share/maven/conf/settings.xml'
                    }
                }
            }
        }

        // --------- CD: Continuous Deployment ---------
        stage('Build Docker Image') {  // Construction de l'image Docker
            steps {
                script {
                    def dockerImage = docker.build("${DOCKER_IMAGE_NAME}:${env.APP_VERSION}")
                    echo "Built Docker Image: ${dockerImage.id}"
                }
            }
        }

        stage('Push Docker Image to Docker Hub') {  // Publication de l'image Docker dans Docker Hub
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: DOCKER_CREDENTIALS_ID,
                                                     usernameVariable: 'DOCKERHUB_USERNAME',
                                                     passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                        sh '''
                            echo "$DOCKERHUB_PASSWORD" | docker login -u "$DOCKERHUB_USERNAME" --password-stdin
                        '''
                        sh "docker push ${DOCKER_IMAGE_NAME}:${env.APP_VERSION}"
                        sh "docker logout"
                    }
                }
            }
        }

        stage('Debug Workspace') {  // Débogage du répertoire de travail
            steps {
                sh 'ls -l ${WORKSPACE}'
            }
        }

        stage('Docker compose (BackEnd MySql)') {  // Déploiement avec Docker Compose
            steps {
                script {
                    sh 'docker compose -f ${WORKSPACE}/Docker-compose.yml up -d'
                }
            }
        }
    }
}
