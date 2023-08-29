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
   if kill -0 `cat $pid` > /dev/null 2>&1; then
     echo meta app is running as process `cat $pid`.  Stop it first.
     exit 1
   fi
fi

_JAVA_DEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,address=8919,server=y,suspend=n"
export OPTION="-Dfile.encoding=UTF-8 -server -Xms2g -Xmx16g $_JAVA_DEBUG -Xss768k -XX:+UnlockExperimentalVMOptions -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintHeapAtGC -XX:+PrintGCApplicationStoppedTime -Xloggc:gc.log -Dproc_server"

echo "Running command:"
echo "nohup java $OPTION -jar $META_HOME/analyzer-1.0.jar > $META_HOME/nohup.out 2>&1 &"
nohup java $OPTION -jar $META_HOME/analyzer-1.0.jar > $META_HOME/nohup.out 2>&1 &
#nohup java $OPTION -jar $META_HOME/guns.jar --spring.config.location=$META_HOME/application-dev.yml > $META_HOME/nohup.out 2>&1 &
echo $! > $pid
