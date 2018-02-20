# GraphDB

This repository demonstrates how to implement a graph-based server architecture. It is written in Kotlin and exposes GraphQL interface. It is based on a blog post from [Excuse The Disruption](http://www.excusethedisruption.com/writing-a-graphql-server-in-kotlin/)

The application was written using:

- [Kotlin](https://kotlinlang.org/)
- [PostgreSQL](https://www.postgresql.org/)
- [Jetbrains Exposed](https://github.com/JetBrains/Exposed) type-safe SQL wrapper
- [Spring Boot](https://projects.spring.io/spring-boot/)
- [Gradle](https://gradle.org/)
- [GraphQL](http://graphql.org/)
- [GraphQL Tools](https://github.com/graphql-java/graphql-java-tools)

The project is split into 2 packages, `framework` and `starwars`. The `starwars` package shows how any data model could plug into the `framework` package with minimal effort. The `framework` package is not currently offered as a library due to its lack of documentation and testing.
