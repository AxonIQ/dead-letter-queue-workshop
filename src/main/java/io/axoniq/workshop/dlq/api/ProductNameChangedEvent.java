package io.axoniq.workshop.dlq.api;

public record ProductNameChangedEvent(String productId, String productName) {

}
