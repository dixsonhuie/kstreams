package com.gigaspaces.demo.kstreams.gks;

import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.internals.ProcessorStateManager;
import org.apache.kafka.streams.processor.internals.RecordCollector;
import org.apache.kafka.streams.state.StateSerdes;

public class GigaChangeLogger<K, V> {

  private final String topic;
  private final int partition;
  private final ProcessorContext context;
  private final RecordCollector collector;
  private final Serializer<K> keySerializer;
  private final Serializer<V> valueSerializer;

  GigaChangeLogger(final String storeName,
                   final ProcessorContext context,
                   final StateSerdes<K, V> serialization) {
    this(storeName, context, context.taskId().partition, serialization);
  }

  private GigaChangeLogger(final String storeName,
                           final ProcessorContext context,
                           final int partition,
                           final StateSerdes<K, V> serialization) {
    topic = ProcessorStateManager.storeChangelogTopic(context.applicationId(), storeName);
    this.context = context;
    this.partition = partition;
    this.collector = ((RecordCollector.Supplier) context).recordCollector();
    keySerializer = serialization.keySerializer();
    valueSerializer = serialization.valueSerializer();
  }

  void logChange(final K key,
      final V value) {
    logChange(key, value, context.timestamp());
  }

  void logChange(final K key,
      final V value,
      final long timestamp) {
    // Sending null headers to changelog topics (KIP-244)
    collector.send(topic, key, value, null, partition, timestamp, keySerializer, valueSerializer);
  }

}
