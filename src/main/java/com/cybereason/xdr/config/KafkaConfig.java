package com.cybereason.xdr.config;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Properties;

public class KafkaConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConfig.class);
    private static final Object BOOTSTRAP_SERVERS = "localhost:9092";
    private static final Properties props = new Properties();

    static {
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG,
                "KafkaGenericConsumer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                LongDeserializer.class.getName());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
    }

    private static Consumer<Long, String> createGenericConsumer() {
        // Create the consumer using props.
        final Consumer<Long, String> consumer =
                new KafkaConsumer<>(props);
        return consumer;
    }

    public static String runConsumer(String topic) {
        final Consumer<Long, String> consumer = createGenericConsumer();

        // Subscribe to the topic.
        consumer.subscribe(Collections.singletonList(topic));

        final int giveUp = 10;
        int noRecordsCount = 0;

        while (true) {
            //polling the records since the last offset.
            final ConsumerRecords<Long, String> consumerRecords =
                    consumer.poll(1000);
            consumer.commitAsync();

            if (consumerRecords.count() == 0) {
                noRecordsCount++;
                if (noRecordsCount > giveUp) break;
                else continue;
            } else {
                ConsumerRecord<Long, String> lastRecord = null;
                for (ConsumerRecord<Long, String> consumerRecord : consumerRecords) {
                    lastRecord = consumerRecord;
                }
                consumer.close();
                LOGGER.info("Kafka Message Arrived! The message is: " + lastRecord.value());
                return lastRecord.value();
            }
            //Printing the messages received in a for loop
//            consumerRecords.forEach(record -> {
//                LOGGER.info("Kafka Message Arrived! The message is: " + record.value());
//            });
            //committing the offset of messages in Async mode
        }
        consumer.close();
        LOGGER.info("My work ended :), no more subscribing to: " + topic);
        return "Didnt get any message for some reason";
    }

    public static void runProducer(String message) {
        //create the producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        //create a producer record
        ProducerRecord<String, String> record = new ProducerRecord<>("trans2idm", message);

        producer.send(record);
        producer.flush();
        producer.close();
    }
}
