package fyi.credentials

class ContainerUtils implements Serializable {
    def script

    ContainerUtils(script) {
        this.script = script
    }

    boolean doesImageExist(String imageName) {
        try {
            script.sh(script: "docker inspect --type=image ${imageName}", returnStdout: true)
            return true
        } catch (Exception e) {
            script.echo "The image ${imageName} does not exist."
            return false
        }
    }

    def tagAndPushContainer(String imageName, String newTag, String registry) {
            if (doesImageExist(imageName)) {
                // Tag the image with the GCR registry name
                String fullImageName = "${registry}/${imageName}:${newTag}"
                script.sh(script: "docker tag ${imageName} ${fullImageName}")

                // Push the image to GCR
                script.sh(script: "docker push ${fullImageName}")
            } else {
                script.echo "The image ${imageName} does not exist. Cannot tag and push."
            }
        }
}