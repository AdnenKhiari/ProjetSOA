package com.example.product.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class ProductTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Product getProductSample1() {
        return new Product().id("id1").name("name1").description("description1");
    }

    public static Product getProductSample2() {
        return new Product().id("id2").name("name2").description("description2");
    }

    public static Product getProductRandomSampleGenerator() {
        return new Product()
            .id(UUID.randomUUID().toString())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .stock(intCount.incrementAndGet());
    }
}
