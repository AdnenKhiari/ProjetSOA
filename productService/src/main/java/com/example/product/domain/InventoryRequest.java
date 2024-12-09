package com.example.product.domain;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public class InventoryRequest {
    private String productId;
    @NotNull
    private Integer quantity;
    @NotNull
    private Instant updatedAt;

    // Getters and setters
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
