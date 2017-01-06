### Limits

The server-side service might be busy for one or the other reason and need to tell the client that. In fact it is trying to say "Be patient - I am rather busy right now". Therefore the server returns a `503` error code (see response codes below) with a *Retry-After* header stating the time
when it is expected that the server is no longer busy and can serve a consumer again.

If the server-side intercepts the consumer it may choose to return a 429 Too many Requests with a response stating that "You are limited to XXXX requests per hour per `access_token` or `client_id` in total per  per `timeunit` overall. 

## Responses

The responses from calling resources in the API adheres to the specification of HTTP 1.1 and thus the status code, the headers used are found in that specification.

### Status Codes

#### Information on status code and headers are found under:

      Status codes:
      <https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html>
      
      Headers:
      <https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html>


A couple of response codes will be described here as inspiration, but the most important thing is to work with the protocol and thus to read and understand what the status codes means and how they fit into the current situation in the API and write explicitly what the client can expect in every situation in order to make the API as developer friendly as possible. 

#### 200 OK

The 200 OK response signal everything went ok and there will usually be a response that contains a body corresponding to the initiating request's `Accept` header.

#### 201 Created

The 201 Created follows after a successful `POST` or `PUT` and states where the newly resource was created in a `Location` header.

#### 202 Accepted

The 202 Accepted response signals that the request was understood and the response will follow later, the response should state information can be obtained later concerning the request for the resource returning the 202. This is signalled in the `Location` header, to be nice the expectations of the client can be set using the `Retry_After` and issue a timeframe for when it makes sense to ask for a status again from the client.

##### Headers

```
Location: http://get/the/new/status/location

Retry-After: 30
```

#### 301 Moved Permanently

The 301 is issued if a client asks for a resource that is no longer at the place it used to be. AS an example this could be a resource that has moved to a different part of the API. In that case a 301 is returned with a `Location` header containing the new position.

##### Headers

```
Location: http://this/is/the/new/location
```

#### 400 Bad Request

The 400 response states that this request was wrong and should not be retried

#### 401 Unauthorized

The 401 response states that this request did not have user authentication and that usually means that the client needs to either have a contract for that resource, authenticate the user, renew a token or ... in order to get access to the requested resource.


#### 403 Forbidden

The 403 response states that this request have user authentication but does not have sufficient authorizations to access the resource.

#### 404 Not Found

The 404 response states that the resource requested did not exist.

#### 409 Conflict
The 409 response states that the attempted request for the resource is resulting in a form of conflict, which the client must resolve before retrying. This could be trying to POST changes to an object that would cause the object to be in an erroneous state.

#### 503 Service Unavailable
The 503 response states that the server for some reason is unavailable.

##### Headers

```
Retry-After: Sat, 31 Dec 2016 23:59:59 GMT
```

or

```
Retry-After: 120
```
