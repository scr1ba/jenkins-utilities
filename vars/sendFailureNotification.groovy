def call(String channel) {
    new fyi.credentials.NotificationUtils(this).sendFailureNotification(channel)
}
