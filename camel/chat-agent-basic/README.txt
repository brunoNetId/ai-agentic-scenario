Pre-requisites
==============
1) You'll need to deploy the dependencies on an OpenShift cluster. Follow the deployment instructions in the root Readme file of the project.
2) The demo requires a patch for the camel-langchain4j-tools component until 4.12 is released.


Run the demo
============
We recommend running the demo following the instructions documented under:

 - docs/README.md


Running this instance
=====================
If you prefer to run this instance manually, use the command below:

camel run * ../../config/domain.properties --dep=dev.langchain4j:langchain4j-embeddings-all-minilm-l6-v2:0.32.0