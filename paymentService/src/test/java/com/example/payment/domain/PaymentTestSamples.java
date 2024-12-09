package com.example.payment.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PaymentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Payment getPaymentSample1() {
        return new Payment().id(1L).orderId(1L).paymentStatus("paymentStatus1").paymentMethod("paymentMethod1");
    }

    public static Payment getPaymentSample2() {
        return new Payment().id(2L).orderId(2L).paymentStatus("paymentStatus2").paymentMethod("paymentMethod2");
    }

    public static Payment getPaymentRandomSampleGenerator() {
        return new Payment()
            .id(longCount.incrementAndGet())
            .orderId(longCount.incrementAndGet())
            .paymentStatus(UUID.randomUUID().toString())
            .paymentMethod(UUID.randomUUID().toString());
    }
}
