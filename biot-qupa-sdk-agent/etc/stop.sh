CONTAINER_JAR=biot-taiji-sdk-agent-0.9.jar
ps_option="-u ${usr_name} -f"
ps_count=$(ps ${ps_option} | grep -v 'grep ' | grep -v 'kill ' | grep -v 'tail ' | grep -v 'vi ' | grep -i -c "${CONTAINER_JAR}" )
if [ 0 -lt ${ps_count} ]; then
	while [ 0 -lt ${ps_count} ]; do
		kill -TERM $(ps ${ps_option} | grep -v 'grep ' | grep -v 'kill ' | grep -v 'tail ' | grep -v 'vi ' | grep -i "${CONTAINER_JAR}" | awk '{print $2}' | sort -u)
		sleep 1
		ps_count=$(ps ${ps_option} | grep -v 'grep ' | grep -v 'kill ' | grep -v 'tail ' | grep -v 'vi ' | grep -i -c "${CONTAINER_JAR}" )			
	done
	exit 0	
else
	echo " "
	echo ">> CONTAINER '${CONTAINER_JAR}' is not running."
	echo " "
	exit 0	
fi
