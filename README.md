# Dead-Letter Queue Workshop

In Axon Framework [release 4.6.0](https://github.com/AxonFramework/AxonFramework/releases/tag/axon-4.6.0), we introduce the Dead-Letter Queue feature for [Event Processors](https://library.axoniq.io/axon_framework_ref/events/event-processors/README.html). 
This workshop will show you how it works and how you can use it in your application.

If you have any questions during the workshop, please ask one of the AxonIQ developers walking around the room! 
Or, if you are doing this workshop at a later date, [reach out on our Discuss](https://discuss.axoniq.io/).

![https://www.axoniq.io/axoniq-conference-2024](.assets/con24.png)

## Introduction

This repository contains the product naming service of a big retailer. 
It's responsible for the naming of products, and only that. 

The `Product` aggregate contains the validation and then applies events. 
The events are handled by the `product_name` projection, which saves the current product name for each id to the database.
The database used by the product naming service is `h2` storing the projections in a file.
Hence, if you want to get rid of them, you can simply remove the database file that is automatically created in this project on start-up.

## Solutions

You can access the solution for each step on a git branch, called `solution/step_{step}`.

## Getting started

Make sure you have the following tools available:
- Java 21+ installed
- Maven 3+ installed
- Java IDE of choice

### Start Axon Server

We will use Axon Server during the workshop as an event storage engine.
You can [download a ZIP file with AxonServer as a standalone JAR](https://download.axoniq.io/axonserver/AxonServer.zip). 
This will also give you the AxonServer CLI and information on how to run and configure the server.

Alternatively, you can run the following command to start AxonServer in a Docker container:

```docker run -d --name axonserver-dlq -p 8024:8024 -p 8124:8124 axoniq/axonserver```

After starting AxonServer, you should be able to check the dashboard by visiting: [http://localhost:8024](http://localhost:8024)

## 1 - Observing the Application

In this demo application you can create products using the `POST /product` endpoint. 
Requests for this are included in [the http file](./requests.http).

This will lead to the `ProductCreatedEvent`, handled in the `product_name` projection to save it. 
When the product already exists and the name is changed, it will apply an `ProductNameChangedEvent`.
The projection will then update the name. 

> **Task 1**
> 
> 1. Start `DlqWorkshopApplication` application
> 2. Execute the "Good Request"
>    - Verify nothing odd happens in the logs
> 3. Execute the "Bad Request"
>    - What happens? 
>    - Why does the error occur?
>
> Move on to the next section when you can answer these questions.

---

## 2 - Error Propagation

As you have observed, our column definition is set to a too low value to save the name of the second product!
The aggregate did not validate this, as a product should be allowed to have a long name. 
It's an error of the projection.

You have probably noticed that the event processor simply logs the error, then... does nothing!
When we execute the "Good Request" again, it will process the new event from that.
Thus, it skipped the "broken" event fully and the data in our database is inconsistent.

By default, the framework configures a `LoggingErrorHandler` as `ListenerInvocationErrorHandler` on event processors, 
which only catches the event and logs it. 
If you want to guarantee that your data is correct, 
you should configure a `ListenerInvocationErrorHandler` that throws the error, such as the `PropagatingErrorHandler`.

> **Task 2**
> 
> 1. Use the `WorkshopConfiguration` class to configure a `PropagatingErrorHandler`. 
>    - Check out [this section in the library](https://library.axoniq.io/axon_framework_ref/events/event-processors/README.html#_processing_group_listener_invocation_error_handler)
> how you can do this.
> 2. Restart the application and execute the "Bad Request" again. 
>    - What behavior do you see?
> 3. Execute the "Good request" again.
>    - Will it ever be processed?
>
> Move on to the next section when you can answer these questions.

---
## 3 - Stuck!

As you might have noticed, the event processor is stuck on the broken event. 
Newer events will not be processed.
The data in our projection is getting stale!

We can fix this using the Dead-letter Queue feature of Axon Framework 4.6. 
Or rather, we can park the event so newer events can get processed, until we fix the projection.

> **Task 3**
> 
> 1. Register a Dead-letter Queue in the `WorkshopConfiguration`
>    - You can find out more about this in [this section of the library](https://library.axoniq.io/axon_framework_ref/events/event-processors/README.html#dead-letter-queue)
> 2. Restart the application and publish a "Bad request"
>    - What behavior do we see?
> 3. Publish another "Good request"
>    - What happens to the second good event?
> 4. Publish the "Good request second product"
>    - What happens to this good event for another aggregate?

As you can see, once an event fails for an aggregate in a projection, events that follow it are moved to the DLQ
as well. More often than not, projections depend on earlier events. For example, the `ProductCreatedEvent` ceates the 
entity, and the `ProductNameChangedEvent` looks for it and updates the name.

If the `ProductCreatedEvent` was moved to the DLQ, and the `ProductNameChangedEvent` was not, the event would fail
because they depend on each other. This is why it is a "Sequenced Dead Letter Queue". Each event belongs to a sequence.
Is an event's sequence already present in the DLQ at the start of processing, it's moved to the DLQ automatically.

By default, the sequencing policy is the aggregate identifier. You can modify this on the `EventProcessingConfigurer`,
if you want to. 

---

## 4 - Retrying

It's nice that we have now parked multiple bad events, and good event sequences can be processed. 
Our projection is not fully consistent, but it's better than if it had stalled completely.

We need to fix the bug (enlarging the column) and then retry the event sequence. 
Let's focus on the retry mechanism first!

[This section of the library](https://library.axoniq.io/axon_framework_ref/events/event-processors/README.html#_processing_dead_letter_sequences)
outlines how messages can be retried. 
An important fact to note is that we retry a sequence of events, not a single event. 
If there are 10 events in the DLQ for the same aggregate identifier, 
for example, it will try to process all 10 events, one by one.

> **Task 4**
> 
> 1. Create an endpoint that retries a message
>    - Alternatively, you can make a Spring `@Scheduled` method to do so every few seconds
> 2. Call your endpoint
> 3. Observe what happens
---

## 5 - Retry Policy

Sometimes we want to limit the number of times a message will be retried. 
In that case, 
we can [configure a policy](https://library.axoniq.io/axon_framework_ref/events/event-processors/README.html#_dead_letter_enqueue_policy). 
With this policy, we can add diagnostics to the Dead Letter. 
In these we can store the number of times a message has been retried. 

> **Task 5**
>
> 1. Create a policy that discards messages after 5 tries
> 2. Retry the message a few times
>    - Observe the message being evicted from the DLQ when the 5 tries are exceeded
>    - If you have multiple bad events, it will rotate between them, and you will need more retry calls. 
---

## 6 - Fixing the Bug

Last but not least, let's fix the bug!

> **Task 6**
>
> 1. Execute a "Bad Request", so we have a bad event in the DLQ.
> 2. Now, update the column size to 50 in `ProductNameEntity`. 
> 3. Restart the application
> 4. Call you retry endpoint
>    - Observe the message being processed.

---

## Conclusion

Using the Dead-Letter Queue of Axon Framework will help you in the management of your event processor failures.
By parking bad messages, you can prevent your event processors from getting stuck. 

We hope this demonstrated the DLQ for you! If you have any questions, let us know.