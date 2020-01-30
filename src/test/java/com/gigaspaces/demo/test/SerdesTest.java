package com.gigaspaces.demo.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gigaspaces.demo.kstreams.gks.Document;

/*
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.core.IsEqual.equalTo;

import com.gigaspaces.demo.kstreams.gks.GigaStateStore;
import com.gigaspaces.demo.kstreams.gks.GigaStoreBuilder;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.kstream.TransformerSupplier;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
*/

import com.gigaspaces.document.DocumentProperties;
import com.gigaspaces.document.SpaceDocument;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class SerdesTest {


    @Before
    public void before() throws InterruptedException {

    }

    @After
    public void after() {
    }

    @Test
    public void testSerDes() {
        Map map = new HashMap<String,String>();

        map.put("notes", "Not suitable for small children.");
        map.put("desc", "This is a valuable product.");
        Document value = new Document("1", map);

        String jsonContent = null;

        ObjectMapper mapper = new ObjectMapper();

        try {
            jsonContent = mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            jsonContent = "";
        }
        System.out.println("jsonContent is: " + jsonContent);
    }

    @Test
    public void testSerDes1() {
        Map map = new HashMap<String,String>();

        map.put("notes", "Not suitable for small children.");
        map.put("desc", "This is a valuable product.");
        Document value = new Document(null, map);

        String jsonContent = null;

        ObjectMapper mapper = new ObjectMapper();

        try {
            jsonContent = mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            jsonContent = "";
        }
        System.out.println("jsonContent is: " + jsonContent);
    }

    @Test
    public void testSerDer2() {
        ObjectMapper mapper = new ObjectMapper();

        DocumentProperties properties = new DocumentProperties()
                .setProperty("docId", "1")
                .setProperty("content", new DocumentProperties()
                        .setProperty("Manufacturer", "Acme")
                        .setProperty("RequiresAssembly", true)
                        .setProperty("NumberOfParts", 42));

        SpaceDocument spaceDocument = new SpaceDocument("Product", properties);

        try {
            String jsonFromGrid = new ObjectMapper().writeValueAsString(spaceDocument);
            System.out.println("jsonFromGrid is: " + jsonFromGrid);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        String docId = spaceDocument.getProperty("docId");
        Map content = spaceDocument.getProperty("content");

        Document value = new Document(docId, content);
        String jsonContent = null;
        try {
            jsonContent = mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            jsonContent = "";
        }
        System.out.println("jsonContent is: " + jsonContent);

    }

}
