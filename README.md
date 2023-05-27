# Jenkins Shared Library

This repository contains a Jenkins shared library designed to streamline Node.js and Java application builds.

## Warning

Please note that using `evaluate` can potentially have security implications if you're not fully in control of the input, as it can execute arbitrary Groovy code. Make sure that you're aware of this before using this approach.

## Directory Structure

This shared library uses the standard directory structure for Jenkins shared libraries:

- `vars/`: This directory contains global script files that define global variables accessible from Pipeline scripts.
    - `nodeBuild.groovy`: Defines a pipeline for building Node.js applications.
    - `javaBuild.groovy`: Defines a pipeline for building Java applications.
- `Notificationutils.groovy`: A script for common notification related functions.

## Using this Shared Library in Jenkins

### Manual Config

To manually load this shared library in Jenkins:

1. Go to "Manage Jenkins" > "Configure System" > "Global Pipeline Libraries".
2. Click "Add".
3. Fill in the "Name" and "Default version" (usually 'master' or 'main').
4. Under "Retrieval method", choose "Modern SCM" and then the SCM type (e.g., Git).
5. Fill in the repository URL and any credentials.

### Configuration as Code (JCasC)

To load this shared library using Jenkins Configuration as Code (JCasC):

```yaml
unclassified:
  globallibraries:
    libraries:
      - defaultVersion: "main"
        name: "jenkins-utilities"
        implicit: true
        retriever:
          modernSCM:
            scm:
              git:
                remote: "https://github.com/scr1ba/jenkins-utilities.git"
```

### Importing the library in a Pipeline

To import this library in a Jenkinsfile or a Pipeline, if you haven't configured it to load implicely, you can use the `@Library` annotation
```javascript
@Library('jenkins-utilities') _
```

Then you can call the library functions like this:
```javascript
nodeBuild(
    packageManager: 'pnpm',
    tools: [nodejs: 'NodeJS 18 LTS'],
    stages: [
        [
            name: 'Checkout',
            step: '''
                git branch: 'main', url: 'https://github.com/nuxt/nuxt.git'
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
        ]
    ]
)
```

### Contributing

Contributions to this shared library are welcome! Please submit a pull request with your proposed changes.
