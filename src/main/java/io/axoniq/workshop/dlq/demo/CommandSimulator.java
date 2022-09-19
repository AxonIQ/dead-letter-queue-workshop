//package io.axoniq.workshop.dlq.demo;
//
//import io.axoniq.workshop.dlq.api.CreateProductCommand;
//import io.axoniq.workshop.dlq.api.UpdateProductNameCommand;
//import org.axonframework.commandhandling.gateway.CommandGateway;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Random;
//
///**
// * Simulates commands being sent to the system. Creates a random product every second, and updates a random product
// * every second.
// */
//@Service
//public class CommandSimulator {
//
//    private final CommandGateway commandGateway;
//    private final Map<String, String> productMap = new HashMap<>();
//    private final Random random = new Random();
//
//    public CommandSimulator(CommandGateway commandGateway) {
//        this.commandGateway = commandGateway;
//    }
//
//    @Scheduled(fixedDelay = 1000)
//    public void run() {
//        this.createRandomProduct();
//        this.updateRandomProduct();
//    }
//
//    private void createRandomProduct() {
//        String name = DemoConstants.generateName();
//        String id = commandGateway.sendAndWait(new CreateProductCommand(name));
//        productMap.put(id, name);
//    }
//
//    private void updateRandomProduct() {
//        String id = productMap.keySet().stream().skip(random.nextInt(productMap.keySet().size() - 1)).findFirst()
//                              .orElseThrow();
//        String name = DemoConstants.generateName();
//
//        commandGateway.sendAndWait(new UpdateProductNameCommand(id, name));
//        productMap.put(id, name);
//    }
//}
