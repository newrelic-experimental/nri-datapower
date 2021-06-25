#!/bin/bash

JAVA_HOME=/usr

MAIN_CLASS=com.newrelic.labs.infra.datapower.Main

APP_HOME={{nri-datapower-home}}

# ***********************************************
# ***********************************************

ARGS="-Dlogback.configurationFile=$APP_HOME/config/logback.xml"

exec $JAVA_HOME/bin/java $ARGS -cp $APP_HOME/nri-datapower.jar $MAIN_CLASS -config_file $APP_HOME/datapower-config.yml