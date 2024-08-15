package com.demo.kafkaProducer.service;
import com.demo.schema.record.AvroProducerSync;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    private final KafkaTemplate<String, AvroProducerSync> kafkaTemplate;
    private final String topicName = "producer-details";

    public KafkaProducerService(KafkaTemplate<String, AvroProducerSync> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(AvroProducerSync avroProducer) {
        try {
            kafkaTemplate.send(topicName, String.valueOf(avroProducer.getId()), avroProducer);
            System.out.println("Message sent successfully");
        } catch (Exception e) {
            System.out.println("Error sending message: " + e.getMessage());
            throw new RuntimeException("Error sending message", e);
        }
    }
}
