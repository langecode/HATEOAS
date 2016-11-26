# Service Relations
Service relation means in this context more specifically - a relation between _concepts_ in a domain, 
inside a micro-service, inside a bounded context or across bounded contexts.
In the examples used here a Service named A and another Service named B is used.
The services may in some cases be said to be close, that is within the same bounded context, or a little further apart.
Services must not not use sensitve identifiers and internal implementation specific id's, 
thus the best option is that A and B uses human readable non-sensitive semantic identifiers as mentioned elsewhere.

## Account - Transaction example
In this initial example Service A has a relation in the service model to B instances which is under the jurisdiction of A. 
That means microservice A and B are within the same bounded context and if that is so the API could look something like this:

    ```
	https://<domain>/A’s/{anAId}/B’s
and
		https://<domain>/B’s/{aBId}

The example for that could be:
    
		https://service.domain.dk/accounts/123456789/terms
	which points to the standard-terms at
		https://service.domain.dk/terms/standard-terms

or if B is aggregated by A in a semantical perception of the domain:
		https://service.domain.dk/accounts/123456789/transactions
	which points to the transactions placed under the individual account itself:
		https://service.domain.dk/accounts/123456789/transactions/999
	```

In this example Service A knows about instances of B.

## Separate Microservices
In the event where A and B are separate micro-services residing under the same bounded context A and B are allowed to have knowledge about each other. 
In the [scenario](link to HATEOAS wiki), it is depicted that requests to A from clients can be decorated with an API capability concerning composition, 
that is “if you can - dear A - please include B’s in the sparse projection”. 
This signals to the A microservice that this client would like to have A and its B’s in one response. 

_Client perspectives matter_
If a majority of clients have this desire to see A together with the related B’s that is a sign of the expectations from client side and 
thus the microservice A can react to that and stay relevant for its consumers, thus be easy to use from a client perspective and 
thus continue to provide a good developer experience at development time, but add to that a better client developer experience at runtime.

# Wiki
The wiki is used to propose and discuss the examples of how to use HATEOAS in the form of HAL and to examplify the use of the API capabilities together with HAL

