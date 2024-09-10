package io.axoniq.workshop.dlq.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProductCreatedEvent(@JsonProperty String productId,
                                  @JsonProperty String productName) {

}