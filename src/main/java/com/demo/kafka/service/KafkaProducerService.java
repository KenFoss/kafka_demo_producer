package com.demo.kafka.service;
import com.demo.kafka.AvroProducer;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
//@EnableBinding(Processor.class)
public class KafkaProducerService {
    private final KafkaTemplate<String, AvroProducer> kafkaTemplate;
    private final String topicName = "producer-details"; // Your Kafka topic name

    public KafkaProducerService(KafkaTemplate<String, AvroProducer> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(AvroProducer avroProducer) {
        try {
            kafkaTemplate.send(topicName, avroProducer.getId().toString(), avroProducer);
            System.out.println("Message sent successfully");
        } catch (Exception e) {
            System.out.println("Error sending message: " + e.getMessage());
            throw new RuntimeException("Error sending message", e);
        }
    }
}
