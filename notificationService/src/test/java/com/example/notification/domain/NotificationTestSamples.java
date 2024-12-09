package com.example.notification.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class NotificationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Notification getNotificationSample1() {
        return new Notification().id("id1").orderId(1L).customerId(1L).type("type1").status("status1");
    }

    public static Notification getNotificationSample2() {
        return new Notification().id("id2").orderId(2L).customerId(2L).type("type2").status("status2");
    }

    public static Notification getNotificationRandomSampleGenerator() {
        return new Notification()
            .id(UUID.randomUUID().toString())
            .orderId(longCount.incrementAndGet())
            .customerId(longCount.incrementAndGet())
            .type(UUID.randomUUID().toString())
            .status(UUID.randomUUID().toString());
    }
}
