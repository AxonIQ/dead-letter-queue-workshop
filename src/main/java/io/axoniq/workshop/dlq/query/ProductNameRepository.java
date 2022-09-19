package io.axoniq.workshop.dlq.query;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductNameRepository extends JpaRepository<ProductNameEntity, String> {

}
