#Description
This "Simple Banking API Sample" serves to exemplify a way of using HATEOAS in the form of HAL in the API in and easy and intuitive way. [Read more](https://github.com/Nykredit/HATEOAS/wiki)
Furthermore it points to elaborated examples and what would be nice to have supported in a better way in the REST implementations and possibly in Swagger in order have an easy way to use the full potential of HATEOAS in the form of HAL and services.

    
## API Description
    
This API show a very simple set of resources that emulates three micro-services which are: account, payment and Person.
The Idea is to create everything as one large single Swagger file and decompose that into some sections: e.g. a general section, 
a capabilities section, where the various API capabilities can be imported etc. [Read more about the Capabilities](https://github.com/Nykredit/HATEOAS)

## General Principles

A semantic REST API is using and thus also HATEOAS in form of the hal specification.
The API can be versioned at the structural level by means of a HTTP Header and at the content level in each endpoint by means of the content-type.

## HTTP Headers used

A number os headers are used.

* `X-Log-Token` for correlating a number activities between service and consumers of the service.
* `X-Client-Version` for identification of a client version and its contract.
* `X-Service-Generation` to signal a non-current structure of the API (saves known redirects from a client perspective)

* `Accept` is used to signal what content the consumer wished and the version and projection can be specified. 
### Correlation between service (Server) and consumer (Client)

A `X-Log-Token` header is used to give a client the opportunity to find a number of calls and activities related to calls belonging to that context.
If the client includes a `X-Log-Token` header and a value associated with that, the value will be extended with a timetick initially to ensure uniqueness.
The original `X-Log-Token` is returned in every response from the service, the client must include the unique token received in the response in the 
following requests if the correlation is still what the client wants. If the client does not include a `X-Log-Token` the service creates a unique token
and returns that in the response and the client can use that.  

### Client Identifier

The `X-Client-Version` header is used for identification of the client version and is required in order for a client to successfully operate on resources. The version in according to semver-org

### Versions

There exists two major forms of versioning: one is related to the structure of the API, the other is related to the contents in each endpoint in the API.

The two different aspects are handled in each their dedicated fashion. 

The `X-Service-Generation` HTTP header is used for signalling the version of the API structure instead of having the version as a part of the baseURL. 

The content-type includes version information and is returned in every response from the service. The content-type can do that is a couple of ways: 

  * using `"_links": { "href": "..."}` with no `"type"` will point to the newest and current content at the referenced endpoint.

  * using  `"_links": { "href": "...", "type": "application/hal+json;v=1"}` with `"type"` will point to the version listed and in this case that is the version 1 at the referenced endpoint.

  The client must know if a problem has occurred in a situation, where the contents from a service endpoint was updated in a way that this particular client could not cope with and therefore it must know what version works and this the hal specification can be used decorate the `"_links":` object with the version of the content that it understands. That lets the client include the understandable content-type as defined in `"_links":` and include that as the value of the "Accept" header.
  An example of such client side decorated response from a server, where the default and newest content-type are "overwritten" by the type for the users.


    {
      "label": "Budget Account",
      "currency": "DKK",
      ...
      "_links": {
        "transactions": [{
            "href": "accounts/1234-567890/transactions/987654321"
          }, {
            "href": "accounts/1234-567890/transactions/987654322"
          }, {
            "href": "accounts/1234-567890/transactions/987654323"
          }
        ],
        "users": {
          "primary": {
            "href": "users/hans-b-hansen-13-09-1234",
            "type": "application/hal+json;v=1"
          },
          "coUsers": [{
              "href": "users/frederikke-b-hansen-16-07-6789",
              "type": "application/hal+json;v=1"
            },{
              "href": "users/ulla-b-hansen-23-03-4567",
              "type": "application/hal+json;v=1"
            }]
        }
      }
    }

If a projection (a given view on e.g. the user is needed) that may be included in the content-type as well, if a matching producer is available in the service that will be used. the version is referring to the structures and contents of the json response from a given endpoint. It is not the historical state of a user object. Examples of the versions of content by value in the `Accept` header below:

* `"application/hal+json;concept=user;v=1"` for the complete user json in hal format

* `"application/hal+json;concept=user-basic;v=1"` for the basic user information in hal json format

* `"application/hal+json;concept=user-basic"` for the newest version of basic user information in hal format

* `"application/hal+json"` for the newest version of user information in hal format


It is possible to express the same without using the parameters as shown above using - although less elegant and less HTTPish:


* `"application/hal+json+user+1"` for the complete user json in hal format

* `"application/hal+json+user-basic+1"` for the basic user information in hal json format

* `"application/hal+json+user-basic"` for the newest version of basic user information in hal format

## API Capability Set

In every API of a certain size a number of capabilities are used. These capabilities consists of a particular set of functionality. This functionality can be divided into the following capabilities:



     Selection:    `select` 
                   - selecting objects by attribute value(s) for response

     Sorting:      `sort` 
                   - sorts objects descending and ascending by 
                     attribute(s) for response

     Temporal:     `interval` 
                   - limits relevant objects in desired response
                     to be within a certain time frame

     Pagination:   `elements` 
                    - specifies the elements in a range desired 
                      to be in response

     Filtering:   `filter` 
                   - ask for exclusion or inclusion of particular
                     attributes or objects in response

     Composition: `embed` 
                  - ask for the inclusion of "related" objects and 
                    projection into the response.


These capabilities may be applied individually to endpoints in APIs.
The user of the API endpoint can see what capability/ies is/are supported at each endpoint by looking at the for tags like select, sort, paginate etc. The Swagger tags are used here to achieve an easy way to show the capabilities in each endpoint as can be seen further down in the Swagger specification.

Another perspective that is often seen in APIs is the use of technical keys (potentially UUIDs) which are semantically poor, but often seen as a necessity for sensitive keys such as social security numbers. In order to avoid having these sensitive information leaked to logs and other places, there is a need for bringing these keys into the body of a request and a non-sensitive key is going to help. The problem with an UUID'ish key is that the developer experience is not optimal. Therefore it would be a nice thing to get some form of consensus on a derived capability like :

      Sensitive Semantic ID deconstruction 
      - generation of non-sensitive semantic key for objects that has a sensitive
        semantical key in the form of something that has a better developer
        experience than e.g. UUIDs can offer.

### Selection API Capability

Selection by criteria(s) is done using a Query Parameter called `select`. 

    The syntax is: select="<attribute>::<value>|<atribute>::<value>|..."


The usage can be exemplified by e.g. asking for accounts having a balance equal to 100.
Currency is omitted here on purpose.


#### The concrete url would look like: 

     <https://banking.services.sample-bank.dk/
              accounts?select="balance::100">
     
     "balance::100"
     which returns accounts having a exact balance of 100.
#### Another example:
    <https://banking.services.sample-bank.dk/
             accounts?select="balance::100+|balance::1000-">
    
    select="balance::100+|balance::1000-"
    which returns accounts having a balance between 100 and 1000 (both inclusive).
    
#### Yet another example:
    <https://banking.services.sample-bank.dk/
             accounts?select="no::123456789+|no::234567890">
    
    select="no::123456789+|no::234567890"
    which returns the to accounts having account numbers "123456789" and "234567890" and thus it works as a way to select certain objects, in this case based on the semantic key for an account which is the number of that account.

### Sorting API Capability

Sorting is done using a `sort` Query Parameter.
Sorting can be done ascending (default) or descending

      The syntax is: sort="<attribute>+/-|<attribute>+/-|..."
      and is equivalent to: sort="<attribute>::+/-|<attribute>::+/-|..."

The usage can be exemplified as an

#### Example:

    <https://banking.services.sample-bank.dk/
             accounts?sort=balance>
    which returns an ascending set of accounts sorted by balance


#### Another example is: 

    <https://banking.services.sample-bank.dk/
             accounts?select=balance|lastUpdate->
    which returns a set of account sorted descending by
    lastUpdate and ascending by balance. 

### Temporal API Capability

Temporal aspects are handled using the `interval` Query Parameter.


      The syntax is: interval="<now/from/to/at/::+/-/#d/#/now>|
                               <now/from/to/at/::+/-/#d/#>"

#### Example:

    <https://banking.services.sample-bank.dk/
             accounts/1234-56789/transactions?interval="from::-14d|to::now">
    which returns the transactions from a specific account within the last 14 days

#### Another example:

    <https://banking.services.sample-bank.dk/
             accounts/1234-56789/transactions?interval="from::1476449846|to::now">
    
    <https://banking.services.sample-bank.dk/
             accounts/1234-56789/transactions?interval="from::1476449846">
    
    <https://banking.services.sample-bank.dk/
             accounts/1234-56789/transactions?interval="at::1476449846">
    
    The latter three returns the transactions from a specific account 
    within the last day assuming now is friday the. 
    14th of October 2016 UTC time.

### Pagination API Capability 

Pagination of responses is obtained by using the Query parameter `elements`.
The Query Parameter `elements` signals the initial element and the last element that is desired to be part of the response.

    The syntax is: elements="<startingFrom>|<endingAt>" both inclusive.

#### Example:

     <https://banking.services.sample-bank.dk/
              accounts/1234-56789/transactions?elements="10|30"> 
     which returns element 10 as the first entry in the json 
     response and element 30 as the last entry in the response.


A maximum element size is defined here max size is 500 elements

### Filtering API Capability

The Query parameters `filter` is used for signalling to the server that a dynamic projection is desired as the response from the service. The service is not obliged to be able to do that, but may return the standard projection of the objects given for that concrete endpoint. This can be used for discovery of what projections service consumers would like to have and help evolving the API to stay relevant and aligned with the consumers use of the service.

    The syntax is: filter="<attribute>::+/-|<attribute>::+/-" 
    + means include only
    - means exclude only
    


#### Example:    

    <https://banking.services.sample-bank.dk/
             accounts/1234-56789?filter="balance::-|name::-"> 
    which ideally returns a account object in the response without balance 
    and name attributes.
    The service may however in the event that this is not supported,
    choose to return a complete object and not this sparse dynamic view. 


#### Example:

    <https://banking.services.sample-bank.dk/
             accounts/1234-56789?filter="balance::+|name::+">
    which ideally returns a account object in the response with only balance
    and name attributes.
    The service may however in the event that this is not supported,
    choose to return a complete object and not this sparse dynamic view. 
### Composition  API Capability 

Composition is about enabling the consumers of services, the Query Parameter `embed` is used to signal to the service that the consumer would like to have a certain assumed related object included as a part of the response if possible.

    The syntax is: embed="<concept>::<projection>|<concept>::<projection>|..." 
    
    
#### Example:

    <https://banking.services.sample-bank.dk/
             accounts/1234-56789?embed="transaction::list|owner::sparse"
    
    embed="transaction::list|owner::sparse"
    which ideally will return a json response including `_links` and `_embeddded` objects inside the response containing either a map or array of transactions with links in the `_links` object and the desired projection in the `_embedded` object for both owner and transactions. 
    
  
The service can choose to return just the accounts including links to transactions under the `_links` object as this is allowed by HAL. The Query Parameter can be used for evolving the service to match the desires of consumers - if many consumers are having the same wishes for what to embed - the owners of the service could start considering whether they want to include more in the responses and endure the added coupling between this service and the service that may deliver the embedded information. This coupling should of course not be synchronous.

### Sensitive Id decomposition 

The creation of an id can be challenging especially if the true semantic id is protected by law, which is the case for people. Therefore a either a UUID is suggested or a semi-semantic approach like firstName-middleName-sirName-dayInMonth-MonthInYear-Sequence, that allows for a human readable - yet not revealing id for a person.

Other suggested methods for doing has been to create a hash(sensitive semantic key), which might work but will be vulnerable for a brute force reengineering effort. The response to that is often to salt it, that is salt(hash(sensitive sementic key)), and that is ok but is seems merely to be a very difficult way to create a UUID, which means we have a key that is developer unfriendly - at least compared to the more human readable key consisting of recognizable fragments from the real world.

The suggested approach is firstname-middlename-familyname-ddMM-sequencenumber

#### Example:

    hans-p-hansen-0112 the initial created Hans P Hansen born on the 1st of December
    hans-p-hansen-0112-1 the second created Hans P Hansen born on the 1st of December
    hans-p-hansen-0112-94 the 95th created Hans P Hansen born on the 1st of December
 
    mike-hansson-0309 the initially created Mike Hansson born on the 3rd of September
 

## Resilience


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

## Service terms

The terms of using the service is as follows, the contract found at <https://sample-bank.dk/services/contracts/2345678> states the general terms for using this service. Consumers can create their own individual contract and terms for usage at <https://sample-bank.dk/services/consumers>
  