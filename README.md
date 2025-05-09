# **Project Under Construction**

> [!WARNING]  
> This project is currently under development. Please check periodically for changes.

---

# Demo: Multi-Agent AI with Apache Camel and Kaoto

This demo will show you the super powers of combining LLMs and Apache Camel to create processes and tools tools that allow users to use natural language translated into actions against backend systems.

---

## Repository Structure

This repository contains several folders and files to help you get started and understand the project:

- **`ansible/`**  
  Contains Ansible playbooks and configuration files for automating the deployment and undeployment of the demo environment.

- **`camel/`**  
  Source code for the main Camel logic of the demo, including scripts and modules that power the AI agents and integrations.

- **`config/`**  
  Configuration for the Camel Instance

- **`demo/`**  
  Demo invoices (PDFs), a sample transcript, sample X12 EDI document and the [demo/storyline.md](the complete demo flow)

- **`deploy/`**  
  Schemata for Milvius and the database.

- **`docs/`**  
  Documentation and guides for running and understanding the demo, including step-by-step instructions and additional background information.
  [docs/README.md](Explains how to set up the demo)
  [docs/prompts.md](Has example prompts you can use)

- **`scripts/`**  
  Resources to start the demo and the agents.

- **`README.md`**  
  This file. Provides an overview and instructions for using the repository.

You may also find additional files and folders for configuration, examples, or utilities as the project evolves.

---

## Tested with

* RH OpenShift 4.12.12
* Red Hat build of Apache Camel 4

---

## Deployment instructions

The demo is designed to operate across two connected environments: your local machine, where Camel and LLMs run, and a remote environment hosting all other dependencies. For the best experience, follow the guidelines below to automatically provision the required systems on OpenShift, then run Camel and the LLMs locally.

<br/>

### 1. Provision an OpenShift environment

1. Provision the following RHDP item:
    * [**Red Hat OpenShift Container Platform Cluster (AWS)**](https://demo.redhat.com/catalog?item=babylon-catalog-prod/sandboxes-gpte.ocp-wksp.prod&utm_source=webapp&utm_medium=share-link)

   <br/>

1. Alternatively, if you don't have access to RHDP, ensure you have an OpenShift environment available meeting the pre-requisite product versions (see '_Tested with_' section to inspect product versions).

<br/>

### 2. Deploy the Demo

The instructions below assume:
* You either have _Docker_, _Podman_ or `ansible-playbook` installed on your local environment.
* You have provisioned an OCP instance using RHDP.
NOTE: RHDP is the Red Hat Demo Portal. You most likely won't have access to it. If you do, grab a standard OCP instance. If you don't feel free to use any OCP instance you have at your disposal.
<br/>


#### Installation

1. Clone this GitHub repository:

    ```sh
    git clone https://github.com/brunoNetId/ai-agentic-scenario.git
    ```

1. Change to root directory of the project.

    ```sh
    cd ai-agentic-scenario
    ```

    <br/>

1. When running with _Docker_ or _Podman_
    
    1. Configure the `KUBECONFIG` file to use (where kube details are set after login).

        ```sh
        export KUBECONFIG=./ansible/kube-demo
        ```

    1. Login into your OpenShift cluster from the `oc` command line.

        ```sh
        oc login --username="admin" --server=https://(...):6443 --insecure-skip-tls-verify=true
        ```

        Replace the `--server` url with your own cluster API endpoint.

    1. Run the Playbook

        1. With Docker:
        
            ```sh
            docker run -i -t --rm --entrypoint /usr/local/bin/ansible-playbook \
            -v $PWD:/runner \
            -v $PWD/ansible/kube-demo:/home/runner/.kube/config \
            quay.io/agnosticd/ee-multicloud:2025-02-14  \
            ./ansible/install.yaml
            ```
        
        1. With Podman:
        
            ```sh
            podman run -i -t --rm --entrypoint /usr/local/bin/ansible-playbook \
            -v $PWD:/runner \
            -v $PWD/ansible/kube-demo:/home/runner/.kube/config \
            quay.io/agnosticd/ee-multicloud:2025-02-14  \
            ./ansible/install.yaml

            ```
    <br/>

1. When running with Ansible Playbook (installed on your machine)

    1. Login into your OpenShift cluster from the `oc` command line.

        For example with: \
        ```sh
        oc login --username="admin" --server=https://(...):6443 --insecure-skip-tls-verify=true
        ```
        (Replace the `--server` url with your own cluster API endpoint)

    1. Set the following property:
        ```
        TARGET_HOST="lab-user@bastion.b9ck5.sandbox1880.opentlc.com"
        ```
    2. Run Ansible Playbook
        ```sh
        ansible-playbook -i $TARGET_HOST,ansible/inventory/openshift.yaml ./ansible/install.yaml
        ```

<br/>

### 3. Undeploy the remote environment

If you wish to undeploy the demo, use the same commands as above, but with:
 - `./uninstall.yaml`

Instead of:
 - ~~`./install.yaml`~~


<br/>

### 4. Run the demo

> [!WARNING]  
> The demo documentation is pending to be improved.

Find more instructions on how to run the demo under the `docs` folder.