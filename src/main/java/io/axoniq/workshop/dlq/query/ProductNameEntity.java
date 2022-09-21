package io.axoniq.workshop.dlq.query;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ProductNameEntity {
    @Id
    private String id;
    @Column(length = 256)
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
