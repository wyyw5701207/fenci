#!/bin/bash

this="$0"
while [ -h "$this" ]; do
  ls=`ls -ld "$this"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '.*/.*' > /dev/null; then
    this="$link"
  else
    this=`dirname "$this"`/"$link"
  fi
done

# the root of the Was installation
if [ -z "$META_HOME" ]; then
  export META_HOME=`dirname "$this"`
fi

META_HOME=`cd "$META_HOME"; pwd`
cd "$META_HOME" 

pid=$META_HOME/meta.pid
if [ -f $pid ]; then
  # kill -0 == see if the PID exists 
  if kill -0 `cat $pid` > /dev/null 2>&1; then
    echo -n stopping meta app
    kill `cat $pid` > /dev/null 2>&1
    while kill -0 `cat $pid` > /dev/null 2>&1; do
      echo -n "."
      sleep 1;
    done
    rm $pid
    echo
  else
    retval=$?
    echo no meta app to stop because kill -0 of pid `cat $pid` failed with status $retval
  fi
fi
