#!/usr/bin/env bash

NAME=$(echo $(basename $0) | sed -e 's/^[SK][0-9]*//' -e 's/\.sh$//')

usage()
{
    echo "Usage: ${0##*/} {start|stop|restart|status}"
    exit 1
}

[ $# -gt 0 ] || usage

##################################################
# Utility functions
##################################################

running()
{
  local PID=$(cat "$1" 2>/dev/null) || return 1
  kill -0 "$PID" 2>/dev/null
}

start_server()
{
    echo -n "Starting {{ service_name }} Service: "

    if [ -f "$SERVICE_PID" ]
    then
        if running $SERVICE_PID
        then
          echo "Already Running!"
          exit 1
        else
          # dead pid file - remove
          rm -f "$SERVICE_PID"
        fi
    fi

    "${RUN_CMD[@]}" &
    disown $!
    echo $! > "$SERVICE_PID"
    
    echo OK
}

stop_server()
{
    echo -n "Stopping {{ service_name }} Server: "

    PID=$(cat "$SERVICE_PID" 2>/dev/null)
    kill "$PID" 2>/dev/null

    TIMEOUT=30
    while running $SERVICE_PID; do
    if (( TIMEOUT-- == 0 )); then
      kill -KILL "$PID" 2>/dev/null
    fi

    sleep 1
    done

    rm -f "$SERVICE_PID"
    echo OK
}

#####################################################
# Find a pid file
#####################################################
if [ -z "$SERVICE_PID" ] 
then
  SERVICE_PID="/tmp/${NAME}.pid"
fi

SERVICE_HOME=/opt/{{ service_name }}

##################################################
# Setup JAVA if unset
##################################################
if [ -z "$JAVA" ]
then
  JAVA=$(which java)
fi

if [ -z "$JAVA" ]
then
  echo "Cannot find a Java JDK. Please set either set JAVA or put java (>=1.5) in your PATH." 2>&2
  exit 1
fi

#####################################################
# This is how the Stardust server will be started
#####################################################

JAVA_OPTIONS+=("")

SERVICE_START=$SERVICE_HOME/{{ service_name }}.jar
if [ ! -f "$SERVICE_START" ]
then
  echo "Cannot find a {{ service_name }}.jar in your SERVICE_HOME directory: $SERVICE_HOME" 2>&2
  exit 1
fi

RUN_ARGS=(${JAVA_OPTIONS[@]} -jar "$SERVICE_START" ${SERVICE_ARGS[*]})
RUN_CMD=("$JAVA" ${RUN_ARGS[@]})

ACTION=$1


##################################################
# Do the action
##################################################
case "$ACTION" in
  start)
    start_server

    ;;

  stop)
    stop_server

    ;;

  restart)
    
    stop_server
    start_server

    ;;
  status)
    
    if [ -f "$SERVICE_PID" ]
    then
      echo "{{ service_name }} running pid=$(< "$SERVICE_PID")"
      exit 0
    fi
    exit 1

    ;;

  *)
    usage

    ;;
esac

exit 0
