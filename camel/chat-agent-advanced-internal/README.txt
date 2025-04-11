Run Ingest:
camel run ingest.camel.yaml --dep=dev.langchain4j:langchain4j-embeddings-all-minilm-l6-v2:0.32.0 --dev

Run demo:
camel run *  --dep=dev.langchain4j:langchain4j-embeddings-all-minilm-l6-v2:0.32.0 --dev


TEST PATCH:
jbang '-Dcamel.jbang.version=4.11.0-SNAPSHOT' camel@apache/camel run * --dep=dev.langchain4j:langchain4j-embeddings-all-minilm-l6-v2:0.32.0 --dev --logging-level=info --local-kamelet-dir=.