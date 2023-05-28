/** Usage in a Jenkinsfile:

nodeBuild(
    packageManager: 'pnpm',
    tools: [nodejs: 'NodeJS 18 LTS'],
    parameters: [
        [type: 'string', name: 'MESSAGE', defaultValue: 'hello_world', description: 'Message to print']
    ],
    stages: [
        [
            name: 'Print message',
            step: '''
                sh 'echo "${params.MESSAGE}"
            '''
        ],
        [
            name: 'Checkout',
            step: '''
                git branch: 'main', url: 'https://github.com/scr1ba/python-placeholder.git'
            '''
        ],
        [
            name: 'Install',
            step: '''
                sh 'pnpm install --shamefully-hoist'
            '''
        ],
        [
            name: 'Test',
            step: '''
                sh 'pnpm test:fixtures'
            '''
        ],
         [
             name: 'Build Image',
             step: '''
                 sh 'docker build -t tiny-python-placeholder .'
             '''
         ],
          [
              name: 'Push Image',
              step: '''
                  sh 'docker tag tiny-python-placeholder europe-west4-docker.pkg.dev/euphoric-oath-385300/node-artifact-registry/tiny-python-placeholder:test-tag'
                  sh 'docker push europe-west4-docker.pkg.dev/euphoric-oath-385300/node-artifact-registry/tiny-python-placeholder:test-tag'
              '''
          ]
    ],
    post: [
        failure: '''
            sendFailureNotification('#cicd')
        ''',
        success: '''
            sendSuccessNotification('#cicd')
        ''',
    ]
)

**/

def call(Map config = [:]) {
    def packageManager = config.packageManager ?: 'npm'
    def toolsConfig = config.tools ?: [:]
    def stagesConfig = config.stages ?: []
    def postConfig = config.post ?: [:]

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

    def postBlock = postConfig.collect { condition, step ->
        """
        ${condition} {
            script {
                ${step}
            }
        }
        """
    }.join('\n')

    def parametersBlock = config.parameters.collect { param ->
        """
        ${param.type}(name: '${param.name}', defaultValue: '${param.defaultValue}', description: '${param.description}')
        """
    }.join('\n')

    def pipelineScript = """
        pipeline {
            agent any
            parameters {
                ${parametersBlock}
             }
            tools {
                ${toolsBlock}
            }
            stages {
                ${stagesBlock}
            }
            post {
                ${postBlock}
            }
        }
    """


    return evaluate(pipelineScript)
}
