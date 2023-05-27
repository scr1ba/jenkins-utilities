package fyi.credentials

// This requires the Slack Notification plugin: https://plugins.jenkins.io/slack/
class NotificationUtils implements Serializable {
    def script

    NotificationUtils(script) {
        this.script = script
    }

    def sendSlackMessage(String message, String color = 'good', String channel = '#cicd') {
        script.slackSend color: color, message: message, channel: channel
    }

    def sendFailureNotification(String channel = '#cicd') {
            def buildUrl = script.env.BUILD_URL
            def jobName = script.env.JOB_NAME
            def buildNumber = script.env.BUILD_NUMBER

            String message = """
                :x: *Build Failed*
                *Job:* ${jobName}
                *Build Number:* ${buildNumber}
                *Build URL:* <${buildUrl}|Link to Build>
            """

            script.slackSend color: 'danger', message: message, channel: channel
        }

    def sendSuccessNotification(String channel = '#cicd') {
            def buildUrl = script.env.BUILD_URL
            def jobName = script.env.JOB_NAME
            def buildNumber = script.env.BUILD_NUMBER

            String message = """
                :white_check_mark: *Build Finished Successfully*
                *Job:* ${jobName}
                *Build Number:* ${buildNumber}
                *Build URL:* <${buildUrl}|Link to Build>
            """

            script.slackSend color: 'good', message: message, channel: channel
        }
}
