package io.axoniq.workshop.dlq.config;

import org.axonframework.config.EventProcessingConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkshopConfiguration {

    @Autowired
    public void configureEventProcessing(EventProcessingConfigurer configurer) {
        // TODO: Configure event processing here
    }
}