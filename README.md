# Spring Filter

<p align="center">
  <a href="https://github.com/turkraft/springfilter">
    <img src="https://raw.githubusercontent.com/turkraft/springfilter/main/.github/logo.png?raw=true" alt="Spring Filter Logo">
  </a>
</p>

Dynamic query filtering for Spring applications. Pass filter expressions as URL parameters and apply them to JPA repositories, MongoDB collections, or in-memory Java objects.

The library parses filter expressions into abstract syntax trees, then converts them to JPA Criteria queries, MongoDB queries, or Java Predicates depending on your module. You can also use the filter builder to construct queries programmatically.

<details>
  <summary>:warning: <b><u>About Release 3.0.0</u></b></summary>

  > [Spring Filter 3.0.0](https://github.com/turkraft/springfilter/issues/254) is a new release built from the ground up. It includes much better integration with Spring, with many new features, enhancements and bug fixes.
  > The language syntax didn't change, frontend applications will therefore not require any modification.
  > The new `FilterBuilder` class is incompatible with the previous one and other breaking changes are present but the basic usage of the library remains similar.
  > Please feel free to [create an issue](https://github.com/turkraft/spring-filter/issues) if you notice anything wrong. [Consider supporting the project by sponsoring us](https://github.com/sponsors/torshid).

  > You can access the older version in the [2.x.x branch](https://github.com/turkraft/spring-filter/tree/2.x.x).
</details>

## Example ([try it live](https://springfilter-jpa.onrender.com/))

*/search?filter=* **average**(ratings) **>** 4.5 **and** brand.name **in** ['audi', 'land rover'] **and** (year **>** 2018 **or** km **<** 50000) and color **:** 'white' **and** accidents **is empty**

```java
@Entity public class Car {
  @Id long id;
      int year;
      int km;
  @Enumerated Color color;
  @ManyToOne Brand brand;
  @OneToMany List<Accident> accidents;
  @ElementCollection List<Integer> ratings;
}
```

The library handles booleans, dates, enums, functions, and entity relations. JPA module generates criteria queries, MongoDB module generates aggregation pipelines, and predicate module filters in-memory objects.

## [Sponsors](https://github.com/sponsors/torshid)

Sponsor our project and have your issues prioritized.

<table>
<tr>
<td><a href="https://github.com/ixorbv"><img width="64" src="https://avatars.githubusercontent.com/u/127401397?v=4"/></a></td>
</tr>
</table>

## Modules

### JPA

Filter JPA entities directly in database queries. The module converts filter expressions to JPA Criteria API specifications.

```xml
<dependency>
  <groupId>com.turkraft.springfilter</groupId>
  <artifactId>jpa</artifactId>
  <version>3.2.1</version>
</dependency>
```

```java
@GetMapping("/cars")
Page<Car> search(@Filter Specification<Car> spec, Pageable page) {
    return repository.findAll(spec, page);
}
```

The repository must implement `JpaSpecificationExecutor`. `SimpleJpaRepository` is a standard implementation. Remove the `Pageable` argument if you don't need pagination.

#### JPA with Native Queries

```java
@GetMapping("/cars/native")
List<Car> searchNative(@Filter Specification<Car> spec) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Car> query = cb.createQuery(Car.class);
    Root<Car> root = query.from(Car.class);

    query.where(spec.toPredicate(root, query, cb));

    return entityManager.createQuery(query).getResultList();
}
```

#### JPA with Projections

```java
@GetMapping("/cars/summary")
List<CarSummary> searchProjection(@Filter Specification<Car> spec) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<CarSummary> query = cb.createQuery(CarSummary.class);
    Root<Car> root = query.from(Car.class);

    query.select(cb.construct(CarSummary.class,
        root.get("brand").get("name"),
        cb.count(root)));
    query.where(spec.toPredicate(root, query, cb));
    query.groupBy(root.get("brand").get("name"));

    return entityManager.createQuery(query).getResultList();
}
```

### MongoDB

Filter MongoDB documents using Spring Data MongoDB queries.

```xml
<dependency>
  <groupId>com.turkraft.springfilter</groupId>
  <artifactId>mongo</artifactId>
  <version>3.2.1</version>
</dependency>
```

```java
@GetMapping("/cars")
Page<Car> search(@Filter(entityClass = Car.class) Query query, Pageable page) {
    return mongoTemplate.find(query.with(page), Car.class);
}
```

#### With MongoRepository

```java
public interface CarRepository extends MongoRepository<Car, String> {
  @Query("?0")
  List<Car> findAll(Document document);

  @Query("?0")
  Page<Car> findAll(Document document, Pageable pageable);
}

@GetMapping("/cars")
Page<Car> search(@Filter(entityClass = Car.class) Document document, Pageable page) {
    return repository.findAll(document, page);
}
```

### Predicate

Filter in-memory collections using Java Predicates. Works with any POJO, no database required.

```xml
<dependency>
  <groupId>com.turkraft.springfilter</groupId>
  <artifactId>predicate</artifactId>
  <version>3.2.1</version>
</dependency>
```

```java
@GetMapping("/cars")
List<Car> search(@Filter Predicate<Car> predicate) {
    List<Car> allCars = loadCarsFromCache();
    return allCars.stream()
        .filter(predicate)
        .collect(Collectors.toList());
}
```

#### Manual Conversion

```java
@Autowired FilterPredicateConverter converter;

public List<Car> filterCars(List<Car> cars, String filterExpression) {
    FilterPredicate<Car> predicate = converter.convert(filterExpression, Car.class);

    return cars.stream()
        .filter(predicate)
        .collect(Collectors.toList());
}
```

#### Use Cases

The predicate module is useful when:
- Filtering cached data in memory
- Filtering API responses before returning to client
- Testing filter logic without database
- Filtering configuration objects or enums
- Processing batch data in memory

```java
@GetMapping("/cars/cached")
List<Car> searchCached(@Filter Predicate<Car> predicate) {
    return cacheService.getAllCars().stream()
        .filter(predicate)
        .collect(Collectors.toList());
}

@GetMapping("/cars/filter-after-fetch")
List<Car> filterAfterFetch(@Filter Predicate<Car> predicate) {
    List<Car> cars = externalApiClient.fetchAllCars();
    return cars.stream()
        .filter(predicate)
        .collect(Collectors.toList());
}
```

#### Size Function Support

```java
// Filter by collection size
GET /cars?filter=size(accidents) > 2
GET /owners?filter=size(cars) : 0
```

The predicate module supports all standard operators and the `size()` function for collections, arrays, maps, and strings.

### Filter Builder

Build filter expressions programmatically instead of writing filter strings manually.

```xml
<dependency>
  <groupId>com.turkraft.springfilter</groupId>
  <artifactId>core</artifactId>
  <version>3.2.1</version>
</dependency>
```

```java
@Autowired FilterBuilder fb;

FilterNode filter = fb.field("year").equal(fb.input(2025))
    .and(fb.field("category").isNull())
    .get();

@Autowired ConversionService cs;
String query = cs.convert(filter, String.class);
// year : 2025 and category is null
```

#### Complex Queries

```java
FilterNode filter = fb.field("brand.name").in(
    fb.collection(fb.input("audi"), fb.input("bmw"))
).and(
    fb.field("year").greaterThan(fb.input(2020))
        .or(fb.field("km").lessThan(fb.input(50000)))
).get();
```

#### With Functions

```java
@Autowired SizeFunction sizeFunction;

FilterNode filter = fb.function(sizeFunction, fb.field("accidents"))
    .greaterThan(fb.input(2))
    .and(fb.field("year").lessThan(fb.input(2015)))
    .get();
```

## Frontend Integration

### JavaScript

Use [spring-filter-query-builder](https://github.com/sisimomo/Spring-Filter-Query-Builder) to build queries in JavaScript/TypeScript.

```javascript
import { sfAnd, sfEqual, sfGt, sfIsNull, sfLike, sfNot, sfOr } from 'spring-filter-query-builder';

const filter = sfAnd([
  sfAnd([sfEqual('status', 'active'), sfGt('createdAt', '1-1-2000')]),
  sfOr([sfLike('value', '*hello*'), sfLike('name', '*world*')]),
  sfNot(sfOr([sfGt('id', 100), sfIsNull('category.order')])),
]);

fetch('http://api/person?filter=' + filter.toString());
```

### Angular

See [spring-filter-ng](https://github.com/68ociredef/spring-filter-ng) documentation.

## Language Syntax

### Field Access

```
field
field.nested
field.nested.deep
```

### Literals

```
123                 // integer
-321.123            // decimal
true, false         // boolean
'text'              // string
'1-01-2023'         // date (format depends on Spring ConversionService)
'escape \' quote'   // escaped string
```

### Collections

```
[1, 2, 3]
['a', 'b', 'c']
[field, nested.field, 'literal']
[field, ['nested', 'array'], 99]
```

### Functions

```
size(collection)
size(field.collection)
today()
```

### Placeholders

```
`placeholder_name`
```

Placeholders are resolved by custom placeholder processors you implement.

### Operators

```
a and b              // logical and
a or b               // logical or
not a                // logical not
a : b                // equals
a ! b                // not equals
a > b                // greater than
a >: b               // greater than or equal
a < b                // less than
a <: b               // less than or equal
a ~ 'pattern'        // like (% and _ wildcards)
a ~~ 'pattern'       // case-insensitive like
a in [x, y]          // in collection
a not in [x, y]      // not in collection
a is null            // null check
a is not null        // not null check
a is empty           // empty check (collections/strings)
a is not empty       // not empty check
```

### Precedence

Use parentheses to control evaluation order:

```
a and (b or c)
(status : 'active' or status : 'pending') and year > 2020
```

## Query Examples

### Basic Filtering

```
// Simple equality
?filter= status : 'active'

// Multiple conditions
?filter= year > 2020 and km < 50000

// OR conditions
?filter= color : 'red' or color : 'blue'
```

### Working with Relations

```
// Filter by related entity field
?filter= brand.name : 'audi'

// Nested relations
?filter= brand.manufacturer.country : 'germany'

// Check if relation exists
?filter= brand is not null

// Multiple relation conditions
?filter= brand.name : 'audi' and dealer.city : 'berlin'
```

### Collection Operations

```
// Check if value is in list
?filter= status in ['active', 'pending', 'review']

// Check collection size
?filter= size(accidents) > 2

// Check if collection is empty
?filter= accidents is empty

// Check if collection is not empty
?filter= ratings is not empty
```

### String Matching

```
// Like with wildcards (% = any chars, _ = single char)
?filter= name ~ '%john%'

// Case-insensitive like
?filter= name ~~ 'JOHN'

// Starts with
?filter= email ~ 'admin%'

// Ends with
?filter= filename ~ '%.pdf'

// Pattern matching
?filter= code ~ 'PRD-____-2023'
```

### Date Filtering

```
// Date comparison (format depends on your Spring configuration)
?filter= createdAt > '2023-01-01'

// Date range
?filter= createdAt > '2023-01-01' and createdAt < '2023-12-31'

// Relative dates with today() function
?filter= createdAt > today()
```

### Complex Queries

```
// Nested conditions with precedence
?filter= (year > 2020 and km < 30000) or (year > 2018 and km < 10000)

// Mix of different operators
?filter= brand.name in ['audi', 'bmw'] and year > 2020 and accidents is empty and color ! 'white'

// Collection size with relations
?filter= size(owner.vehicles) > 3 and status : 'active'
```

### Null and Empty Checks

```
// Check for null
?filter= deletedAt is null

// Check for not null
?filter= description is not null

// Empty collection
?filter= tags is empty

// Non-empty collection
?filter= children is not empty
```

## Supported Types

All modules handle:
- Primitives (int, long, double, boolean, etc.)
- Strings
- Enums
- Dates (LocalDate, LocalDateTime, Date, Instant, etc.)
- Collections (List, Set)
- Arrays
- Entity relations (@ManyToOne, @OneToMany, @ManyToMany)

Date parsing uses Spring's `ConversionService`. Configure it to change date formats.

## Custom Operators

Define custom operators by extending `FilterInfixOperator`, `FilterPrefixOperator`, or `FilterPostfixOperator`:

```java
@Component
public class ContainsOperator extends FilterInfixOperator {
    public ContainsOperator() {
        super("contains", 5);
    }
}
```

Then implement processors for each module you use:

```java
@Component
public class ContainsOperationExpressionProcessor implements
    FilterInfixOperationProcessor<FilterExpressionTransformer, Expression<?>> {

    @Override
    public Class<FilterExpressionTransformer> getTransformerType() {
        return FilterExpressionTransformer.class;
    }

    @Override
    public Class<ContainsOperator> getDefinitionType() {
        return ContainsOperator.class;
    }

    @Override
    public Expression<?> process(FilterExpressionTransformer transformer,
                                  InfixOperationNode source) {
        // Implementation
    }
}
```

Register the operator with autoconfiguration or manual configuration.

## Custom Functions

Define custom functions by extending `FilterFunction`:

```java
@Component
public class LowerFunction extends FilterFunction {
    public LowerFunction() {
        super("lower");
    }
}
```

Implement processors for each module:

```java
@Component
public class LowerFunctionExpressionProcessor implements
    FilterFunctionProcessor<FilterExpressionTransformer, Expression<?>> {

    @Override
    public Class<FilterExpressionTransformer> getTransformerType() {
        return FilterExpressionTransformer.class;
    }

    @Override
    public Class<LowerFunction> getDefinitionType() {
        return LowerFunction.class;
    }

    @Override
    public Expression<?> process(FilterExpressionTransformer transformer,
                                  FunctionNode source) {
        Expression<?> arg = transformer.transform(source.getArgument(0));
        return transformer.getCriteriaBuilder().lower((Expression<String>) arg);
    }
}
```

## Configuration

### Default Parameter Name

By default, filters are read from the `filter` query parameter. Override this:

```java
@GetMapping("/cars")
List<Car> search(@Filter(parameter = "q") Specification<Car> spec) {
    return repository.findAll(spec);
}
```

Now use `?q=year > 2020` instead of `?filter=year > 2020`.

### Entity Class for MongoDB

MongoDB requires explicit entity class specification:

```java
@GetMapping("/cars")
List<Car> search(@Filter(entityClass = Car.class) Query query) {
    return mongoTemplate.find(query, Car.class);
}
```

### Optional Filters

Use `Optional` to handle missing filter parameters:

```java
@GetMapping("/cars")
List<Car> search(@Filter Optional<Specification<Car>> spec) {
    return repository.findAll(spec.orElse(null));
}
```

### Programmatic Filter Application

Apply filters without Spring MVC annotations:

```java
@Autowired FilterSpecificationConverter jpaConverter;
@Autowired FilterQueryConverter mongoConverter;
@Autowired FilterPredicateConverter predicateConverter;

public void manualFiltering() {
    // JPA
    Specification<Car> spec = jpaConverter.convert("year > 2020", Car.class);
    List<Car> jpaCars = repository.findAll(spec);

    // MongoDB
    Query query = mongoConverter.convert("year > 2020", Car.class);
    List<Car> mongoCars = mongoTemplate.find(query, Car.class);

    // Predicate
    FilterPredicate<Car> predicate = predicateConverter.convert("year > 2020", Car.class);
    List<Car> filteredCars = allCars.stream().filter(predicate).collect(Collectors.toList());
}
```

## Spring Configuration

Spring Filter uses Spring's `ConversionService` for type conversions. Configure it to customize date formats, enum handling, etc:

```java
@Configuration
public class ConversionConfig {

    @Bean
    public ConversionService conversionService() {
        DefaultConversionService service = new DefaultConversionService();
        service.addConverter(new StringToLocalDateConverter());
        return service;
    }
}
```

## Testing

### Unit Testing with Filter Builder

```java
@Test
void testFilterBuilder() {
    FilterNode filter = fb.field("year").greaterThan(fb.input(2020)).get();
    Specification<Car> spec = jpaConverter.convert(filter);

    List<Car> result = repository.findAll(spec);

    assertTrue(result.stream().allMatch(car -> car.getYear() > 2020));
}
```

### Integration Testing

```java
@SpringBootTest
@AutoConfigureMockMvc
class FilterIntegrationTest {

    @Autowired MockMvc mockMvc;

    @Test
    void testFilter() throws Exception {
        mockMvc.perform(get("/cars?filter=year > 2020"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[*].year").value(everyItem(greaterThan(2020))));
    }
}
```

### Testing Predicates

```java
@Test
void testPredicateFiltering() {
    List<Car> cars = Arrays.asList(
        new Car("Audi", 2021),
        new Car("BMW", 2019),
        new Car("Mercedes", 2022)
    );

    FilterPredicate<Car> predicate = predicateConverter.convert("year > 2020", Car.class);
    List<Car> filtered = cars.stream().filter(predicate).collect(Collectors.toList());

    assertEquals(2, filtered.size());
}
```

## Performance

### JPA

JPA module generates optimized Criteria queries. The database executes filtering, so performance matches native SQL queries. Use appropriate indexes on filtered fields.

### MongoDB

MongoDB module generates aggregation pipelines. Performance depends on indexes. Profile queries with MongoDB's explain plan.

### Predicate

Predicate module filters in memory. Performance is O(n) where n is collection size. Suitable for:
- Small collections
- Cached data
- Already loaded collections
- Testing

For large datasets, prefer JPA or MongoDB modules to filter at database level.

## Contributing

Pull requests welcome. Use [Google Java Style](https://github.com/google/styleguide/blob/gh-pages/eclipse-java-google-style.xml) for formatting.

### Contributors

* [@marcopag90](https://github.com/marcopag90) and [@glodepa](https://github.com/glodepa) - MongoDB support
* [@sisimomo](https://github.com/sisimomo) - [JavaScript query builder](https://github.com/sisimomo/Spring-Filter-Query-Builder)
* [@68ociredef](https://github.com/68ociredef) - [Angular query builder](https://github.com/68ociredef/spring-filter-ng)

## Articles

* [Easily filter entities in your Spring API](https://torshid.medium.com/easily-filter-entities-in-your-spring-api-f433537cfd41)

## License

MIT License - see [LICENSE](LICENSE) file.
