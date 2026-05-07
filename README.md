# spring-gemfire-office-hours

start gemfire

```shell
deployment/scripts/start-gemfire-external-clients.sh
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