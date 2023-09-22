# Deploying a Spring Boot Application to Kubernetes(k8s)

This project we will deploy a Spring Boot application to a Kubernetes cluster. Kubernetes is an open-source container orchestration platform that allows you to manage containerized applications. We'll go through the steps to containerize your Spring Boot application, create Kubernetes deployment and service files, and then deploy the application to a Kubernetes cluster.

## Prerequisites

Before starting the Task, make sure you have the following prerequisites in place:

1. A Spring Boot application that you want to deploy.
2. Docker installed on your local machine.
3. A Kubernetes cluster set up. You can use Minikube or a cloud-based Kubernetes service like Google Kubernetes Engine (GKE) or Amazon Elastic Kubernetes Service (EKS).

## Implementation Steps

### 1. Containerize Your Spring Boot Application

To deploy your Spring Boot application to Kubernetes, you need to containerize it using Docker. Here are the steps to containerize your application:

1.1. Create a Dockerfile in your project directory. This file contains instructions for building a Docker image of your application.

```Dockerfile
# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-alpine

# Set the working directory to /app
WORKDIR /app

# Copy the JAR file into the container at /app
COPY target/your-application.jar /app/

# Make port 6039 available to the world outside this container
EXPOSE 6039

# Run the JAR file
CMD ["java", "-jar", "your-application.jar"]
```

1.2. Build a Docker image of your application using the Dockerfile. Replace `your-application.jar` with your actual JAR file name.

```bash
docker build -t your-image-name .
```

1.3. Verify that the Docker image was created successfully.

### 2. Deploy to Kubernetes

Now that you have a Docker image of your application, you can deploy it to Kubernetes.

2.1. Create a Kubernetes deployment YAML file (`deployment.yaml`). Replace `your-image-name` with the name of your Docker image and customize the file as needed.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: your-deployment-name
spec:
  replicas: 2 # Number of pods to run
  selector:
    matchLabels:
      app: your-application
  template:
    metadata:
      labels:
        app: your-application
    spec:
      containers:
        - name: your-container-name
          image: your-image-name # Use the Docker image you built
          ports:
            - containerPort: 6039
```

2.2. Create a Kubernetes service YAML file (`service.yaml`) to expose your application. Customize the file as needed.

```yaml
apiVersion: v1
kind: Service
metadata:
  name: your-service-name
spec:
  selector:
    app: your-application
  ports:
    - protocol: "TCP"
      port: 6039
      targetPort: 6039
  type: LoadBalancer # Use 'LoadBalancer' if running on a cloud-based cluster else Use 'NodePort' or 'Ingress' if preferred
```
2.3. Create a Kubernetes ingess YAML file (`my-server-app-ingress.yaml`) [optional]. Customize the file as needed.

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: my-server-app-ingress
spec:
  rules:
    - host: mongoserver.com  # Replace with your domain or set it to "*"
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: my-server-app-service
                port:
                  number: 6039

```

### 3. Deploy to Kubernetes Cluster

3.1. Apply the deployment and service YAML files to your Kubernetes cluster.

```bash
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml
```

3.2. Verify that the pods and services are running in your cluster.

```bash
kubectl get pods
kubectl get services
```

### 4. Access Your Application

4.1. Obtain the external IP address or domain name of your service.

```bash
kubectl get svc your-service-name
```

4.2. Access your Spring Boot application using the external IP address or domain name and the exposed port (6039 in this example).

```bash
http://external-ip:6039
```

## Summary

In this Task, we learned how to deploy a Spring Boot application to a Kubernetes cluster. This involves containerizing our application using Docker, creating Kubernetes deployment and service files, and deploying our application to the cluster. Kubernetes provides scalability and reliability for our application, making it suitable for production deployments.
