#sudo adduser mq_user
QMGR=QM1
PORT=1415
USER=mq_user
crtmqm $QMGR
strmqm $QMGR
runmqsc $QMGR <<EOF
	define qlocal(REQUEST_Q) maxdepth(5000)
	define qlocal(REPLY_Q) maxdepth(5000)
	alter qmgr chlauth(disabled) 
	alter qmgr maxmsgl(104857600)
	alter channel(system.def.svrconn) chltype(svrconn) mcauser($USER) maxmsgl(104857600)
	alter qlocal(system.default.local.queue) maxmsgl(104857600)
	alter qmodel(system.default.model.queue) maxmsgl(104857600)
        DEF CHL('JAVA.CHANNEL') CHLTYPE(SVRCONN) TRPTYPE(TCP) MCAUSER('') DESCR('Sample channel for WS MQ classes for Java')
	define listener(L1) trptype(tcp) port($PORT) control(qmgr)
	start listener(L1)
	alter channel(SYSTEM.DEF.SVRCONN) chltype(SVRCONN) sharecnv(1)
	define channel(system.admin.svrconn) chltype(svrconn) mcauser('mqm') replace
EOF
setmqaut -m $QMGR -t qmgr -p $USER +all
setmqaut -m $QMGR -t queue -n REQUEST_Q -p $USER +all

