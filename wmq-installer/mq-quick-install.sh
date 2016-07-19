#!/bin/bash
#07/19/16
#cd ${wmq_install_unzipped}/MQServer 
export WMQ_INSTALL_DIR=/g01/srv/mqm
sudo rpm --prefix $WMQ_INSTALL_DIR -ivh MQSeriesRuntime-*.rpm 
sudo rpm --prefix $WMQ_INSTALL_DIR -ivh MQSeriesJRE-*.rpm 
sudo rpm --prefix $WMQ_INSTALL_DIR -ivh MQSeriesJava-*.rpm 
sudo rpm --prefix $WMQ_INSTALL_DIR -ivh MQSeriesServer-*.rpm 
sudo rpm --prefix $WMQ_INSTALL_DIR -ivh MQSeriesClient-*.rpm  
sudo rpm --prefix $WMQ_INSTALL_DIR -ivh MQSeriesExplorer-*.rpm 
sudo rpm --prefix $WMQ_INSTALL_DIR -ivh MQSeriesMan-*.rpm 
sudo rpm --prefix $WMQ_INSTALL_DIR -ivh MQSeriesSDK-*.rpm  
sudo rpm --prefix $WMQ_INSTALL_DIR -ivh MQSeriesSamples-*.rpm   
