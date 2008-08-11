#!/bin/sh

COMMAND=/bogus/path/to/servicecontainerdriver

case "$1" in
    start)
		[ -f $COMMAND ] || {
			echo "$0: $COMMAND not found"
			exit 1
		}
		[ -x $COMMAND ] || chmod u+x $COMMAND
		$COMMAND	
	    ;;
    stop)
		pkill -f transfer-services-co
        ;;
    restart)
		$0 stop
		sleep 5
		$0 start
		;;
    *)
        echo "Usage: $0 {start|stop|restart}"
        exit 1
        ;;
esac

