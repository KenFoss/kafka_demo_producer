package com.demo.kafkaProducer.service.mapper;

import static com.demo.kafkaProducer.domain.ProducerAsserts.*;
import static com.demo.kafkaProducer.domain.ProducerTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AvroProducerMapperTest {

    private ProducerMapper producerMapper;

    @BeforeEach
    void setUp() {
        producerMapper = new ProducerMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getProducerSample1();
        var actual = producerMapper.toEntity(producerMapper.toDto(expected));
        assertProducerAllPropertiesEquals(expected, actual);
    }
}
