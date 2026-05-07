cd $GEMFIRE_HOME/bin

$GEMFIRE_HOME/bin/gfsh -e "start locator --name=locator-spring-office-hours --port=10334 --J=-Dgemfire.prometheus.metrics.emission=Default --J=-Dgemfire.prometheus.metrics.port=7977 --J=-Dgemfire.prometheus.metrics.host=127.0.0.1 --J=-Dgemfire.prometheus.metrics.interval=15s --bind-address=127.0.0.1 --J=-Dgemfire.enable-management-rest-service=true --J=-Dgemfire.enable-cluster-configuration=true  --initial-heap=512m --max-heap=512m  --J=-Dgemfire.start-rest-api=true"

curl http://127.0.0.1:7070/metrics
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "configure pdx --read-serialized=true --disk-store"
$GEMFIRE_HOME/bin/gfsh -e "start server --name=server1-spring-office-hours --locators=127.0.0.1[10334] --initial-heap=1g --max-heap=1g --server-port=2882 --J=-Dgemfire.prometheus.metrics.emission=Default --J=-Dgemfire.prometheus.metrics.port=7978 --J=-Dgemfire.prometheus.metrics.host=127.0.0.1 --J=-Dgemfire.prometheus.metrics.interval=15s --bind-address=127.0.0.1  --http-service-port=8590 --J=-Dgemfire.enable-management-rest-service=true --J=-Dgemfire.enable-cluster-configuration=true --J=-XX:+AlwaysPreTouch --start-rest-api=true "
curl http://127.0.0.1:8590/metrics