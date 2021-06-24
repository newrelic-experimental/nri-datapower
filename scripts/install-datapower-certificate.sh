#!/bin/bash

JAVA_HOME=/usr

MAIN_CLASS=com.newrelic.labs.infra.datapower.InstallCert

APP_HOME={{app-home-folder}}

# ***********************************************
# ***********************************************

ARGS="-Dlogback.configurationFile=$APP_HOME/config/logback.xml"

exec $JAVA_HOME/bin/java $ARGS -cp "$APP_HOME/nri-datapower.jar" $MAIN_CLASS %*