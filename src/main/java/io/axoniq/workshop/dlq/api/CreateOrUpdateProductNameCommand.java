package io.axoniq.workshop.dlq.api;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class CreateOrUpdateProductNameCommand {
    @TargetAggregateIdentifier
    private final String id;
    private final String productName;

    public CreateOrUpdateProductNameCommand(String id, String productName) {
        this.id = id;
        this.productName = productName;
    }

    public String getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }
}
