package io.axoniq.workshop.dlq.adapter.rest;

public class ProductNameCreateOrUpdateRequest {
    private final String id;
    private final String name;

    public ProductNameCreateOrUpdateRequest(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
