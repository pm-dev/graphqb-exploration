# GraphDB

This repository demonstrates how to implement a graph-based web application architecture. It is written in Kotlin and exposes a GraphQL interface.

The application was written using:

- [Kotlin](https://kotlinlang.org/)
- [Jetbrains Exposed](https://github.com/JetBrains/Exposed) type-safe SQL wrapper
- [Spring Boot](https://projects.spring.io/spring-boot/)
- [GraphQL](http://graphql.org/)
- [GraphQL Tools](https://github.com/graphql-java/graphql-java-tools)

The project is split into 2 packages, `framework` and `starwars`. `framework` is an object-graph-mapper (OGM) for data held in a relational database. The `starwars` package shows how any data model could plug into the `framework` package with minimal effort. The `framework` package is not currently offered as a library due to its lack of documentation and testing.


To build and run the application locally, from the repository root:
```
gradlew run
```

Then load `http://localhost:5000/graphiql.html` and start playing around in GraphiQL
