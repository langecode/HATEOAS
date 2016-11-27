# HATEOAS
HATEOAS is used in REST services in various ways and has different forms. Read more on the [wiki](https://github.com/Nykredit/HATEOAS/wiki)
The goal of this repository is to start working for a common accepted way to use HATEOAS in REST services and Micro Services.
The form of HATEOAS used here is HAL, because it features both the linking possibility and a composition perspective. 
Both things affects the freedom to vary the size and coupling between services and 
has a significant influence on the Developer Experience for the APIs using it.

## Goals
The Goal of the project is to propose HATEOAS as HAL in a certain way and present some added best practices to that at the API level,
that hopefully could be useful to a greater group of developers. The aim being to have an easier and understandable way to express API 
capabilities in an easy way in the description of the API as well as providing a supported tooling for these capabilities or best practices
and in that way help developers to be more productive and deliver more business value.  

The ultimate goal of this project is to evolve these best practices to a level of maturity, where they could make that into reference 
implementations of REST and possible the standards such as e.g. JAX-RS. The initial approach however is to have developers working with 
it and mature this into something with a broad appeal to developers of REST services and having proven itself to be useful for service implementation. 

### Why are we doing this? 
The primary aim is to help service developers to create better services that are easier to keep on developing and we of course need that ourselves.
We have seen that a lot of variations in the approach for such capabilities used in different services by different developers. Looking closer
at these implementations of the capabilities it seems that it could be narrowed to something along the lines of the proposal in this project. 
The purpose of this is thus to propose a way for using HATEOAS in the form of HAL and add capabilities on top of that to help development
og REST services being done in an easy and understandable way. Ultimately solve needed issues around REST services in an elegant way 
using e.g. HATEOAS in form of HAL with these capabilities to support an evolutionary design of Micro Services.

### How is the project organized?
This project is organized around an example of an invented simple banking API, written in Swagger. 
The example includes the best practices and point to more elaborate examples for the things used in the example.
These elaborate examples will include references to code that is already created in some version, but will also point to areas, 
where a proposed implementation has not yet been done. 

#The Proposal
The proposal is to _use [HAL](http://stateless.co/hal_specification.html) for the HATEOAS part and use capabilities_ for functionality like:

 * **selection** 
   * client can ask for a certain object or set of objects 
   * based on concrete values of one or more attributes in json objects at a given endpoint
   * a query parameter _"select"_ is used for that

 * **sorting** 
   * client can ask for a sorted set objects 
   * based on concrete attributes in json objects at a given endpoint
   * a query parameter _"sort"_ is used for that

 * **temporal filter** 
   * client can ask for object(s) within a certain temporal limit 
   * based on the ability to filter within a fixed or dynamic interval and to specify the desired returned collection from an endpoint
   * a query parameter _"interval"_ is used for that

 * **pagination** 
   * clients can ask for particular object(s) with a range 
   * where the number of concrete elements can be specified as desired to be within the returned collection from an endpoint
   * a query parameter _"elements"_ is used for that

* **filter** 
   * clients can ask for "sparse" objects (if possible) 
   * based on specifying dynamic projections or views of the desired json to be returned from an endpoint
   * a query parameter _"filter"_ is used for that

* **composition** 
   * clients can signal what "related" information is desired (if possible) 
   * based on "object-type" (concept) and view specified.
   * a query parameter _"embed"_ is used for that 

 The capabilities will signal to the service, what the consumers of the service expects it to be able to deliver - or at least what the clients would like it to deliver.
 That is a way for the service to evolve or for service-spin-off's and that will be able to help services stay relevant and also to maximize the business value for these services.
 Please see [example in swagger.yaml](https://github.com/Nykredit/hateoas/blob/master/Swagger/simple-banking-api-hateoas-hal-sample.yaml) it is best viewed in the 
 [Swagger editor](http://editor.swagger.io/#/) where it is possible to import the file or paste the yaml file into the editor.

 Clients should be tolerant reader's and thus they cannot be sure that all services supports a capability, 
 which is why the clients needs to cope with the situation that a certain capability is not supported. 
 This is similar to the hypertext cache pattern in HAL, where clients knows that the __links_ object is contained and the __embedded_ object may or may not be contained.

* **Semantic ID creation** - for sensitive natural id's
 The creation of semantic ids is necessary in situations where the natural semantic id is sensitive and thus cannot be part of the url. 
 An example of this is a personal id used for people in Denmark called CPR number. This number is protected by law and thus this must not be used as semantic id for a person.
 Persons therefore needs a new non-sensitive id, which can be created from the first name, a potential middle name, the family name, the day in month where the person was born,
 the month of the year where the person was born and if not unique add a sequence number. 
 
 This is very similar to the MIFID CONCAT definitions where they unfortunately have used
 "#" for separation and a length that will cause:
   * the names to be less human readable
   * result in name clashes
   * not useful as semantic ids for person in a URL
   * require more added sequence numbers. 

 Therefore the proposed format is:
       <firstname>-<middlename-><familyname>-<ddMM><-sequence number>

    _examples:_
    ```
      hans-peter-hansen-0112

      hans-peter-hansen-0112-1 
      "The second is created if two persons with the same 
      name and born on the same day and month exists"

      mike-hansson-0309 
    ```
 If a "restricted length scenario" exist a length of 10 characters for first name, and 10 characters for middle name 
 and equally 10 characters for family name has been suggested and 999 as max sequence number.

 Such limitations should be written as a part of the service API and thus the consumers of services would know them.    

* **Services**
 A description of the communication between services and the use of /_link and /_embedded as well as recommendations to keep links and potential embedded objects relevant for consumers of services can be found [here](https://github.com/Nykredit/HATEOAS/wiki/Welcome).
 

# Links
These are links that are relevant to this project.

The [HAL Specification](http://stateless.co/hal_specification.html) and the Informational specification [here](https://tools.ietf.org/html/draft-kelly-json-hal-08)

The [Swagger example for this HATEOAS HAL simple banking API](https://github.com/Nykredit/hateoas/blob/master/Swagger/simple-banking-api-hateoas-hal-sample.yaml)
The file is best viewed in the [Swagger editior](http://editor.swagger.io/#/) and import the file inside the editor.

The is more material to be read on the [wiki](https://github.com/Nykredit/HATEOAS/wiki)

A proposed HATEOAS HAL data serialization done in Jackson is available at [Nykredit GitHub](https://github.com/Nykredit/jackson-dataformat-hal)

A Maven archetype targetted for services using this principle is under development and will be available soon.
