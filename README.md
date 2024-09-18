# Helm Chart Guide for a Spring Boot Application

## What is a Helm Chart?

Helm is a package manager for Kubernetes, similar to how Homebrew is used on macOS, Chocolatey on Windows, or APT on Ubuntu. It facilitates the process of managing Kubernetes applications by allowing users to define, install, and upgrade complex Kubernetes applications succinctly using a Helm Chart.

A Helm Chart is a collection of files organized in a specific directory structure, which describes a related set of Kubernetes resources. Charts can simplify the deployment and management of applications and services on a Kubernetes cluster, making it easier to define, version, and manage than manually handling complex `yaml` files.

## Why Use Helm Charts?

### Scenario Explaining the Relevance of Helm Charts

Consider a scenario where you need to deploy Jenkins on different operating systems like macOS, Windows, and Ubuntu. Each platform has its package manager that simplifies software installation. Similarly, for Kubernetes clusters, instead of manually configuring multiple complex YAML files for deployment, services, and management, you use a Helm chart. The Helm chart abstracts these details into a simple set of configurations that can be managed more intuitively.

### Challenges Addressed by Helm

![ChallengesWhileMaintainingDifferentManifestFiles](images/1.ChallengesWhileMaintainingDifferentManifestFiles.jpg)

Deploying applications on a Kubernetes cluster without Helm can present several challenges:

#### 1. Defining, Managing, and Executing Multiple Manifest Files
Without Helm, you have to manually handle multiple manifest files for different resources like deployments, services, and persistent volumes. It’s error-prone and inefficient.

#### 2. Deployment Ordering Challenge
The correct order of deployment is critical. For instance, a database must be ready before the app that uses it starts. Helm manages these dependencies effectively, ensuring resources are deployed in the correct sequence.

#### 3. Versioning and Rollbacks for All Resources
Helm tracks versions of your deployments and allows for easy rollbacks to a previous stable state, which is complex to manage manually with raw Kubernetes yaml files.

#### 4. Environment-Specific Configuration Files
Helm allows you to tweak your configurations to fit different environments (development, staging, production) without changing the core application logic. This separation of concerns simplifies management and enhances security.

## Helm Chart Directory Structure Overview

![TreeViewOfHelmChart](images/2.TreeViewOfHelmChart.jpg)

The structure of a Helm chart is designed to organize all necessary files in a coherent manner, facilitating easy management of Kubernetes resources. Below is an overview of the key components within a typical Helm chart directory:

- `Chart.yaml`: This file contains metadata about the chart such as its name, version, and other relevant information. It is essential for identifying the chart and its specifics.

- `values.yaml`: This file stores the default configuration values for the chart. These values can be overridden at installation time to customize the deployment according to specific requirements.

- `charts/`: This directory may contain any dependent charts. If your chart depends on other charts, they can be included here, enabling the management of complex applications with interdependencies.

- `templates/`: This is the core directory where Kubernetes manifest templates are stored. It typically includes files such as:
  - `deployment.yaml`: Defines the deployment configuration for the Kubernetes application.
  - `service.yaml`: Specifies the service that exposes the application.
  - `helpers.tpl`: Contains template helpers which can be reused across multiple manifest files, improving maintainability and reducing duplication.
  - Other manifest files as required by the application, such as `ingress.yaml`, `persistentVolumeClaim.yaml`, etc.

- `.helmignore`: Similar to `.gitignore`, this file specifies patterns to ignore when packaging the chart, which can be useful to exclude files and directories from the final chart package.

This structured approach not only makes the management of Kubernetes deployments easier but also ensures that complex applications can be handled with ease across different environments.

## How Helm Addresses Kubernetes Deployment Challenges

Helm simplifies the management and deployment of Kubernetes applications. Below, we explore how Helm addresses the key challenges identified:

### Challenge 1: Managing Multiple Manifest Files

![SolvingChallengeOneOfManganingAndExecutingManifestFiles](images/3.SolvingChallengeOneOfManganingAndExecutingManifestFiles.jpg)

Without Helm, managing multiple Kubernetes manifest files can be cumbersome and error-prone. Helm addresses this by encapsulating all necessary Kubernetes resources and their dependencies within a single chart package. When you execute commands like `helm install myapp ./myapp`, Helm deploys all associated resources defined in the chart's templates and dependencies, ensuring a cohesive deployment process.

### Challenge 2: Handling Deployment Order

![SolvingChallengeTwoOfOrdering](images/4.SolvingChallengeTwoOfOrdering.jpg)

Dependency management is crucial, especially when certain applications must be deployed in a specific order. Helm handles this by allowing you to define dependencies directly within the `Chart.yaml` file. For example:

```yaml
dependencies:
  - name: mysql
    version: "8.8.26"
    repository: "https://charts.bitnami.com/bitnami"
  - name: kafka
    version: "12.11.3"
    repository: "https://charts.bitnami.com/bitnami"
```

These dependencies are managed by Helm, which ensures that they are installed in the correct order before the main application, maintaining operational integrity and dependency requirements.

### Challenge 3: Versioning and Rollbacks

![SolvingChallengeThreeOfRollback](images/5.SolvingChallengeThreeOfRollback.jpg)

Helm tracks every installation or upgrade as a revision, making it easy to rollback to a previous version if something goes wrong. The `helm history` command lists all the revisions for a particular Helm release:

```bash
$ helm history myapp
```

This output might show:

```yaml
REVISION    UPDATED                   STATUS      CHART           APP VERSION   DESCRIPTION
1           Mon May 10 10:24:52 2023  deployed    myapp-0.1.0     1.0.0         Install complete
2           Mon May 10 11:30:16 2023  deployed    myapp-0.2.0     1.0.1         Upgrade complete
```

If an upgrade introduces issues, you can rollback to a previous revision using:

```bash
If an upgrade introduces issues, you can rollback to a previous revision using:
```

Helm confirms:

```css
Rollback was a success! Happy Helming!
```

### Challenge 4: Environment-Specific Configurations

![SolvingChallengeFourOfEnvSpecificConfigFiles](images/6.SolvingChallengeFourOfEnvSpecificConfigFiles.png)

Managing configurations for different environments (development, staging, production) can be complex without proper tools. Helm simplifies this process through the use of environment-specific values files:

- `values-dev.yaml` for development
- `values-staging.yaml` for staging
- `values-prod.yaml` for production

Each file contains settings tailored to its respective environment, such as replica counts, image tags, service ports, and resource limits. To deploy an application using the development configuration, you would use:

```bash
$ helm install myapp ./myapp -f values-dev.yaml
```

This command instructs Helm to override the default values defined in `values.yaml` with those specified in `values-dev.yaml`, ensuring the deployment is configured appropriately for the development environment. This approach not only streamlines the deployment process across different environments but also helps in maintaining the consistency and integrity of application deployments.

## Spring Boot Application Setup

This section describes the setup of a simple Spring Boot application that serves a list of customers through a REST endpoint.

### Dependencies

The Spring Boot application utilizes the following dependencies:

- `spring-boot-starter-web` for building web, including RESTful, applications using Spring MVC. Uses Apache Tomcat as the default embedded container.
- `spring-boot-devtools` for development-time features like automatic restarts.
- `lombok` to minimize boilerplate code in Java applications.
- `spring-boot-starter-test` for testing Spring Boot applications with libraries including JUnit, Hamcrest, and Mockito.

### Dockerfile

To containerize the Spring Boot application, we use the following Dockerfile:

```dockerfile
FROM openjdk:17
WORKDIR /app
COPY ./target/springboot-helm-chart-example.jar /app
EXPOSE 8080
CMD ["java", "-jar", "springboot-helm-chart-example.jar"]
```

This Dockerfile starts from the `openjdk:17` base image, sets the working directory to `/app`, copies the built jar file into the image, exposes port 8080, and specifies the command to run the application.

## Deploying with Minikube and Helm

### Setting Up the Environment

#### Starting Minikube

Minikube is a tool that lets you run Kubernetes locally. Start Minikube with the following command:

```bash
minikube start
```

This command starts a single-node Kubernetes cluster on your local machine.

#### Starting Docker

Ensure that Docker is running on your machine as Minikube will use Docker as the container runtime. You can start Docker through your desktop Docker application or by any other means you typically use.

#### Syncing Docker Environment with Minikube
To build images directly into Minikube's Docker environment (avoiding the need to push/pull from a registry), sync your Docker CLI to Minikube’s Docker daemon:

```bash
eval $(minikube docker-env)
```

Or, on PowerShell:

```powershell
minikube -p minikube docker-env --shell powershell | Invoke-Expression
```

This step is crucial as it allows you to use Docker directly on Minikube’s VM, which simplifies local development and testing.

### Building the Application

#### Compile and Package the Application
Before building the Docker image, compile and package the Java application using Maven. This step creates the `jar` file that will be included in the Docker image.

```bash
mvn clean package
```

This command runs the Maven build process, which compiles the Java code and packages it into a `jar` file located in the `target/` directory.

### Dockerizing the Application

Navigate to the project directory where your Dockerfile is located and build the Docker image:

```bash
docker build -t springboot-helm-chart-app:2.0 .
```

This command builds a Docker image with the tag `springboot-helm-chart-app:2.0`, containing the Spring Boot application.

### Deploying with Helm

#### Initialize Helm Chart

Create a Helm chart for the application which will define the Kubernetes resources needed to deploy:

```bash
helm create spring-app-chart
```

Navigate into the `spring-app-chart` directory and adjust the templates for deployment and services as necessary, ensuring values are correctly set for your application in `values.yaml`

#### Deploy the Application

Deploy your application to Kubernetes using the Helm chart:

```bash
helm install myapp-chart spring-app-chart
```

This command instructs Helm to manage the installation of your application, creating the necessary Kubernetes resources such as deployments and services.

### Verifying the Deployment
Check the status of the deployed resources:

```bash
kubectl get all
```

To view the logs from a specific pod:

```bash
kubectl logs myapp-chart-spring-app-chart-756f47df8f-527bt
```

### Accessing the Application
Retrieve the service URL via Minikube to access your application:

```bash
minikube service myapp-chart-spring-app-chart --url
```

Visit the provided URL in your browser followed by `/customers` to interact with the deployed Spring Boot application.

### Expected Outcome

The application should return a JSON array of customers when the `/customers` endpoint is accessed, confirming that the deployment was successful and the application is functioning as expected within your Kubernetes cluster managed by Minikube.

```json
[
  {
    "id": 1,
    "name": "Hari Krishna",
    "email": "harikrishna@gmail.com",
    "gender": "male"
  },
  {
    "id": 2,
    "name": "Rakesh Kumar",
    "email": "rakesh@gmail.com",
    "gender": "male"
  },
  {
    "id": 3,
    "name": "Tarun Kumar",
    "email": "tarun@gmail.com",
    "gender": "male"
  }
]
```
