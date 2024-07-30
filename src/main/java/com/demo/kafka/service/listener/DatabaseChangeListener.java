package com.demo.kafka.service.listener;

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
        producerService.sendMessage(entity);
    }
}
