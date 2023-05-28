/** Usage in a Jenkinsfile:

nodeBuild(
    packageManager: 'pnpm',
    tools: [nodejs: 'NodeJS 18 LTS'],
    parameters: [
        [type: 'string', name: 'MESSAGE', defaultValue: 'hello_world', description: 'Message to print'],
        [type: 'text', name: 'MULTILINE', defaultValue: 'Line 1\nLine 2', description: 'Multiline text'],
        [type: 'booleanParam', name: 'BOOLEAN', defaultValue: 'false', description: 'A boolean value'],
        [type: 'choice', name: 'CHOICE', choices: 'Option1\nOption2\nOption3', description: 'A choice'], // Option1 is the default value
        [type: 'password', name: 'PASSWORD', defaultValue: '', description: 'A password'],
        [type: 'file', name: 'FILE', description: 'A file parameter'], // no default value
        [type: 'credentials', name: 'MY_CREDS', defaultValue: 'slack-token', required: true, description: 'My credentials']
    ],
    stages: [
        [
            name: 'Print message',
            step: '''
                sh 'echo "${params.MESSAGE}"'
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
                  sh 'docker tag tiny-python-placeholder $LOCATION_NAME/$PROJECT_ID/$REPOSITORY_NAME/tiny-python-placeholder:test-tag'
                  sh 'docker push $LOCATION_NAME/$PROJECT_ID/$REPOSITORY_NAME/tiny-python-placeholder:test-tag'
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
        generateParameterBlock(param)
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

def generateParameterBlock(Map param) {
        switch (param.type) {
            case 'string':
            case 'booleanParam':
            case 'text':
                return "${param.type}(name: '${param.name}', defaultValue: \"\"\"${param.defaultValue}\"\"\", description: '${param.description}')"
            case 'password':
                return "${param.type}(name: '${param.name}', defaultValue: '${param.defaultValue}', description: '${param.description}')"
            case 'choice':
                return "${param.type}(name: '${param.name}', choices: \"\"\"${param.choices}\"\"\", description: '${param.description}')"
            case 'file':
                return "${param.type}(name: '${param.name}', description: '${param.description}')"
            case 'credentials':
                return "${param.type}(name: '${param.name}', defaultValue: '${param.defaultValue}', description: '${param.description}')"
            default:
                throw new IllegalArgumentException("Unknown parameter type: ${param.type}")
        }
    }