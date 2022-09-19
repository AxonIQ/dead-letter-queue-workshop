package io.axoniq.workshop.dlq.api;

public class CreateProductCommand {
    private final String productName;

    public CreateProductCommand(String productName) {
        this.productName = productName;
    }

    public String getProductName() {
        return productName;
    }
}
