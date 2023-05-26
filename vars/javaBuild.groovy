// Usage: javaBuild(mvnCommand: 'mvn', tools: [jdk: 'JDK 18 LTS', maven: 'Default'])
def call(Map config = [:]) {
    // Default to './mvnw' if no Maven command is specified
    String mvnCommand = config.mvnCommand ?: './mvnw'

    // Optional tools configuration
    Map toolsConfig = config.tools ?: [:]

    pipeline {
        agent any
        tools {
            // If a JDK version is specified, use it
            if (toolsConfig.jdk) {
                jdk toolsConfig.jdk
            }
            // If a Maven version is specified, use it
            if (toolsConfig.maven) {
                maven toolsConfig.maven
            }
        }
        stages {
            stage('Checkout') {
                steps {
                    checkout scm
                }
            }
            stage('Build') {
                steps {
                    sh "${mvnCommand} clean install"
                }
            }
            // Additional stages as needed...
        }
    }
}
