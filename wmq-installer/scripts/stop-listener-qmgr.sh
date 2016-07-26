#usage $0 QMGR
# e.g $0 QM1
dspmq
echo

ps -ef | grep runmq
endmqlsr -w -m $1
echo

ps -ef | grep runmq
dspmq
echo

endmqm $1
sleep 5
#dltmqm $1
