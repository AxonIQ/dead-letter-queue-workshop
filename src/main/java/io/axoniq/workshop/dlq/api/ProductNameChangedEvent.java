package io.axoniq.workshop.dlq.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProductNameChangedEvent(@JsonProperty String productId,
                                      @JsonProperty String productName) {

}