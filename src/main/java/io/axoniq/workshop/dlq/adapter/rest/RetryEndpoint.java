package io.axoniq.workshop.dlq.adapter.rest;

import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.messaging.deadletter.SequencedDeadLetterProcessor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RetryEndpoint {

    private final EventProcessingConfiguration configuration;


    public RetryEndpoint(EventProcessingConfiguration configuration) {
        this.configuration = configuration;
    }

    @PostMapping("retry")
    // Or: @Scheduled(fixedInterval = 5000)
    public void retry() {
        SequencedDeadLetterProcessor<EventMessage<?>> processor = configuration
                .sequencedDeadLetterProcessor("product_name").orElseThrow();
        processor.processAny();
    }
}
