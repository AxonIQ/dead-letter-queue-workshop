package io.axoniq.workshop.dlq.api;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record CreateOrUpdateProductNameCommand(@TargetAggregateIdentifier String id, String productName) {

}
