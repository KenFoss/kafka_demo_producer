package com.demo.kafkaProducer.domain;

import static com.demo.kafkaProducer.domain.ProducerTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.demo.kafkaProducer.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AvroProducerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Producer.class);
        Producer producer1 = getProducerSample1();
        Producer producer2 = new Producer();
        assertThat(producer1).isNotEqualTo(producer2);

        producer2.setId(producer1.getId());
        assertThat(producer1).isEqualTo(producer2);

        producer2 = getProducerSample2();
        assertThat(producer1).isNotEqualTo(producer2);
    }
}
