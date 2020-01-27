package com.gigaspaces.demo.kstreams.processors;

import com.gigaspaces.demo.kstreams.gks.Document;
import com.gigaspaces.demo.kstreams.gks.GigaStateStore;
import java.util.HashMap;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;

//public class CountingProcessor implements Processor<String, String> {
public class CountingProcessor {
/*
  private ProcessorContext context;
  private HashMap<String, Long> wordCount;
  private GigaStateStore store;

  @Override
  public void init(ProcessorContext processorContext) {
    this.context = processorContext;
    wordCount = new HashMap<>();

    store = (GigaStateStore) context.getStateStore(GigaStateStore.STORE_NAME);

  }

  @Override
  public void process(String key, String value) {

    //Stream<String> stream = Arrays.stream(value.split("\\s+"));

    String[] values = value.split("\\s+");
    for(int i=0; i < values.length; i++) {
      String word = values[i];
      if (wordCount.get(word) == null) {
        wordCount.put(word, 0L);
      }
      System.out.println(word+" "+wordCount.size());
      wordCount.put(word, wordCount.get(word)+1);
    };
    Document<String, Long> doc = new Document<>(key, wordCount);
    System.out.println(store != null);
    store.write("acc", doc);
  }

  @Override
  public void close() {

  }

 */
}
