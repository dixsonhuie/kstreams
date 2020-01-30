package com.gigaspaces.demo.kstreams.gks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gigaspaces.demo.kstreams.SerdesFactory;

import java.rmi.RemoteException;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.UrlSpaceConfigurer;

import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.metadata.SpaceTypeDescriptorBuilder;
import com.gigaspaces.metadata.index.SpaceIndexType;
import com.gigaspaces.query.IdQuery;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.StateStore;
import org.apache.kafka.streams.state.StateSerdes;


/*
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
*/


public class GigaStateStore implements StateStore, GigaWritableStore<String, Document> {

    /* In Elasticsearch, an INDEX can be thought as logical area much like a db in a dbms or space in Gigaspaces.
     */

    public static final String INDEX = "words";
    public static final String TYPE_DESCRIPTOR_NAME = INDEX;

    public static String STORE_NAME = "GigaStateStore";

    private final String hostAddr;

    private final ObjectMapper mapper = new ObjectMapper();
    private GigaChangeLogger<String,Document> changeLogger = null;
    private GigaSpace client;
    private ProcessorContext context;
    private long updateTimestamp;
    private Document value;
    private String key;
    private Serde<Document> docSerdes;

    public GigaStateStore(String hostAddr) {
        this.hostAddr = hostAddr;
    }

    @Override // GigaReadableStore
    public Document read(String key) {
        Document doc = null;

        if (key == null) {
            return new Document();
        }
    /*
    GetRequest request = new GetRequest(INDEX, INDEX, key);
    try {
      GetResponse response = client.get(request, RequestOptions.DEFAULT);

      String source = response.getSourceAsString();
      doc = mapper.readValue(source, Document.class);

    } catch (IOException e) {
      e.printStackTrace();
    }
*/
        SpaceDocument spaceDocument = client.readById(new IdQuery<SpaceDocument>(TYPE_DESCRIPTOR_NAME, key));
        if( spaceDocument != null ) {
            String docId = spaceDocument.getProperty("docId");
            Map content = spaceDocument.getProperty("content");

            doc = new Document(docId, content);

            return doc;
        }
        else { // no document found
            doc = new Document(key, new HashMap<String, Long>());
            return doc;
        }
    }

    @Override // GigaReadableStore
    public List<Document> search(String words, String... fields) {
    /*
    if (words.length() == 0) {
      return new ArrayList<>();
    }

    List<Document> results = new ArrayList<>();

    SearchRequest request = new SearchRequest(INDEX);

    SearchSourceBuilder builder = new SearchSourceBuilder();
    builder.query(QueryBuilders.multiMatchQuery(words, fields));
    request.source(builder);

    try {
      SearchResponse response = client.search(request, RequestOptions.DEFAULT);
      for (SearchHit hit : response.getHits()) {
        String source = hit.getSourceAsString();
        Document doc = mapper.readValue(source, Document.class);
        results.add(doc);
      }

    } catch (IOException e) {
      e.printStackTrace();
    }

    return results;

     */
        return null;
    }

    @Override // GigaWritableStore
    public void write(String key, Document value) {
        this.key = key;
        this.value = value;


/*
    String jsonContent;
    try {
      jsonContent = mapper.writeValueAsString(value);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      jsonContent = "";
    }

    IndexRequest request = new IndexRequest(INDEX, INDEX, key);
    request.source(jsonContent, XContentType.JSON);


    try {
      client.index(request);
    } catch (IOException e) {
      e.printStackTrace();
    }
 */
/*
    HashMap<String,Object> jsonProperties =
            null;
    try {
      jsonProperties = new ObjectMapper().readValue(jsonContent, HashMap.class);

      SpaceDocument dataAsDocument = new SpaceDocument(TYPE_DESCRIPTOR_NAME, jsonProperties);

      client.write(dataAsDocument);

    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
*/
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("docId", key);
        properties.put("content", value.content);

        SpaceDocument spaceDocument = new SpaceDocument(TYPE_DESCRIPTOR_NAME, properties);

        client.write(spaceDocument);

        this.updateTimestamp = System.currentTimeMillis();
    }

    @Override // StateStore
    public String name() {
        return STORE_NAME;
    }

    @Override // StateStore
    public void init(ProcessorContext processorContext, StateStore stateStore) {

        context = processorContext;
/*
    final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    credentialsProvider.setCredentials(
        AuthScope.ANY, new UsernamePasswordCredentials("elastic", "changeme"));

    client = new RestHighLevelClient(
        RestClient.builder(
            HttpHost.create(hostAddr))
        .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
    );
*/
        UrlSpaceConfigurer configurer = new UrlSpaceConfigurer("jini://*/*/" + INDEX);
        client = new GigaSpaceConfigurer(configurer).gigaSpace();

        HashMap<String,Long> map = new HashMap<>();
        // register type
        SpaceTypeDescriptor typeDescriptor = new SpaceTypeDescriptorBuilder(TYPE_DESCRIPTOR_NAME)
                .idProperty("docId", false, SpaceIndexType.EQUAL)
                .addFixedProperty("content", map.getClass())
                .addPropertyIndex("content", SpaceIndexType.EQUAL)
                .create();

        client.getTypeManager().registerTypeDescriptor(typeDescriptor);

        docSerdes = SerdesFactory.from(Document.class);

        StateSerdes<String,Document> serdes = new StateSerdes(
                name(),
                Serdes.String(),
                docSerdes);

        changeLogger = new GigaChangeLogger<>(name(), context, serdes);

        context.register(this, (key, value) -> {
            // here the store restore should happen from the changelog topic.
      /*
      // commented because another write is in the transform() method
      String sKey = new String(key);
      Document docValue = docSerdes.deserializer().deserialize(sKey, value);
      write(sKey, docValue);

       */
        });
    }

    @Override //StateStore
    public void flush() {
    /*
      Definition of a flush, for which there is no GigaSpace equivalent
       - Flush essentially means that all the documents in the in-memory buffer are written to new Lucene segments,
       - These, along with all existing in-memory segments, are committed to the disk, which clears the translog.
       - This commit is essentially a Lucene commit.

    org.elasticsearch.action.admin.indices.flush.FlushRequest FlushRequest request = new FlushRequest(INDEX);

    try {
      client.indices().flush(request, RequestOptions.DEFAULT);
    } catch (IOException e) {
      e.printStackTrace();
    }
    */
        changeLogger.logChange(key, value, updateTimestamp);
    }

    @Override // StateStore
    public void close() {
    /*
      We don't have a GigaSpace.close equivalent, unless we use internal API
    try {
      client.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    */
    }

    @Override //StateStore
    public boolean persistent() {
        return true;
    }

    @Override //StateStore
    public boolean isOpen() {
    /*
    try {
      return client.ping(RequestOptions.DEFAULT);
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }

     */
        try {
            client.getSpace().ping();
            return true;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

}
