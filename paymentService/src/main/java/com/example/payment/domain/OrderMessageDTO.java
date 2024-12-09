package com.example.payment.domain;

import java.math.BigDecimal;
import java.util.List;

public class OrderMessageDTO {
    private BigDecimal totalAmount;
    private Long orderId;
    private Long customerId;
    private List<String> productIds;

    private Long paymentId;

    public OrderMessageDTO() {
    }

    public OrderMessageDTO(BigDecimal totalAmount, Long orderId, Long customerId, List<String> productIds,
                           Long paymentId) {
        this.totalAmount = totalAmount;
        this.orderId = orderId;
        this.customerId = customerId;
        this.productIds = productIds;
        this.paymentId = paymentId;
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

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public List<String> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<String> productIds) {
        this.productIds = productIds;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    @Override
    public String toString() {
        return "OrderMessageDTO{" +
            "totalAmount=" + totalAmount +
            ", orderId=" + orderId +
            ", customerId=" + customerId +
            ", productIds=" + productIds +
            ", paymentId='" + paymentId + '\'' +
            '}';
    }
}

