# spring-gemfire-office-hours

start gemfire

```shell
deployment/scripts/start-gemfire-external-clients.sh
```

or 




```shell
start locator --name=locator --initial-heap=1g --max-heap=1g
start server --name=server1 --initial-heap=5g --max-heap=5g
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