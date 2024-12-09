package com.example.payment.domain;

import java.math.BigDecimal;

public class PaymentDTOKafka {

    private BigDecimal totalAmount;
    private Long orderId;

    public PaymentDTOKafka(BigDecimal totalAmount, Long orderId) {
        this.totalAmount = totalAmount;
        this.orderId = orderId;
    }

    public PaymentDTOKafka() {
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
