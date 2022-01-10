package com.cybereason.xdr.service.impl;

import com.cybereason.xdr.config.BigTableConfig;
import com.cybereason.xdr.config.KafkaConfig;
import com.cybereason.xdr.config.PubsubConfig;
import com.cybereason.xdr.model.*;
import com.cybereason.xdr.service.XDRService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.rpc.NotFoundException;
import com.google.api.gax.rpc.ServerStream;
import com.google.cloud.bigtable.data.v2.models.Query;
import com.google.cloud.bigtable.data.v2.models.Row;
import okhttp3.Response;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class XDRServiceImpl implements XDRService {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(XDRServiceImpl.class);
    private static final String BASE_URL = "http://identity-service:8086";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient httpClient = new OkHttpClient();

    public static Map<String, String> msToTopic;

    static {
        msToTopic = new HashMap<>();
        msToTopic.put("transformer", "research-idm-topic-sub");
        msToTopic.put("identity", "research-router-topic-sub");
        msToTopic.put("threat", "research-router-topic-sub");
        msToTopic.put("router", "research-router-topic-sub");
        msToTopic.put("transparency", "research-router-topic-sub");

    }

    @Override
    public String process(String message, String msName) {
        LOGGER.debug("Received a message to deliver: " + message);
        return publishAndConsume(message, msName);
    }

    @Override
    public String getUserAccounts(String tenantID) throws JsonProcessingException {
        Response response = null;
        UserAccountRequest userAccountRequest = UserAccountRequest.builder().tenantID(tenantID).build();
        RequestBody body = RequestBody.Companion.create(objectMapper.writeValueAsString(userAccountRequest), JSON);
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/user-account/get/tenant")
                .post(body)
                .build();

        Call call = httpClient.newCall(request);
        try {
            response = call.execute();
        } catch (IOException e) {
            LOGGER.error("Failed to execute the request: ", e.getMessage(), e);
        }
        if (response == null)
            return "OK";
        else {
            try {
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "OK";
        }
    }

    @Override
    public String getBigtableData(String type) throws JsonProcessingException {
        List<String> tableNames;
        if ("accounts".equalsIgnoreCase(type)) {
            List<BigtableAccountsData> bigtableDataList = new ArrayList<>();
            try {
                tableNames = BigTableConfig.bigtableAdmin().listTables();
                for (String tableName : tableNames) {
                    Query query = Query.create(tableName).prefix("account");
                    ServerStream<Row> rows = BigTableConfig.bigtableDataClient().readRows(query);
                    for (Row row : rows) {
                        String key = row.getKey().toStringUtf8();
                        UserAccount userAccount = objectMapper.readValue(row.getCells().get(0).getValue().toStringUtf8(), UserAccount.class);
                        BigtableAccountsData bigtableData = BigtableAccountsData.builder().key(key).userAccount(userAccount).tableName(tableName).build();
                        bigtableDataList.add(bigtableData);
                    }
                }
                return objectMapper.writeValueAsString(bigtableDataList);
            } catch (NotFoundException e) {
                System.err.println("Failed to list tables from a non-existent instance: " + e.getMessage());
            }
        } else {
            try {
                List<BigtableIdentitiesData> bigtableDataList = new ArrayList<>();
                tableNames = BigTableConfig.bigtableAdmin().listTables();
                for (String tableName : tableNames) {
                    Query query = Query.create(tableName).prefix("identity");
                    ServerStream<Row> rows = BigTableConfig.bigtableDataClient().readRows(query);
                    for (Row row : rows) {
                        String key = row.getKey().toStringUtf8();
                        UserIdentity userIdentity = objectMapper.readValue(row.getCells().get(0).getValue().toStringUtf8(), UserIdentity.class);
                        BigtableIdentitiesData bigtableData = BigtableIdentitiesData.builder().key(key).userIdentity(userIdentity).tableName(tableName).build();
                        bigtableDataList.add(bigtableData);
                    }
                }
                return objectMapper.writeValueAsString(bigtableDataList);
            } catch (NotFoundException e) {
                System.err.println("Failed to list tables from a non-existent instance: " + e.getMessage());
            }
        }
        return null;
    }

    private String publishAndConsume(String message, String msName) {
        String topic = msToTopic.get(msName.toLowerCase());

        try {
            PubsubConfig.publish(message);
            return PubsubConfig.subscribe(msName, topic);
        } catch (IOException | ExecutionException | InterruptedException | TimeoutException e) {
            LOGGER.error("Failed to produce PubSub Message:", e.getMessage(), e);
        }
        return null;
    }

    private boolean publishToKafkaTopic(String message) {
        KafkaConfig.runProducer(message);
        LOGGER.info("Produced the following message: " + message);
        return true;
    }

    private String subscribeToKafkaTopic(String msName) {
        String topic = msToTopic.get(msName.toLowerCase());
        LOGGER.info("*** Kafka Consumer for topic: " + topic + " is up and running ***");
        return KafkaConfig.runConsumer(topic);
    }
}
