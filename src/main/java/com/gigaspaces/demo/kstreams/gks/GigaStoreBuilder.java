package com.gigaspaces.demo.kstreams.gks;

import java.util.Map;
import org.apache.kafka.streams.state.StoreBuilder;

public class GigaStoreBuilder implements StoreBuilder<GigaStateStore> {

  private final String hostAddr;
  private Map<String, String> config;

  public GigaStoreBuilder() {
    this("http://localhost:9200");
  }

  public GigaStoreBuilder(String hostAddr) {
    this.hostAddr = hostAddr;
  }

  @Override
  public StoreBuilder<GigaStateStore>  withCachingEnabled() {
    return this;
  }

  @Override
  public StoreBuilder<GigaStateStore>  withCachingDisabled() {
    return this;
  }

  @Override
  public StoreBuilder<GigaStateStore> withLoggingEnabled(Map<String, String> config) {
    this.config = config;
    return this;
  }

  @Override
  public StoreBuilder<GigaStateStore>  withLoggingDisabled() {
    return this;
  }

  @Override
  public GigaStateStore build() {
    return new GigaStateStore(hostAddr);
  }

  @Override
  public Map<String, String> logConfig() {
    return config;
  }

  @Override
  public boolean loggingEnabled() {
    return false;
  }

  @Override
  public String name() {
    return GigaStateStore.STORE_NAME;
  }
}
