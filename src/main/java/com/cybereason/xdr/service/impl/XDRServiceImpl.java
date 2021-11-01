package com.cybereason.xdr.service.impl;

import com.cybereason.xdr.config.KafkaConfig;
import com.cybereason.xdr.service.XDRService;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class XDRServiceImpl implements XDRService {
    private static final Logger LOGGER = LoggerFactory.getLogger(XDRServiceImpl.class);
    private static final String BASE_URL = "http://localhost:8081";
    private final OkHttpClient httpClient = new OkHttpClient();

    public static Map<String, String> msToTopic;

    static {
        msToTopic = new HashMap<>();
        msToTopic.put("transformer", "trans2idm");
        msToTopic.put("identity", "idm2threat");
        msToTopic.put("threat", "threat2router");
    }

    @Override
    public String process(String message, String msName) {
        LOGGER.info("Received a message to deliver: ", message);
        if (produceToTopic(message)) {
            return subscribeToTopic(msName);
        }
        return "ERROR";
    }

    private boolean produceToTopic(String message) {
        KafkaConfig.runProducer(message);
        LOGGER.info("Produced the following message: " + message);
        return true;
    }

    private String subscribeToTopic(String msName) {
        String topic = msToTopic.get(msName.toLowerCase());
        LOGGER.info("*** Kafka Consumer for topic: " + topic + " is up and running ***");
        return KafkaConfig.runConsumer(topic);
    }


//    private String OLDProduceToTopic(String message) {
//        Response response = null;
//
//        RequestBody body = RequestBody.create(
//                MediaType.parse("application/json"), message);
//
//        Request request = new Request.Builder()
//                .url(BASE_URL + "/publish")
//                .post(body)
//                .build();
//
//        Call call = httpClient.newCall(request);
//        try {
//            response = call.execute();
//        } catch (IOException e) {
//            LOGGER.error("Failed to execute the request: ", e.getMessage(), e);
//        }
//        if (response == null)
//            return "OK";
//        else {
//            try {
//                return response.body().string();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return "OK";
//        }
//    }
}
