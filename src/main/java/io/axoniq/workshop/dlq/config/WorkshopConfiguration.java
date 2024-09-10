package io.axoniq.workshop.dlq.config;

import org.axonframework.common.jpa.EntityManagerProvider;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.deadletter.jpa.JpaSequencedDeadLetterQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkshopConfiguration {

    @Autowired
    public void configureEventProcessing(EventProcessingConfigurer configurer) {
        configurer.registerDeadLetterQueue(
                "product_name",
                config -> JpaSequencedDeadLetterQueue.builder()
                                                     .processingGroup("product_name")
                                                     .maxSequences(256)
                                                     .maxSequenceSize(256)
                                                     .entityManagerProvider(config.getComponent(
                                                             EntityManagerProvider.class
                                                     ))
                                                     .transactionManager(config.getComponent(
                                                             TransactionManager.class
                                                     ))
                                                     .serializer(config.serializer())
                                                     .build()
        );

        configurer.registerDeadLetterPolicy("product_name", configuration -> (letter, cause) -> {
            Integer retries = (Integer) letter.diagnostics().getOrDefault("retries", 0);
            if(retries < 5) {
                return Decisions.requeue(cause, l -> l.diagnostics().and("retries", retries + 1));
            }
            return Decisions.doNotEnqueue();
        });
    }
}
