#!/bin/bash
. ./setenv.sh
$WMQ_INSTALL_DIR/bin/setmqenv -s
endmqm QM1
endmqm QM2

sudo rpm -ev MQSeriesJava
sudo rpm -ev MQSeriesSamples
sudo rpm -ev MQSeriesMan
sudo rpm -ev MQSeriesSDK
sudo rpm -ev MQSeriesClient
sudo rpm -ev MQSeriesServer
sudo rpm -ev MQSeriesExplorer
sudo rpm -ev MQSeriesJRE
sudo rpm -ev MQSeriesRuntime
