package com.demo.kafka.service.listener;

import com.demo.kafka.AvroProducer;
import com.demo.kafka.domain.Producer;
import com.demo.kafka.service.KafkaProducerService;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DatabaseChangeListener {
    @Autowired
    private KafkaProducerService producerService;

    @PostPersist
    @PostUpdate
    @PostRemove
    public void afterAnyUpdate(Producer entity) {
        if (entity == null) {
            System.out.println("Received null entity in DatabaseChangeListener");
            return;
        }

        AvroProducer avroProducer = new AvroProducer();
        avroProducer.setId(entity.getId());
        avroProducer.setOwnerName(entity.getOwnerName());
        avroProducer.setProductName(entity.getProductName());
        avroProducer.setQuantity(entity.getQuantity());

        producerService.sendMessage(avroProducer);
    }
}
