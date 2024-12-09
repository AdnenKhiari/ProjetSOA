package com.example.order.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public class PaymentDTO implements Serializable {

    private BigDecimal totalAmount;
    private Long orderId;

    public PaymentDTO(BigDecimal totalAmount, Long orderId) {
        this.totalAmount = totalAmount;
        this.orderId = orderId;
    }

    public PaymentDTO() {
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

    @Override
    public String toString() {
        return "PaymentDTO{" +
            "totalAmount=" + totalAmount +
            ", orderId=" + orderId +
            '}';
    }
}
