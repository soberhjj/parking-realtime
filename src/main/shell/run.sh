#!/bin/sh
source ~/.bashrc
#工程名称
PROJECT_NAME=publicopinion-realtime
#脚本路径
SCRIPT_PATH="${BASE_SCRIPT_PATH}/${PROJECT_NAME}"
#日志输出目录
LOG_PATH="${BASE_LOG_PATH}/${PROJECT_NAME}"

JAR_FILE_PATH=${SCRIPT_PATH}/publicopinion-realtime-1.0.0-shaded.jar

nohup spark-submit \
--master yarn \
--deploy-mode cluster \
--name publicopinion-realtime  \
--conf spark.streaming.stopGracefullyOnShutdown=true \
--conf spark.serializer=org.apache.spark.serializer.KryoSerializer \
--conf spark.streaming.unpersist=true \
--conf spark.dynamicAllocation.enabled=false \
--conf spark.streaming.kafka.maxRetries=2 \
--conf spark.streaming.kafka.maxRatePerPartition=1000 \
--conf spark.yarn.am.attemptFailuresValidityInterval=1h \
--conf spark.yarn.max.executor.failures=3 \
--conf spark.yarn.executor.failuresValidityInterval=1h \
--conf spark.task.maxFailures=8 \
--class com.newland.publicopinion.Main \
${JAR_FILE_PATH} \
-duration 60  -brokers 192.168.136.22:9092 -topic spider_news -consumer publicopinion-realtime_20190101 \
>> ${LOG_PATH}/run.log 2>&1 &





