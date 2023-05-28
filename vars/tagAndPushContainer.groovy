def call(String imageName, String newTag, String registry) {
    new fyi.credentials.ContainerUtils(this).tagAndPushContainer(imageName, newTag, registry)
}
