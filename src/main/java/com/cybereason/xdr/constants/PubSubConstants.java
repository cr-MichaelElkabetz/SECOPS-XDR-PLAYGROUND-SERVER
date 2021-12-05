package com.cybereason.xdr.constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PubSubConstants {
    private static final Logger LOGGER = LoggerFactory.getLogger(PubSubConstants.class);

    public static String projectID = System.getenv("pubsub.project.id");
    public static String transformerTopicSub = System.getenv("pubsub.topic.sub.transformer");
    public static String idmTopicSub = System.getenv("pubsub.topic.sub.idm");
    public static String threatTopicSub = System.getenv("pubsub.topic.sub.threat");
    public static String routerTopicSub = System.getenv("pubsub.topic.sub.router");

    static {
        if (projectID == null)
            LOGGER.error("Environment Variable is missing: pubsub.project.id");
        if (idmTopicSub == null)
            LOGGER.error("Environment Variable is missing: pubsub.topic.sub.idm");
        if (threatTopicSub == null)
            LOGGER.error("Environment Variable is missing: pubsub.topic.sub.threat");
        if (routerTopicSub == null)
            LOGGER.error("Environment Variable is missing: pubsub.topic.sub.router");
    }
}
