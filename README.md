# Spring Filter

<p align="center">
  <a href="https://github.com/turkraft/spring-filter">
    <img src="https://i.imgur.com/0otEurv.png" alt="Spring Filter Logo" width="180">
  </a>
</p>

You need a way to dynamically filter entities without any effort? Just add me to your `pom.xml`.
Your API will gain a full featured search functionality. You don't work with APIs? No problem, you may still not want to mess with SQL, JPA predicates, security, and all of that I guess.

## Example ([try it live](https://spring-filter.herokuapp.com))
*/search?filter=* **average**(ratings) **>** 4.5 **and** brand.name **in** ('audi', 'land rover') **and** (year **>** 2018 **or** km **<** 50000) and color **:** 'white' **and** accidents **is empty**

```java
/* Entity used in the query above */
@Entity public class Car {
  @Id long id;
      int year;
      int km;
  @Enumerated Color color;
  @ManyToOne Brand brand;
  @OneToMany List<Accident> accidents;
  @ElementCollection List<Integer> ratings;
  // ...
}
```

> :rocket: Yes we support booleans, dates, enums, functions, **and even relations**! Need something else? [Tell us here](https://github.com/torshid/spring-filter/issues).

## Installation

```xml
<dependency>
    <groupId>com.turkraft</groupId>
    <artifactId>spring-filter</artifactId>
    <version>2.1.1</version>
</dependency>
```

## Usages

### a. Controller
> Requires **javax.persistence-api**, **spring-data-jpa**, **spring-web** and **spring-webmvc**
```java
@GetMapping(value = "/search")
public Page<Entity> search(@Filter Specification<Entity> spec, Pageable page) {
  return repo.findAll(spec, page);
}
```
> The repository should implement `JpaSpecificationExecutor` in order to execute Spring's Specification, `SimpleJpaRepository` is a well known implementation. You can remove the `Pageable` argument and return a `List` if pagination and sorting are not needed.
> 
### b. Specification
> Requires **javax.persistence-api**, **spring-data-jpa**, **spring-web**
```java
Specification<Entity> spec = new FilterSpecification<Entity>(query);
```

### c. Predicate
> Requires **javax.persistence-api**
```java
Predicate predicate = ExpressionGenerator.run(String query, Root<?> r, CriteriaQuery<?> q, CriteriaBuilder cb);
```

> :warning: **If you need to search over relations**, you also require **hibernate-core**

### d. Builder
```java
/* Using static methods */
import static com.turkraft.springfilter.FilterBuilder.*;
Filter filter = like("name", "%jose%");
String query = filter.generate(); // name ~ '%jose%'
// filter = Filter.from(query);
// Predicate predicate = ExpressionGenerator.run(filter, Root<?> r, CriteriaQuery<?> cq, CriteriaBuilder cb);
// Specification<Entity> spec = new FilterSpecification<Entity>(filter);
```

## Syntax

### Fields
Field names should be directly given without any extra literals. Dots indicate nested fields. For example: `category.updatedAt`

### Inputs
Numbers should be directly given. Booleans should also directly be given, valid values are `true` and `false`. Others such as strings, enums, dates, should be quoted. For example: `status : 'active'`

### Operators
<table>
  <tr> <th>Literal</th> <th>Description</th> <th>Example</th> </tr>
  <tr> <td>and</th> <td>and's two expressions</td> <td>status : 'active' <b>and</b> createdAt > '1-1-2000'</td> </tr>
  <tr> <td>or</th> <td>or's two expressions</td> <td>value ~ '%hello%' <b>or</b> name ~ '%world%'</td> </tr>
  <tr> <td>not</th> <td>not's an expression</td> <td> <b>not</b> (id > 100 or category.order is null) </td> </tr>
</table>

> You may prioritize operators using parentheses, for example: `x and (y or z)`

### Comparators
<table>
  <tr> <th>Literal</th> <th>Description</th> <th>Example</th> </tr>
  <tr> <td>~</th> <td>checks if the left (string) expression is similar to the right (string) expression</td> <td>catalog.name <b>~</b> 'electronic%'</td> </tr>
  <tr> <td>:</th> <td>checks if the left expression is equal to the right expression</td> <td>id <b>:</b> 5</td> </tr>
  <tr> <td>!</th> <td>checks if the left expression is not equal to the right expression</td> <td>username <b>!</b> 'torshid'</td> </tr>
  <tr> <td>></th> <td>checks if the left expression is greater than the right expression</td> <td>distance <b>></b> 100</td> </tr>
  <tr> <td>>:</th> <td>checks if the left expression is greater or equal to the right expression</td> <td>distance <b>>:</b> 100</td> </tr>
  <tr> <td><</th> <td>checks if the left expression is smaller than the right expression</td> <td>distance <b><</b> 100</td> </tr>
  <tr> <td><:</th> <td>checks if the left expression is smaller or equal to the right expression</td> <td>distance <b><:</b> 100</td> </tr>
  <tr> <td>is null</th> <td>checks if an expression is null</td> <td>status <b>is null</b></td> </tr>
  <tr> <td>is not null</th> <td>checks if an expression is not null</td> <td>status <b>is not null</b></td> </tr>
  <tr> <td>is empty</th> <td>checks if the (collection) expression is empty</td> <td>children <b>is empty</b></td> </tr>
  <tr> <td>is not empty</th> <td>checks if the (collection) expression is not empty</td> <td>children <b>is not empty</b></td> </tr>
  <tr> <td>in</th> <td>checks if an expression is present in the right expressions</td> <td>status <b>in (</b>'initialized'<b>,</b> 'active'<b>)</b></td> </tr>
</table>

> Note that the `*` character can also be used instead of `%` when using the `~` comparator. By default, this comparator is case insensitive, the behavior can be changed with `FilterParameters.CASE_SENSITIVE_LIKE_OPERATOR`.

### Functions
A function is characterized by its name (case insensitive) followed by parentheses. For example: `currentTime()`. Some functions might also take arguments, arguments are seperated with commas. For example: `min(ratings) > 3`
<table>
  <tr> <th>Name</th> <th>Description</th> <th>Example</th> </tr>
  <tr> <td> absolute </th> <td> returns the absolute </td> <td> <b>absolute(</b>x<b>)</b> </td> </tr>
  <tr> <td> average </th> <td> returns the average </td> <td> <b>average(</b>ratings<b>)</b> </td> </tr>
  <tr> <td> min </th> <td> returns the minimum </td> <td> <b>min(</b>ratings<b>)</b> </td> </tr>
  <tr> <td> max </th> <td> returns the maximum </td> <td> <b>max(</b>ratings<b>)</b> </td> </tr>
  <tr> <td> sum </th> <td> returns the sum </td> <td> <b>sum(</b>a<b>,</b> b<b>)</b>, <b>sum(</b>scores<b>)</b></td> </tr>
  <tr> <td> diff </th> <td> returns the difference </td> <td> <b>diff(</b>a<b>,</b> b<b>)</b> </td> </tr>
  <tr> <td> prod </th> <td> returns the product </td> <td> <b>prod(</b>a<b>,</b> b<b>)</b> </td> </tr>
  <tr> <td> quot </th> <td> returns the quotient </td> <td> <b>quot(</b>a<b>,</b> b<b>)</b> </td> </tr>
  <tr> <td> mod </th> <td> returns the modulus </td> <td> <b>mod(</b>a<b>,</b> b<b>)</b> </td> </tr>
  <tr> <td> sqrt </th> <td> returns the square root </td> <td> <b>sqrt(</b>a<b>)</b> </td> </tr>
  <tr> <td> currentDate </th> <td> returns the current date </td> <td> <b>currentDate()</b> </td> </tr>
  <tr> <td> currentTime </th> <td> returns the current time </td> <td> <b>currentTime()</b> </td> </tr>
  <tr> <td> currentTimestamp </th> <td> returns the current time stamp </td> <td> <b>currentTimestamp()</b> </td> </tr>
  <tr> <td> size </th> <td> returns the collection's size </td> <td> <b>size(</b>accidents<b>)</b> </td> </tr>
  <tr> <td> length </th> <td> returns the string's length </td> <td> <b>length(</b>name<b>)</b> </td> </tr>
  <tr> <td> trim </th> <td> returns the trimmed string </td> <td> <b>trim(</b>name<b>)</b> </td> </tr>
  <tr> <td> upper </th> <td> returns the uppercased string </td> <td> <b>upper(</b>name<b>)</b> </td> </tr>
  <tr> <td> lower </th> <td> returns the lowercased string </td> <td> <b>lower(</b>name<b>)</b> </td> </tr>
  <tr> <td> concat </th> <td> returns the concatenation of given strings </td> <td> <b>concat(</b>firstName<b>,</b> ' '<b>,</b> lastName<b>)</b> </td> </tr>
</table>

#### Subqueries
<table>
  <tr> <th>Name</th> <th>Description</th> <th>Example</th> <th>Explanation</th> </tr>
  <tr> <td> exists </th> <td> returns the existence of a subquery result </td> <td> <b>exists(</b>employees.age > 60<b>)</b> </td> <td> returns true if at least one employee's age is greater than 60</td> </tr>
</table>

## Configuration

### Date format
You are able to change the date format by setting the static formatters inside the `FilterParameters` class. You may see below the default patterns and how you can set them with properties:

<table>
  <tr> <th>Type</th> <th>Default Pattern</th> <th>Property Name</th> </tr>
  <tr> <td> java.util.Date </th> <td> dd-MM-yyyy </td> <td> turkraft.springfilter.dateformatter.pattern </td> </tr>
  <tr> <td> java.time.LocalDate </th> <td> dd-MM-yyyy </td> <td> turkraft.springfilter.localdateformatter.pattern </td> </tr>
  <tr> <td> java.time.LocalDateTime </th> <td> dd-MM-yyyy'T'HH:mm:ss </td> <td> turkraft.springfilter.localdatetimeformatter.pattern </td> </tr>
  <tr> <td> java.time.OffsetDateTime </th> <td> dd-MM-yyyy'T'HH:mm:ss.SSSXXX </td> <td> turkraft.springfilter.offsetdatetimeformatter.pattern </td> </tr>
  <tr> <td> java.time.LocalTime </th> <td> HH:mm:ss </td> <td> turkraft.springfilter.localtimeformatter.pattern </td> </tr>
  <tr> <td> java.time.Instant </th> <td> dd-MM-yyyy'T'HH:mm:ss.SSSXXX </td> <td> <i>Parses using DateTimeFormatter.ISO_INSTANT</i> </td> </tr>
</table>

## MongoDB
MongoDB is also partially supported as an alternative to JPA. The query input is compiled to a `Bson`/`Document` filter. You can then use it as you wish with `MongoTemplate` or `MongoOperations` for example. 

> Requires **spring-data-mongodb** 

> :warning: Functions are currently not supported with MongoDB, and the `~` operator actually uses the [regex](https://docs.mongodb.com/manual/reference/operator/query/regex/) operator.

### Usage
```java
@GetMapping(value = "/search")
public Page<Entity> search(@Filter(entityClass = Entity.class) Document doc, Pageable page) {
  // your repo may implement DocumentExecutor for easy usage
  return repo.findAll(doc, page); 
}
```
```java
Bson bson = BsonGenerator.run(Filter.from(query), Entity.class);
Document doc = BsonUtils.getDocumentFromBson(bson);
Query query = BsonUtils.getQueryFromDocument(doc);
// ...
```

### Parameters

#### Codec Registry
The codec registry can be customized using the `BsonGeneratorParameters.setCodecRegistry` method. You may also use the `CodecRegistryProvider` as follows:
```java
@Configuration
public class MongoDBCodecConfiguration {
  public MongoDBCodecConfiguration(CodecRegistryProvider codecRegistryProvider) {
    BsonGeneratorParameters.setCodecRegistry(codecRegistryProvider.getCodecRegistry());
  }
}
```

## JavaScript Query Builder

Instead of manually writing string queries in your frontend applications, you may use the [JavaScript query builder](https://github.com/sisimomo/Spring-Filter-Query-Builder) which is similar to the Java `FilterBuilder` class.

### Usage

```javascript
import { SpringFilterQueryBuilder as builder } from 'https://cdn.jsdelivr.net/npm/spring-filter-query-builder@0.3.0/src/index.min.js';
const filter =
    builder.or(
        builder.and(
            builder.equal("test.test1", "testvalue1"),
            builder.isNotNull("test.test2")
        ),
        builder.notEqual("test.test2", "testvalue2")
    );
const req = await fetch('http://api/person?filter=' + filter.toString());
```

## Angular Query Builder

Please see [documentation](https://github.com/68ociredef/spring-filter-ng).

## Customization
If you need to customize the behavior of the filter, the way to go is to extend the `FilterBaseVisitor` class, by taking `QueryGenerator` or `ExpressionGenerator` as examples. In order to also modify the query syntax, you should start by cloning the repository and editing the `Filter.g4` file. 

## Articles
* [Easily filter entities in your Spring API](https://torshid.medium.com/easily-filter-entities-in-your-spring-api-f433537cfd41)

## Contributing
Ideas and pull requests are always welcome. [Google's Java Style](https://github.com/google/styleguide/blob/gh-pages/eclipse-java-google-style.xml) is used for formatting.

## Contributors
* Thanks to [@marcopag90](https://github.com/marcopag90) and [@glodepa](https://github.com/glodepa) for adding support to MongoDB.
* Thanks to [@sisimomo](https://github.com/sisimomo) for creating the [JavaScript query builder](https://github.com/sisimomo/Spring-Filter-Query-Builder).
* Thanks to [@68ociredef](https://github.com/68ociredef) for creating the [Angular query builder](https://github.com/68ociredef/spring-filter-ng).

## License
Distributed under the [MIT license](LICENSE).
