package com.example.order.domain;

import java.util.List;

public class OrderFullDto {

    private List<String> ids;

    private Long customerId;

    public OrderFullDto(List<String> ids, Long customerId) {
        this.ids = ids;
        this.customerId = customerId;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
}
