package com.cybereason.xdr.constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PubSubConstants {
    private static final Logger LOGGER = LoggerFactory.getLogger(PubSubConstants.class);

    public static String projectID = System.getenv("pubsub.project.id");
    public static String serverFQDN = System.getenv("pubsub.address");
    public static String publishTopicID = System.getenv("pubsub.topic.publish");
    public static String subscribeTopicID = System.getenv("pubsub.topic.subscribe");

    static {
        if (projectID == null)
            LOGGER.error("Environment Variable is missing: pubsub.project.id");
        if (serverFQDN == null)
            LOGGER.error("Environment Variable is missing: pubsub.address");
        if (publishTopicID == null)
            LOGGER.error("Environment Variable is missing: pubsub.topic.publish");
        if (subscribeTopicID == null)
            LOGGER.error("Environment Variable is missing: pubsub.topic.subscribe");
    }
}
