package io.axoniq.workshop.dlq.adapter.rest;

import io.axoniq.workshop.dlq.api.CreateOrUpdateProductNameCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductNameEndpoint {
    private final CommandGateway commandGateway;

    public ProductNameEndpoint(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @PostMapping("product")
    public void createProduct(@RequestBody ProductNameCreateOrUpdateRequest request) {
        commandGateway.sendAndWait(new CreateOrUpdateProductNameCommand(request.getId(), request.getName()));
    }
}
