package com.gigaspaces.demo.kstreams.processors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gigaspaces.demo.kstreams.gks.Document;
import com.gigaspaces.demo.kstreams.gks.GigaStateStore;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;

public class CountingTransformer implements
    Transformer<String, String, KeyValue<String, String>> {

  private ProcessorContext context;
  private GigaStateStore store;
  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  public void init(ProcessorContext processorContext) {
   this.context = processorContext;
    store = (GigaStateStore) context.getStateStore(GigaStateStore.STORE_NAME);
  }

  @Override
  public KeyValue<String, String> transform(String key, String words) {

    HashMap<String, Long> wordCount = new HashMap<>();

    Document<String, Long> doc = store.read(key);

    String[] values = words.split("\\s+");
    for(int i=0; i < values.length; i++) {
      String word = values[i];
      if (wordCount.get(word) == null) {
        wordCount.put(word, 0L);
      }
      wordCount.put(word, wordCount.get(word)+1);
    };


    wordCount.forEach((key1, value) -> {
      long oValue = doc.content.get(key1) == null ? 0 : doc.content.get(key1);
      doc.content.put(key1, oValue+value);
    });

    store.write(key, doc);

    return new KeyValue<>(key, mapAsJson(wordCount));
  }

  @Override
  public void close() {

  }

  private String mapAsJson(Map<String, Long> map) {
    String jsonDoc = "";
    try {
      jsonDoc = mapper.writeValueAsString(map);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return jsonDoc;
  }
}
