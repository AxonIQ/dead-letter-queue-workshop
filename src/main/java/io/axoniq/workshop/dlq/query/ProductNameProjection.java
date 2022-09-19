package io.axoniq.workshop.dlq.query;

import io.axoniq.workshop.dlq.api.ProductCreatedEvent;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@ProcessingGroup("product_name")
public class ProductNameProjection {

    private final ProductNameRepository repository;

    public ProductNameProjection(ProductNameRepository repository) {
        this.repository = repository;
    }

    @EventHandler
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(ProductCreatedEvent event) {
        repository.save(new ProductNameEntity(event.getProductId(), event.getProductName()));
    }
}
