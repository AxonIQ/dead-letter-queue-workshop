package io.axoniq.workshop.dlq.query;

import io.axoniq.workshop.dlq.api.ProductCreatedEvent;
import io.axoniq.workshop.dlq.api.ProductNameChangedEvent;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@ProcessingGroup("product_name")
@Transactional(propagation = Propagation.REQUIRES_NEW)
class ProductNameProjection {

    private final ProductNameRepository repository;

    public ProductNameProjection(ProductNameRepository repository) {
        this.repository = repository;
    }

    @EventHandler
    public void handle(ProductCreatedEvent event) {
        repository.save(new ProductNameEntity(event.productId(), event.productName()));
    }

    @EventHandler
    public void handle(ProductNameChangedEvent event) {
        ProductNameEntity product = repository.findById(event.productId()).orElseThrow();
        product.setName(event.productName());
        repository.save(product);
    }
}