# Dead-letter queue workshop

With Axon Framework 4.6.0, we introduce the Dead-letter Queue feature. 
This workshop will show you how it works and how you can use it in your application.

If you have any questions during the workshop, please ask one of the AxonIQ developers walking around the room! 
Or, if you are doing this workshop at a later date, [reach out on our Discuss](https://discuss.axoniq.io/).


## Introduction

This repository contains the `ProductNaming` Microservice of a big retailer. 
It's responsible for the naming of products, and only that. 

The `Product` aggregate contains the validation and then applies events. 
The events are handled by the `product_name` projection, 
which saves the current product name for each id to the database.


## Getting started

Make sure you have the following tools available:
- Java 11+ installed
- Maven 3+ installed
- JAVA IDE of choice

### Start Axon Server

We will use Axon Server during the workshop as an event storage engine.
You can [download a ZIP file with AxonServer as a standalone JAR](https://download.axoniq.io/axonserver/AxonServer.zip). 
This will also give you the AxonServer CLI and information on how to run and configure the server.

Alternatively, you can run the following command to start AxonServer in a Docker container:

```docker run -d --name axonserver-dlq -p 8024:8024 -p 8124:8124 axoniq/axonserver```

After starting AxonServer, you should be able to check the dashboard by visiting: [http://localhost:8024](http://localhost:8024)

## Observing the application

In this demo application you can create products using the `POST /product` endpoint. 
Requests for this are included in [the http file](./requests.http).

This will lead to the `ProductCreatedEvent`, handled in the `product_name` projection to update it. 

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

## Error propagation
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
>    - Check out [this section in the reference guide](https://docs.axoniq.io/reference-guide/axon-framework/events/event-processors#processing-group-listener-invocation-error-handler)
> how you can do this.
> 2. Restart the application and execute the "Bad Request" again. 
>    - What behavior do you see?
> 3. Execute the "Good request" again.
>    - Will it ever be processed?
>
> Move on to the next section when you can answer these questions.

---
## Stuck!
As you might have noticed, the event processor is stuck on the broken event. 
Newer events will not be processed.
The data in our projection is getting stale!

We can fix this using the Dead-letter Queue feature of Axon Framework 4.6. 
Or rather, we can park the event so newer events can get processed, until we fix the projection.

> **Task 3**
> 
> 1. Register a Dead-letter Queue in the `WorkshopConfiguration`
>    - You can find out more about this in [this section of the reference guide](https://docs.axoniq.io/reference-guide/axon-framework/events/event-processors#dead-letter-queue)
> 2. Restart the application
>    - What behavior do we see?
> 3. Publish another "Good request"
>    - Is it processed despite the earlier failing event?
---

## Retrying
It's nice that we have now parked the bad event, and good events can be processed. 
Our projection is not fully consistent, but it's better than if it had stalled completely.

We need to fix the bug (enlarging the column) and then retry the event. Let's focus on the retry mechanism first!

[This section of the reference guide](https://docs.axoniq.io/reference-guide/axon-framework/events/event-processors#processing-dead-letter-sequences)
outlines how messages can be retried. 

> **Task 4**
> 
> 1. Create an endpoint that retries a message
>    - Alternatively, you can make a Spring @Scheduled method to do so every few seconds
> 2. Call your endpoint
> 3. Observe what happens
---

## Retry policy
Sometimes we want to limit the number of times a message will be retried. 
In that case, we can [configure a policy](https://docs.axoniq.io/reference-guide/axon-framework/events/event-processors#processing-dead-letter-sequences). 
With this policy, we can add diagnostics to the Dead Letter. In these we can store the number of times a message has been retried. 

> **Task 5**
>
> 1. Create a policy that discards messages after 5 tries
> 2. Retry the message a few times
>    - Observe the message being evicted from the DLQ when the 5 tries are exceeded
>    - If you have multiple bad events, it will rotate between them, and you will need more retry calls. 
---

## Fixing the bug

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



