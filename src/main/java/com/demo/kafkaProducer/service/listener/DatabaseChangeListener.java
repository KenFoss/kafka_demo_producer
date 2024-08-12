package com.demo.kafkaProducer.service.listener;

import com.demo.schema.record.AvroProducerSync;
import com.demo.kafkaProducer.domain.Producer;
import com.demo.kafkaProducer.service.KafkaProducerService;
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

        AvroProducerSync avroProducer = new AvroProducerSync();
        avroProducer.setId(entity.getId());
        avroProducer.setOwnerName(entity.getOwnerName());
        avroProducer.setProductName(entity.getProductName());
        avroProducer.setQuantity(entity.getQuantity());

        producerService.sendMessage(avroProducer);
    }
}
