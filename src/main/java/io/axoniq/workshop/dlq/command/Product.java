package io.axoniq.workshop.dlq.command;

import io.axoniq.workshop.dlq.api.CreateOrUpdateProductNameCommand;
import io.axoniq.workshop.dlq.api.ProductCreatedEvent;
import io.axoniq.workshop.dlq.api.ProductNameChangedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateCreationPolicy;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.modelling.command.CreationPolicy;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
class Product {

    @AggregateIdentifier
    private String productId;
    private String productName;

    public Product() {
        // For Axon
    }

    @CommandHandler
    @CreationPolicy(AggregateCreationPolicy.CREATE_IF_MISSING)
    public void on(CreateOrUpdateProductNameCommand command) {
        if (productId == null) {
            AggregateLifecycle.apply(new ProductCreatedEvent(command.id(), command.productName()));
            return;
        }
        if (!command.productName().equals(productName)) {
            AggregateLifecycle.apply(new ProductNameChangedEvent(command.id(), command.productName()));
        }
    }

    @EventSourcingHandler
    public void on(ProductCreatedEvent event) {
        this.productId = event.productId();
        this.productName = event.productName();
    }

    @EventSourcingHandler
    public void on(ProductNameChangedEvent event) {
        this.productName = event.productName();
    }
}