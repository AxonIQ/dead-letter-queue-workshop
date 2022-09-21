package io.axoniq.workshop.dlq.command;

import io.axoniq.workshop.dlq.api.CreateOrUpdateProductNameCommand;
import io.axoniq.workshop.dlq.api.ProductCreatedEvent;
import io.axoniq.workshop.dlq.api.ProductNameChangedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.common.IdentifierFactory;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateCreationPolicy;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.modelling.command.CreationPolicy;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
public class Product {

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
            AggregateLifecycle.apply(new ProductCreatedEvent(command.getId(), command.getProductName()));
            return;
        }
        if (!command.getProductName().equals(productName)) {
            AggregateLifecycle.apply(new ProductNameChangedEvent(command.getId(), command.getProductName()));
        }
    }

    @EventSourcingHandler
    public void on(ProductCreatedEvent event) {
        this.productId = event.getProductId();
        this.productName = event.getProductName();
    }

    @EventSourcingHandler
    public void on(ProductNameChangedEvent event) {
        this.productName = event.getProductName();
    }
}
