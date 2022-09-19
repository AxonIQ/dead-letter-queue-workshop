package io.axoniq.workshop.dlq.api;

public class ProductCreatedEvent {
    private final String productId;
    private final String productName;

    public ProductCreatedEvent(String productId, String productName) {
        this.productId = productId;
        this.productName = productName;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }
}
