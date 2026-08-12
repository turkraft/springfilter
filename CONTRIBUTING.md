# Contributing to Spring Filter

Thanks for taking the time to contribute.

## Getting Started

### Prerequisites

- JDK 17 or later
- Maven 3.6+

### Build

```bash
mvn clean package
```

This compiles all modules and runs tests.

### Project Structure

```
springfilter/
  core/              # Parser, filter builder, AST nodes, language definitions
  jpa/               # JPA Criteria integration
  jpa-language/      # JPA-specific built-in functions
  jpa-example/       # Example Spring Boot app using JPA module
  mongo/             # MongoDB integration
  mongo-language/    # MongoDB-specific built-in functions
  mongo-example/     # Example Spring Boot app using MongoDB module
  predicate/         # In-memory java.util.function.Predicate filtering
  predicate-language/# Predicate-specific built-in functions
  openapi/           # Swagger/OpenAPI auto-documentation
  page-sort/         # Pagination, sorting, field selection annotations
  typesafe/          # Compile-time type-safe filter builder runtime
  typesafe-processor/ # Annotation processor for @Filterable
  typesafe-example/  # Example app using type-safe builders
```

### IDE Setup

Import the root `pom.xml` as a Maven project. The project uses standard Maven layout.

## Coding Style

Use [Google Java Style](https://github.com/google/styleguide/blob/gh-pages/eclipse-java-google-style.xml). Configure your IDE formatter accordingly.

## Pull Requests

1. Fork the repository and create a branch from `main`.
2. If adding a feature, consider opening an issue first to discuss it.
3. Follow the existing code style and patterns.
4. Add tests for new functionality.
5. Ensure all tests pass with `mvn clean package`.
6. Update the README if your change affects public API or user-facing behavior.
7. Squash your commits into a single, well-described commit before submitting if your branch has multiple small commits.

## Reporting Issues

- Use the GitHub issue tracker.
- Include the module and version you're using.
- Provide a minimal reproduction case.
- For bug reports, include the full error message and stack trace.

## Adding a New Module

If you want to add support for a new database or backend:

1. Create a new Maven module following the naming convention (`<backend>` and `<backend>-language`).
2. Implement a `FilterNodeTransformer` for the target backend.
3. Implement processors for each operator and function in your transformer.
4. Add a converter that ties the parser output to your transformer.
5. Add autoconfiguration with a Spring `@Configuration` class.
6. Add a `-language` module with backend-specific functions if needed.
7. Create an `-example` module demonstrating usage.

## Adding a Custom Operator or Function

Operators and functions can be added directly in the `core` module if they are backend-agnostic:

1. Extend `FilterInfixOperator`, `FilterPrefixOperator`, or `FilterPostfixOperator` (for operators) or `FilterFunction` (for functions).
2. Implement the corresponding processor interface for each backend module that should support it.
3. Spring's auto-configuration will pick up your `@Component`-annotated operator or function automatically.

## License

By contributing, you agree that your contributions will be licensed under the MIT License.
