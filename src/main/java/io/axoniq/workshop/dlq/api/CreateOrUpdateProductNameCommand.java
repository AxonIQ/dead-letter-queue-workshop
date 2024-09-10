package io.axoniq.workshop.dlq.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record CreateOrUpdateProductNameCommand(@TargetAggregateIdentifier @JsonProperty String id,
                                               @JsonProperty String productName) {

}
