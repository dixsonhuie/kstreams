#!/usr/bin/env bash

if [ -z "$KAFKA_HOME" ]; then
    echo "Please set KAFKA_HOME environment variable."
    exit 1
fi

## only shows values
## $KAFKA_HOME/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic source-topic --from-beginning


## show keys
   $KAFKA_HOME/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic source-topic --from-beginning \
   --property "print.key=true" \
   --property "key.separator=:"
