package fyi.credentials

// This requires the Slack Notification plugin: https://plugins.jenkins.io/slack/
class NotificationUtils implements Serializable {
    def script

    NotificationUtils(script) {
        this.script = script
    }

    def notifyBuildStatusViaSlack(String status, String slackChannel) {
        def color = status == 'SUCCESS' ? 'good' : 'danger'
        script.slackSend color: color, message: "Build ${status}", channel: slackChannel
    }

    def notifyWithAttachment(String slackChannel, String text, String fallback, String color) {
        def attachments = [[
            text: text,
            fallback: fallback,
            color: color
        ]]

        script.slackSend channel: slackChannel, attachments: attachments
    }

    def uploadFileToSlack(String filePath, String initialComment) {
        script.slackUploadFile filePath: filePath, initialComment: initialComment
    }

    def sendSlackMessage(String message, String color = 'good', String channel = '#cicd') {
        script.slackSend color: color, message: message, channel: channel
    }

    def sendSlackAttachments(List attachments, String channel = '#cicd') {
        script.slackSend channel: channel, attachments: attachments
    }

    def uploadSlackFile(String filePath, String initialComment = '') {
        script.slackUploadFile filePath: filePath, initialComment: initialComment
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
}
