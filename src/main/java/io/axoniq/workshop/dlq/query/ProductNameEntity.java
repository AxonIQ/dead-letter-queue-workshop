package io.axoniq.workshop.dlq.query;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
class ProductNameEntity {

    @Id
    private String id;
    @Column(length = 20)
    private String name;

    public ProductNameEntity(String id, String name) {
        this.id = id;
        this.name = name;
    }

    protected ProductNameEntity() {
        // For JPA
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}