/** Usage in a Jenkinsfile:
nodeBuild(
    packageManager: 'pnpm',
    tools: [nodejs: 'NodeJS 18 LTS'],
    stages: [
        [
            name: 'Checkout',
            step: {
                git branch: 'main', url: 'https://github.com/nuxt/nuxt.git'
            }
        ],
        [
            name: 'Install',
            step: {
                sh 'pnpm install --shamefully-hoist'
            }
        ],
        [
            name: 'Test',
            step: {
                sh 'pnpm test:fixtures'
            }
        ]
    ]
)
**/

def call(Map config = [:]) {
    // Default to 'npm' if no package manager is specified
    String packageManager = config.packageManager ?: 'npm'

    // Optional tools configuration
    Map toolsConfig = config.tools ?: [:]

    // Stages configuration
    List stagesConfig = config.stages ?: []

    pipeline {
        agent any
        tools {
            // If a Node.js version is specified, use it
            if (toolsConfig.nodejs) {
                nodejs toolsConfig.nodejs
            }
        }
        stages {
            stagesConfig.each { stageConfig ->
                stage(stageConfig.name) {
                    steps {
                        script {
                            stageConfig.step()
                        }
                    }
                }
            }
        }
    }
}
