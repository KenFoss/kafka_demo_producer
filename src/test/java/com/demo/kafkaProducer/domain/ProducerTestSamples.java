package com.demo.kafkaProducer.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ProducerTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Producer getProducerSample1() {
        return new Producer().id(1L).ownerName("ownerName1").productName("productName1").quantity(1);
    }

    public static Producer getProducerSample2() {
        return new Producer().id(2L).ownerName("ownerName2").productName("productName2").quantity(2);
    }

    public static Producer getProducerRandomSampleGenerator() {
        return new Producer()
            .id(longCount.incrementAndGet())
            .ownerName(UUID.randomUUID().toString())
            .productName(UUID.randomUUID().toString())
            .quantity(intCount.incrementAndGet());
    }
}
