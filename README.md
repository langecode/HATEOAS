# HATEOAS
HATEOAS is used in REST services in various ways and has different forms.
The goal of this repository is to start working for a common accepted way to use HATEOAS in REST services and Mi cro Services.
The form of HATEOAS that is HAL, because it features bot the linking posibility and a composition perspective. 
Both things affects the freedom to vary the size and coupling between services.

## Goals
The Goal is to propose HATEOAS as HAL in a certain way and present some practices at the API level,
that hopefully could be useful to a greater gropu of developers   

The ultimate goal of this project is to evolve into the set of best practices and have them at a level,
where they could make that into reference implemntations of REST and possible the standards such as e.g. JAX-RS.

### Why are we doing this? 
The primary aim is to help service developers to create better services that are easier to keep on developing.
We have seen that a lot of variations have been used in different services and the purpose of these ways seems
to be centered around solving issues that could be solved in a more elegant way using e.g. HATEOAS in form of HAL.

### How is the project organized?
This project is organized around an example of an invented simple banking API, written in Swagger. 
The example includes the best practices and point to more elaborate examples for the things used in the example.
These elaborate examples will include references to code that is already created in some version, but will also point to areas, 
where a proposed implementation has not yet been done. 

# Links
These are links that are relevant to this project.

The [HAL Specification](http://stateless.co/hal_specification.html) and the Informational specification [here](https://tools.ietf.org/html/draft-kelly-json-hal-08)

The [Swagger example for this HATEOAS HAL simple banking API](https://github.com/Nykredit/hateoas/Swagger/simple-banking-api-hateoas-hal-sample.yaml)

A proposed HATEOAS HAL data seralization done in Jackson is available at [Nykredit GitHub](https://github.com/Nykredit/jackson-dataformat-hal)


