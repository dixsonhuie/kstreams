#!/usr/bin/env bash

if [ -z "$KAFKA_HOME" ]; then
    echo "Please set KAFKA_HOME environment variable."
    exit 1
fi

# this command expects input to be entered from the command line. Use ctrl-d to exit.
# [2020-01-24 16:13:42,369] WARN [Producer clientId=console-producer] Error while fetching metadata with correlation id 3 : {messages=LEADER_NOT_AVAILABLE} (org.apache.kafka.clients.NetworkClient)
$KAFKA_HOME/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic source-topic