package com.cybereason.xdr.config;

import com.cybereason.models.Event;
import com.cybereason.xdr.constants.PubSubConstants;
import com.cybereason.xdr.model.IdentityEnrichmentMessage;
import com.cybereason.xdr.model.SingleMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Most of the code copied and pasted 😂
 * With ♥ by Mike Elkabetz
 * Date: 05/12/2021
 */
public class PubsubConfig {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(PubsubConfig.class);
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final String PUBLISH_TOPIC = "research-chronicle-topic";
    private static final String BASE_URL = "https://master-dynamic-mapper.eng.cybereason.net/api/v1/transform/single-message";

    public static void publish(String message)
            throws IOException, ExecutionException, InterruptedException {

        Publisher publisher = null;
        TopicName topicName = TopicName.of(PubSubConstants.projectID, PUBLISH_TOPIC);
        try {
            publisher = Publisher.newBuilder(topicName).build();
            ByteString data = ByteString.copyFromUtf8(message);
            PubsubMessage pubsubMessage = PubsubMessage.newBuilder().putAttributes("format", "json").setData(data).build();
            LOGGER.info("Publish to topic: " + PUBLISH_TOPIC);
            publisher.publish(pubsubMessage);
        } finally {
            if (publisher != null) {
                publisher.shutdown();
                publisher.awaitTermination(1, TimeUnit.MINUTES);
            }
        }
    }

    public static String subscribe(String msName, String topic) throws ExecutionException, InterruptedException, TimeoutException, JsonProcessingException {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        ProjectSubscriptionName subscriptionName =
                ProjectSubscriptionName.of(PubSubConstants.projectID, topic);

        MessageReceiver receiver =
                (PubsubMessage message, AckReplyConsumer consumer) -> {
                    consumer.ack();
                    if (msName.equalsIgnoreCase("Transparency")) {
                        IdentityEnrichmentMessage identityEnrichmentMessage;
                        try {
                            identityEnrichmentMessage = objectMapper.readValue(message.getData().toStringUtf8(), IdentityEnrichmentMessage.class);
                            if (identityEnrichmentMessage != null) {
                                completableFuture.complete(getTransparencyOutput(identityEnrichmentMessage));
                            } else {
                                completableFuture.complete(message.getData().toStringUtf8());
                            }
                        } catch (JsonProcessingException e) {
                            LOGGER.info("Failed to deserialize message");
                        }
                    }
                };


        Subscriber subscriber = Subscriber.newBuilder(subscriptionName, receiver).build();

        subscriber.startAsync();

        LOGGER.info("Subscribe to topic: " + subscriptionName);

        return completableFuture.get();
    }

    private static String getTransparencyOutput(IdentityEnrichmentMessage identityEnrichmentMessage) throws JsonProcessingException {
        Event event = (Event) identityEnrichmentMessage.getCybereasonDataObjects().iterator().next();
        if (event != null) {
            SingleMessage singleMessage = SingleMessage.builder().dataSource("xdr").schemaId("base").elementType("Event").jsonNode(objectMapper.valueToTree(event)).build();
            return executeRequest(singleMessage);
        }
        return null;
    }

    private static String executeRequest(SingleMessage singleMessage) throws JsonProcessingException {
        RequestBody body = RequestBody.Companion.create(objectMapper.writeValueAsString(singleMessage), JSON);
        final OkHttpClient httpClient = new OkHttpClient();
        Response response = null;

        Request request = new Request.Builder()
                .url(BASE_URL)
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
                LOGGER.error("Failed to execute the request: ", e.getMessage(), e);
            }
            return "OK";
        }
    }
}

