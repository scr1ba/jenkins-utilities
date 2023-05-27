def call(String message, String color = 'good', String channel = '#cicd') {
    new fyi.credentials.NotificationUtils(this).sendSlackMessage(message, color, channel)
}
