curl -X POST http://localhost:8080/gemfire-vectordb/v1/indexes -H "Content-Type: application/json" -d '{"name": "quickstart", "fields": ["country"]}'



spring-ai-gemfire-store

curl -X POST http://localhost:8080/gemfire-vectordb/v1/indexes -H "Content-Type: application/json" -d '{"name": "spring-ai-gemfire-store"}'

List Indexes

curl http://localhost:8080/gemfire-vectordb/v1/indexes


curl http://localhost:8080/gemfire-vectordb/v1/indexes/quickstart


# spring-gemfire-office-hours

start gemfire

```shell
deployment/scripts/start-gemfire-containers.sh
```

or 

```shell
deployment/scripts/start-local.sh
```


Start Gfsh

```shell
$GEMFIRE_HOME/bin/gfsh
```

Connect
```shell
connect
```

```shell
create region --name=Employee --type=PARTITION --skip-if-exists
```

https://blogs.vmware.com/tanzu/introducing-vmware-gemfire-10-ga/