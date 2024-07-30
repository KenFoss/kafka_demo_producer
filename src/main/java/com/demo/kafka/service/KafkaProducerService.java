package com.demo.kafka.service;
import com.demo.kafka.domain.Producer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.net.ConnectException;

@Service
public class KafkaProducerService  {
    private final KafkaTemplate<String, Producer> kafkaTemplate;
    public String producerTopic = "data-producer-00";

    public KafkaProducerService(KafkaTemplate<String, Producer> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(Producer entity) {
//        try{
        kafkaTemplate.send(producerTopic, entity);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            throw new RuntimeException("Error sending entity");
//        }
    }
}
