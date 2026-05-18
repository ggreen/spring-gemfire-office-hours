# spring-gemfire-office-hours

Prerequisite 

- GemFire binaries and Java 17 or higher installed

## Getting Start



start gemfire

Option 1 (Podman required)
```shell
deployment/scripts/start-gemfire-containers.sh
```

or Option 2 (locally) 

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


"Was GemFire 10 named after anyone"