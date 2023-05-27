def call(String status, String channel) {
    new fyi.credentials.NotificationUtils(this).notifyBuildStatusViaSlack(status, channel)
}
