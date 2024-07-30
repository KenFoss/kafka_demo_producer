package com.demo.kafka.service.mapper;

import static com.demo.kafka.domain.ProducerAsserts.*;
import static com.demo.kafka.domain.ProducerTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProducerMapperTest {

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
