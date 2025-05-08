# How to Run the Demo

This guide explains how to set up and run the multi-agent AI demo using Apache Camel, Kaoto, and LLMs.

---

## Prerequisites

- **Provision an OpenShift environment**  
  Follow the main project guidelines to provision an OpenShift environment and deploy all required systems.

- **Run LLMs locally**  
  For best performance, run your LLMs (e.g., Ollama) on a powerful laptop with sufficient GPU resources.

- **Camel Instances**  
  All Camel instances are run using [Camel JBang](https://camel.apache.org/camel-jbang/latest/).

---

## Configuration

When the Ansible scripts provision the environment, they also auto-configure Camel and the scripts by generating:

- `config/domain.properties`  
  This configuration file is used by Camel and the start/stop/reset scripts.

Camel uses several apps for the demo, each running on different ports to avoid conflicts. These ports are documented in:

- `camel/ports.txt`

---

## Demo Preparations

1. **Open a Tunnel for PostgreSQL**  
   When all systems are running in OpenShift, open a tunnel to allow Camel to communicate with PostgreSQL:

   ```sh
   oc port-forward svc/postgresql 5432 -n demo
   ```

2. **Access Required UIs**  
   Open browser tabs for the following:

   - **Milvus (Vector Database):**  
     - Namespace: `milvus`  
     - Credentials:  
       - Username: `root`  
       - Password: `Milvus`

   - **Filestash (S3 Explorer):**  
     - Namespace: `filestash`  
     - Connection parameters:  
       - User Key: `minio`  
       - Access Key: `minio123`  
       - Endpoint: Minio's API route URL

3. **Prepare Invoices**  
   Drop invoices into the `ingest` S3 bucket.  
   Sample invoices can be found under the `demo/invoices` folder.

---

## Starting and Stopping Camel Applications

1. **Start Camel Applications**

   From a terminal, change directory to the `scripts` folder and run:

   ```sh
   ./agents-up
   ```

   Camel will start and begin consuming invoices from the S3 bucket.

2. **Stop Camel Applications**

   To stop the Camel applications, run:

   ```sh
   ./agents-down
   ```

---

## Resetting the Demo

If you want to run the demo multiple times, you can reset the initial conditions by running:

```sh
./reset
```

This command will:

- Stop any running Camel applications related to this project
- Delete data from the remote systems (S3, DB, VectorDB)

---

**Tip:**  
For more details, refer to the main project [README.md](../README.md) and the documentation in the `docs/` folder.
