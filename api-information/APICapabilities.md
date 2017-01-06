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

