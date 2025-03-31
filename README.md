# **Project Under Construction**
=====================================

This project is currently in development. Please check back later for updates.



# Demo: Multi-Agent AI with Apache Camel and Kaoto

This demo will show you the super powers of combining LLMs and Apache Camel to create tools that allow users to use natural language to fetch information to backend systems.

## Tested with

* RH OpenShift 4.12.12
* Red Hat build of Apache Camel 4


## Deployment instructions

### 2. Provision an OpenShift environment

1. Provision the following RHDP item:
    * [**Red Hat OpenShift Container Platform Cluster (AWS)**](https://demo.redhat.com/catalog?item=babylon-catalog-prod/sandboxes-gpte.ocp-wksp.prod&utm_source=webapp&utm_medium=share-link)

   <br/>

1. Alternatively, if you don't have access to RHDP, ensure you have an OpenShift environment available meeting the pre-requisite product versions (see '_Tested with_' section to inspect product versions).

<br/>

### 2. Deploy the Demo

The instructions below assume:
* You either have _Docker_, _Podman_ or `ansible-playbook` installed on your local environment.
* You have provisioned an OCP instance using RHDP.

<br/>


#### Installation

1. Clone this GitHub repository:

    ```sh
    git clone https://github.com/brunoNetId/workshop-camel-llm-tools.git
    ```

1. Change to root directory of the project.

    ```sh
    cd workshop-camel-llm-tools
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

### 3. Undeploy the Workshop

If you wish to undeploy the demo, use the same commands as above, but with:
 - `./uninstall.yaml`

Instead of:
 - ~~`./install.yaml`~~
