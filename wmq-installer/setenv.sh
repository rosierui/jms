#!/bin/bash
#The scripts can be used for install wmq 8.0 on Ubutu linux 14.04.3
export WMQ_ARCHIVE=~/Downloads/sys-srv/mqadv_dev80_linux_x86-64.tar.gz
export CURR_DIR=`pwd`
export UNZIPPED_PATH=${CURR_DIR}/wmq_install_unzipped/MQServer
#export WMQ_INSTALL_DIR=/opt/mqm
export WMQ_INSTALL_DIR=/g01/srv/mqm

export LD_LIBRARY_PATH=$WMQ_INSTALL_DIR/java/lib64
export JAVA_HOME=$WMQ_INSTALL_DIR/java/jre64/jre
export PATH=$PATH:$WMQ_INSTALL_DIR/bin:$JAVA_HOME/bin
export QM=QM1
export PORT=1420
export REQUESTQ=REQUEST_Q
export REPLYQ=REPLY_Q


