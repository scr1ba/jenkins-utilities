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
    def packageManager = config.packageManager ?: 'npm'
    def toolsConfig = config.tools ?: [:]
    def stagesConfig = config.stages ?: []

    def toolsBlock = toolsConfig.collect { tool, version -> "${tool} '${version}'" }.join('\n')

    def stagesBlock = stagesConfig.collect { stageConfig ->
        """
        stage('${stageConfig.name}') {
            steps {
                script {
                    ${stageConfig.step}
                }
            }
        }
        """
    }.join('\n')

    def pipelineScript = """
        pipeline {
            agent any
            tools {
                ${toolsBlock}
            }
            stages {
                ${stagesBlock}
            }
        }
    """

    return evaluate(pipelineScript)
}
