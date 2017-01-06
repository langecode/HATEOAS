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
