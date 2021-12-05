package com.cybereason.xdr.config;

import com.cybereason.xdr.constants.PubSubConstants;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(PubsubConfig.class);
    private static final String PUBLISH_TOPIC = "research-chronicle-topic";

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

    public static String subscribe(String topic) throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        ProjectSubscriptionName subscriptionName =
                ProjectSubscriptionName.of(PubSubConstants.projectID, topic);

        MessageReceiver receiver =
                (PubsubMessage message, AckReplyConsumer consumer) -> {
                    consumer.ack();
                    completableFuture.complete(message.getData().toStringUtf8());
                };


        Subscriber subscriber = Subscriber.newBuilder(subscriptionName, receiver).build();

        subscriber.startAsync();

        LOGGER.info("Subscribe to topic: " + subscriptionName);

        return completableFuture.get();
    }
}

