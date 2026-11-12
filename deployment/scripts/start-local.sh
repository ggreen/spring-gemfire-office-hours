#export GEMFIRE_HOME=/Users/devtools/repositories/IMDG/gemfire/vmware-gemfire-10.2.0

ROOT_DIR=$PWD
mkdir -p runtime/locator1
mkdir -p runtime/server1-spring-office-hours
mkdir -p runtime/server2-spring-office-hours
GF_VERSION=10.3.4


wait_for_port() {
  local host="${1:-localhost}"
  local port="${2:?Port is required}"
  local timeout="${3:-30}"
  local start_time=$SECONDS

  echo "Waiting for $host:$port..."
  while ! nc -z "$host" "$port" 2>/dev/null; do
    if (( SECONDS - start_time >= timeout )); then
      echo "Timed out waiting for $host:$port" >&2
      return 1
    fi
    sleep 0.5
  done

  echo "$host:$port is open!"
}


cd $ROOT_DIR/runtime/locator1
java -Xms512m -Xmx512m -Dgemfire.prometheus.metrics.emission=Default -Dgemfire.prometheus.metrics.port=7977 -Dgemfire.prometheus.metrics.host=127.0.0.1 -Dgemfire.prometheus.metrics.interval=15s  -Dgemfire.enable-management-rest-service=true -Dgemfire.enable-cluster-configuration=true    -Dgemfire.start-rest-api=true -Dgemfire.tcp-port=40000 -jar $GEMFIRE_JAR_DIR/vmware-gemfire-$GF_VERSION.jar locator --name=locator1 --port=10334  --dir=$PWD/runtime/locator1 --bind-address=127.0.0.1 &


wait_for_port localhost 7977

echo "Configuring PDX"
java -jar $GEMFIRE_JAR_DIR/vmware-gemfire-gfsh-$GF_VERSION.jar -e "connect" -e "configure pdx --read-serialized=true --disk-store"

echo "Starting server1"
cd $ROOT_DIR/runtime/server1-spring-office-hours
java -Xms1g -Xmx1g -Dgemfire.prometheus.metrics.emission=Default -Dgemfire.prometheus.metrics.port=7978 -Dgemfire.tcp-port=40001 -Dgemfire.prometheus.metrics.host=127.0.0.1 -Dgemfire.prometheus.metrics.interval=15s -Dgemfire.enable-management-rest-service=true -Dgemfire.enable-cluster-configuration=true -XX:+AlwaysPreTouch -jar $GEMFIRE_JAR_DIR/vmware-gemfire-${GF_VERSION}.jar server --name=server1-spring-office-hours --locators="127.0.0.1[10334]"  --server-port=2882  --bind-address=127.0.0.1  --http-service-port=8080  --start-rest-api=true --dir=$PWD/runtime/server1-spring-office-hours &



echo "start server 2"

cd $ROOT_DIR/runtime/server2-spring-office-hours
java -Xms1g -Xmx1g -Dgemfire.prometheus.metrics.emission=Default -Dgemfire.prometheus.metrics.port=7973 -Dgemfire.tcp-port=40003 -Dgemfire.prometheus.metrics.host=127.0.0.1 -Dgemfire.prometheus.metrics.interval=15s -Dgemfire.enable-management-rest-service=true -Dgemfire.enable-cluster-configuration=true -XX:+AlwaysPreTouch -jar $GEMFIRE_JAR_DIR/vmware-gemfire-${GF_VERSION}.jar server --name=server2-spring-office-hours --locators="127.0.0.1[10334]" --server-port=2883 --bind-address=127.0.0.1 --http-service-port=8083 --start-rest-api=true --dir=$PWD/runtime/server2-spring-office-hours &

wait_for_port localhost 8080
wait_for_port localhost 8083
#curl http://127.0.0.1:8080/metrics/
#curl http://127.0.0.1:8083/metrics


java -jar $GEMFIRE_JAR_DIR/vmware-gemfire-gfsh-$GF_VERSION.jar -e "connect" -e "create region --name=Employee --type=PARTITION --skip-if-exists"
java -jar $GEMFIRE_JAR_DIR/vmware-gemfire-gfsh-$GF_VERSION.jar -e "connect" -e "create region --name=AiCache --type=PARTITION --skip-if-exists"