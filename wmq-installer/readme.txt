Credit - Roman Kharkovski

Download from https://ibmadvantage.com/2014/01/23/install-websphere-mq-v7-5-on-red-hat-linux-in-under-1-minute-with-one-click/
https://www.dropbox.com/sh/gqotv04z0hi40od/_imdUP8Xpa

Modified to install mqadv_dev80_linux_x86-64.tar.gz on Ubuntu 14.04.3

update line 3 of setenv.sh as need

cd /path-to/wmq-installer/
sudo ./mqinstall.sh

exit mq explorer started by mqinstall.sh

start a new mq explorer

${WMQ_INSTALL_DIR}/bin/MQExplorer

then run the following script for test
./mqtest.sh

verify the console output against the data in mq explorer
