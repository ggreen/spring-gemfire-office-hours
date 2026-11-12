#export GEMFIRE_HOME=/Users/devtools/repositories/IMDG/gemfire/vmware-gemfire-10.2.0
cd $GEMFIRE_HOME/bin

rm -rf $GEMFIRE_HOME/bin/*spring-office-hours*

$GEMFIRE_HOME/bin/gfsh -e "start locator --name=locator-spring-office-hours --port=10334 --J=-Dgemfire.prometheus.metrics.emission=Default --J=-Dgemfire.prometheus.metrics.port=7977 --J=-Dgemfire.prometheus.metrics.host=127.0.0.1 --J=-Dgemfire.prometheus.metrics.interval=15s --bind-address=127.0.0.1 --J=-Dgemfire.enable-management-rest-service=true --J=-Dgemfire.enable-cluster-configuration=true  --initial-heap=512m --max-heap=512m  --J=-Dgemfire.start-rest-api=true --J=-Dgemfire.tcp-port=40000"

$GEMFIRE_HOME/bin/gfsh -e "connect" -e "configure pdx --read-serialized=true --disk-store"
$GEMFIRE_HOME/bin/gfsh -e "start server --name=server1-spring-office-hours --locators=127.0.0.1[10334] --initial-heap=1g --max-heap=1g --server-port=2882 --J=-Dgemfire.prometheus.metrics.emission=Default --J=-Dgemfire.prometheus.metrics.port=7978 --J=-Dgemfire.tcp-port=40001 --J=-Dgemfire.prometheus.metrics.host=127.0.0.1 --J=-Dgemfire.prometheus.metrics.interval=15s --bind-address=127.0.0.1  --http-service-port=8080 --J=-Dgemfire.enable-management-rest-service=true --J=-Dgemfire.enable-cluster-configuration=true --J=-XX:+AlwaysPreTouch --start-rest-api=true "


$GEMFIRE_HOME/bin/gfsh -e "start server --name=server2-spring-office-hours --locators=127.0.0.1[10334] --initial-heap=1g --max-heap=1g --server-port=2883 --J=-Dgemfire.prometheus.metrics.emission=Default --J=-Dgemfire.prometheus.metrics.port=7973 --J=-Dgemfire.tcp-port=40003 --J=-Dgemfire.prometheus.metrics.host=127.0.0.1 --J=-Dgemfire.prometheus.metrics.interval=15s --bind-address=127.0.0.1  --http-service-port=8083 --J=-Dgemfire.enable-management-rest-service=true --J=-Dgemfire.enable-cluster-configuration=true --J=-XX:+AlwaysPreTouch --start-rest-api=true "


$GEMFIRE_HOME/bin/gfsh -e "connect" -e "create region --name=Employee --type=PARTITION --skip-if-exists"
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "create region --name=AiCache --type=PARTITION --skip-if-exists"