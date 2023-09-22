# Deploying a Spring Boot Application to Kubernetes

In this task, we will deploy a Spring Boot application to a Kubernetes cluster. Kubernetes is an open-source container orchestration platform that allows you to manage containerized applications. We'll go through the steps as follows
1. Creating Kubernetes manifests for MongoDB and the Spring Boot application.
2. Deploying MongoDB as a separate pod.
3. Deploying the Spring Boot application as a separate pod.
4. Exposing the application using NodePort.
5. Testing the deployed application.

## Prerequisites

Before starting, make sure you have the following prerequisites in place:

1. A Spring Boot application that you want to deploy.
2. Docker installed on your local machine.
3. Kubernetes cluster (e.g., Minikube).
4. `kubectl` command-line tool
5. MongoDB Docker image (if not using an external MongoDB)

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
  name: my-server-app-service
spec:
  selector:
    app: my-server-app
  ports:
    - protocol: "TCP"
      port: 6039
      targetPort: 6039
  type: NodePort

```
2.3. Create MongoDB Configuration

If you are using an external MongoDB, skip this step. If you want to deploy MongoDB within the cluster:

#### Create a Persistent Volume (PV)

A Persistent Volume is used to store MongoDB data, ensuring that data persists even if the MongoDB pod is restarted or recreated. Here's an example `mongodb-pv.yaml` configuration for creating a PV:

```yaml
# mongodb-pv.yaml
apiVersion: v1
kind: PersistentVolume
metadata:
  name: mongodb-pv
spec:
  capacity:
    storage: 1Gi
  volumeMode: Filesystem
  accessModes:
    - ReadWriteOnce
  persistentVolumeReclaimPolicy: Retain
  storageClassName: standard
  hostPath:
    path: /mnt/data/mongodb
```

#### Create a Persistent Volume Claim (PVC)

A Persistent Volume Claim is used to claim storage resources from a PV. Create a PVC to bind to the PV. Here's an example `mongodb-pvc.yaml` configuration for creating a PVC:

```yaml
# mongodb-pvc.yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: mongodb-pvc
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 1Gi
  storageClassName: standard
```

#### MongoDB Deployment Configuration (mongodb-deployment.yaml)

```yaml
# mongodb-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mongodb
spec:
  replicas: 1
  selector:
    matchLabels:
      app: mongodb
  template:
    metadata:
      labels:
        app: mongodb
    spec:
      containers:
        - name: mongodb
          image: mongo:latest
          ports:
            - containerPort: 27017
          volumeMounts:
            - name: mongodb-data
              mountPath: /data/db
      volumes:
        - name: mongodb-data
          persistentVolumeClaim:
            claimName: mongodb-pvc
```

### 3. Deploy to Kubernetes Cluster

Apply the deployment and service YAML files to your Kubernetes cluster.
To apply the changes to your Kubernetes cluster, you can use the `kubectl apply -f` command for each of the YAML configuration files you've created. Here are the commands to apply the changes:

1. Apply the MongoDB Deployment and Service:

   ```bash
   kubectl apply -f mongodb-deployment.yaml
   kubectl apply -f mongodb-service.yaml
   ```

2. Apply the MongoDB Persistent Volume Claim (PVC):

   ```bash
   kubectl apply -f mongodb-pvc.yaml
   ```

3. Apply the Application Deployment and Service:

   ```bash
   kubectl apply -f deployment.yaml
   kubectl apply -f service.yaml
   ```

Make sure you run these commands in the directory where your YAML files are located. This will create or update the respective resources in your Kubernetes cluster based on the changes you've made in the YAML files.

After applying these changes, MongoDB should be running in a separate pod with data persistence, and your application should be able to connect to it using the specified environment variable. You can access your application's endpoints using the appropriate service type (NodePort or LoadBalancer) as configured in the `service.yaml` file.

### 4. Access Your Application

4.1. Obtain the external IP address or domain name of your service.

Once you have applied the changes to your Kubernetes cluster and ensured that MongoDB is running in a separate pod with data persistence, you can proceed with accessing your application's endpoints and verifying that everything is working as expected. Here are the next steps:

1. Check the Status of Your Pods:

   You can use the following command to check the status of your pods to ensure they are running:

   ```bash
   kubectl get pods
   ```
   Make sure both the MongoDB pod and your application pod are in the "Running" state.

2. Get the External IP (if using LoadBalancer):

   If you are using a LoadBalancer service type for your application, you can use the following command to get the external IP:

   ```bash
   kubectl get svc my-server-app-service
   ```

   The "EXTERNAL-IP" field should eventually show an external IP address if you are using a cloud-based Kubernetes provider. Note that it might take some time for the external IP to be assigned.

3. Access Your Application:

   Once you have the external IP or NodePort (depending on your service type), you can access your application's endpoints in a web browser or using tools like `curl` or Postman. Open a web browser and enter the following URL:

   ```
   http://<EXTERNAL-IP>:6039
   ```

   Replace `<EXTERNAL-IP>` with the actual external IP address or use the NodePort if you are using NodePort as the service type.

4. Test Your Application:

   Test your application by accessing various endpoints, creating, updating, and deleting records, and verifying that the data is stored persistently in MongoDB. You can use tools like Postman or `curl` to send HTTP requests to your application's endpoints.

5. Monitor Logs and Debug (if needed):

   You can monitor the logs of your application and MongoDB pods to troubleshoot any issues. Use the following command to view the logs of a pod:

   ```bash
   kubectl logs <pod-name>
   ```

   Replace `<pod-name>` with the actual name of the pod you want to view logs for.

6. Cleanup (Optional):

   If you want to clean up the resources created in your cluster, you can use the `kubectl delete` command followed by the resource type and name. For example, to delete the MongoDB and application deployments, services, and PVCs, you can run:

   ```bash
   kubectl delete deployment my-server-app
   kubectl delete service my-server-app-service
   kubectl delete deployment mongodb-deployment
   kubectl delete service mongodb-service
   kubectl delete pvc mongodb-pvc
   ```

   Be cautious when using the `delete` command, as it will permanently remove the resources.

These steps should help you verify that your application is running correctly in your Kubernetes cluster and that it meets the specified requirements. If you encounter any issues or have specific questions, feel free to ask for further assistance.

## Summary

In this task, you learned how to deploy a Spring Boot application to a Kubernetes cluster. This involves containerizing your application using Docker, creating Kubernetes deployment and service files, and deploying your application to the cluster. Kubernetes provides scalability and reliability for your application, making it suitable for production deployments.
