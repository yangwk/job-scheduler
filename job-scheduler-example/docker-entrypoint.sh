#!/usr/bin/env bash

param=""

vm_opts="${JVM_OPTS}"

if [[ ! -n $vm_opts ]] ; then
	vm_opts="-XX:+UseParallelGC -Xmx4g"
fi

echo "all env : "
env

jarFile=$(ls . | grep jar)

echo "java ${vm_opts} -jar ${jarFile} ${param}"

exec java ${vm_opts} -jar ${jarFile} ${param}
