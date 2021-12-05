package com.cybereason.xdr.service.impl;

import com.cybereason.xdr.config.KafkaConfig;
import com.cybereason.xdr.config.PubsubConfig;
import com.cybereason.xdr.model.UserAccountRequest;
import com.cybereason.xdr.service.XDRService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class XDRServiceImpl implements XDRService {
    private static final Logger LOGGER = LoggerFactory.getLogger(XDRServiceImpl.class);
    private static final String BASE_URL = "http://localhost:8081";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient httpClient = new OkHttpClient();

    static {
        msToTopic = new HashMap<>();
        msToTopic.put("transformer", "research-chronicle-topic-sub");
        msToTopic.put("identity", "research-idm-topic-sub");
        msToTopic.put("threat", "research-threat-topic-sub");
        msToTopic.put("router", "research-router-topic-sub");
    }

    public static Map<String, String> msToTopic;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String process(String message, String msName) {
        LOGGER.info("Received a message to deliver: " + message);
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

    private String publishAndConsume(String message, String msName) {
        String topic = msToTopic.get(msName.toLowerCase());

        try {
            PubsubConfig.publish(message);
            return PubsubConfig.subscribe(topic);
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
