PROJECT_HOME=$PWD


$GEMFIRE_HOME/bin/gfsh -e "connect --locator=localhost[10334]" -e "shutdown --include-locators"

$GEMFIRE_HOME/bin/gfsh -e "connect --locator=localhost[10002]"  -e  "cshutdown --include-locators"