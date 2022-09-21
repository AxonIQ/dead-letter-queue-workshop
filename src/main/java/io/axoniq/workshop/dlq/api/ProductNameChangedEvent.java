package io.axoniq.workshop.dlq.api;

public class ProductNameChangedEvent {
    private final String productId;
    private final String productName;

    public ProductNameChangedEvent(String productId, String productName) {
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
