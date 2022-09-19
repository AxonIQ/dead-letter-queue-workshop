package io.axoniq.workshop.dlq.command;

import io.axoniq.workshop.dlq.api.CreateProductCommand;
import io.axoniq.workshop.dlq.api.ProductCreatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.common.IdentifierFactory;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
public class Product {

    @AggregateIdentifier
    private String productId;
    private String productName;

    protected Product() {
        // For Axon
    }

    @CommandHandler
    public Product(CreateProductCommand command) {
        String id = IdentifierFactory.getInstance().generateIdentifier();
        AggregateLifecycle.apply(new ProductCreatedEvent(id, command.getProductName()));
    }

    @EventSourcingHandler
    public void on(ProductCreatedEvent event) {
        this.productId = event.getProductId();
        this.productName = event.getProductName();
    }
}
