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


# Agenda


  - Put & Get
  - Performance Test
  - Load Data
  - Queries
  - Event/ Continuous Queries
  - Artificial Intelligence


Example Queries


```sql
select distinct location.state from /Employee
```

```sql
    select avg(salary) as avg_salary, max(salary) as max_salary
    from /Employee
    where dept = 'Tanzu'

```


## CQ

```shell
curl -X 'PUT' \
'http://localhost:9090/api/employees/rich' \
-H 'accept: application/vnd.hal+json' \
-H 'Content-Type: application/json' \
-d '{"dept":"rich","email":"rich@rich.com","firstName":"Richie","hireDate":1779299361566,"id":"rich","jobTitle":"Big Shot","lastName":"Rich","location":{"city":null,"country":{"code":null,"name":"United States"},"id":"dreamLand","state":"NY","streetAddress":null,"zipCode":"12345"},"notes":"Must be nice","salary":2000000}'
```

## Ai Example

Context

```text
VMware Tanzu GemFire (often just called GemFire) is a high-performance, distributed, in-memory data grid (IMDG) and key-value store.  Instead of relying on traditional, disk-based databases which can introduce latency, GemFire pools the random-access memory (RAM) across a cluster of multiple servers to store and manage data. This allows applications to read and write data at ultra-low, sub-millisecond speeds.  Because of its extreme speed, reliability, and high consistency, GemFire is heavily used by large enterprises in industries like financial services (e.g., trading platforms), e-commerce (e.g., shopping carts), and real-time logistics.  
```

```shell
curl -X 'POST' \
  'http://localhost:9090/ai/add/document' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '"VMware Tanzu GemFire (often just called GemFire) is a high-performance, distributed, in-memory data grid (IMDG) and key-value store.  Instead of relying on traditional, disk-based databases which can introduce latency, GemFire pools the random-access memory (RAM) across a cluster of multiple servers to store and manage data. This allows applications to read and write data at ultra-low, sub-millisecond speeds.  Because of its extreme speed, reliability, and high consistency, GemFire is heavily used by large enterprises in industries like financial services (e.g., trading platforms), e-commerce (e.g., shopping carts), and real-time logistics."'
```

Question: what is GemFire?

```shell
curl -X 'POST' \
  'http://localhost:9090/ai/ask' \
  -H 'accept: application/vnd.hal+json' \
  -H 'Content-Type: application/json' \
  -d '"what is GemFire?"'
```


Add URL

https://blogs.vmware.com/tanzu/introducing-vmware-gemfire-10-ga/

```shell
curl -X 'POST' \
  'http://localhost:9090/ai/add/url' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '"https://blogs.vmware.com/tanzu/introducing-vmware-gemfire-10-ga/"'
```

"Was GemFire 10 named after anyone"

```shell
curl -X 'POST' \
  'http://localhost:9090/ai/ask' \
  -H 'accept: application/vnd.hal+json' \
  -H 'Content-Type: application/json' \
  -d '"Was GemFire 10 named after anyone""'
```


---------------

Running AI data Pipeline with Spring AI + GemFire

```shell
curl -X 'POST' \
  'http://localhost:9090/ai/add/employee/knowledge' \
  -H 'accept: */*' \
  -d ''
```

We are launching a new mission-critical application development GemFire project in the next month. Based on their notes which employees should we assign to it?


```shell
curl -X 'POST' \
  'http://localhost:9090/ai/ask' \
  -H 'accept: application/vnd.hal+json' \
  -H 'Content-Type: application/json' \
  -d '"We are launching a new mission-critical application development GemFire project in the next month. Based on their notes which employees should we assign to it?"'
```