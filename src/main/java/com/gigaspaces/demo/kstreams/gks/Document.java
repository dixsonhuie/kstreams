package com.gigaspaces.demo.kstreams.gks;

import java.util.HashMap;
import java.util.Map;

public class Document<K,V> {


  public String docId;
  //public Map<String, String> headers;
  public Map<K,V> content;

  public Document() {
    this(null, new HashMap<>());
  }
  public Document(String docId, Map<K,V> content) {
    this.content = content;
    //this.headers = new HashMap<>();
    this.docId = docId;
  }
}
