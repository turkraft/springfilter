# Spring Filter

<p align="center">
  <a href="https://github.com/turkraft/springfilter">
    <img src="https://raw.githubusercontent.com/turkraft/springfilter/main/.github/logo.png?raw=true" alt="Spring Filter Logo">
  </a>
</p>

Are you looking for an effortless way to dynamically filter entities? Look no further than Spring Filter. With Spring Filter, your API will benefit from a comprehensive search functionality. Even if you don't have a web API, you can still leverage the powerful filter builder to generate complex SQL or Mongo queries.

The library's modular design and seamless integration with Spring make it easy to extend with custom operators and functions, or even integrate it into a different platform. Say goodbye to the headache of generating valid queries in frontend applications, as JavaScript filter builders are also available to simplify the process.

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

> :rocket: Yes we support booleans, dates, enums, functions, **and even relations**! Need something else? [Tell us here](https://github.com/turkraft/spring-filter/issues).

## [Sponsors](https://github.com/sponsors/torshid)

Sponsor our project and gain the advantage of having your issues prioritized for prompt resolution.

<table>
<tr>
<td><a href="https://github.com/ixorbv"><img width="64" src="https://avatars.githubusercontent.com/u/127401397?v=4"/></a></td>
</tr>
</table>

## Integrations and Usages

### JPA Integration

```xml
<dependency>
  <groupId>com.turkraft.springfilter</groupId>
  <artifactId>jpa</artifactId>
  <version>3.1.8</version>
</dependency>
```

```java
@GetMapping(value = "/search")
Page<Entity> search(@Filter Specification<Entity> spec, Pageable page) {
    return repository.findAll(spec, page);
}
```

The repository should implement `JpaSpecificationExecutor` in order to execute Spring's Specification, `SimpleJpaRepository` is a well known implementation. You can remove the `Pageable` argument and return a `List` if pagination and sorting are not needed.

### Mongo Integration

```xml
<dependency>
  <groupId>com.turkraft.springfilter</groupId>
  <artifactId>mongo</artifactId>
  <version>3.1.8</version>
</dependency>
```

```java
@GetMapping(value = "/search")
Page<Entity> search(@Filter(entityClass = Entity.class) Query query, Pageable page) {
    return mongoTemplate.find(query.with(pageable), Entity.class);
}
```

#### Using MongoRepository

```java
public interface EntityRepository extends MongoRepository<Entity, String> {
  @Query("?0")
  List<Employee> findAll(Document document);
  @Query("?0")
  Page<Employee> findAll(Document document, Pageable pageable);
}

@GetMapping(value = "/search")
Page<Entity> search(@Filter(entityClass = Entity.class) Document document, Pageable page) {
    return entityRepository.findAll(query, page);
}
```

### Filter Builder

```xml
<dependency>
  <groupId>com.turkraft.springfilter</groupId>
  <artifactId>core</artifactId>
  <version>3.1.8</version>
</dependency>
```

```java
@Autowired FilterBuilder fb;
FilterNode filter = fb.field("year").equal(fb.input(2023)).and(fb.isNull(fb.field("category"))).get();

@Autowired ConversionService cs;
String query = cs.convert(filter, String.class); // year : 2023 and category is null
```

Please note that Spring's `ConversionService` is internally used when converting objects to strings and vice-versa.
Spring Filter does not enforce any pattern for dates and other types.
Customization should be done directly within Spring if required.

### JavaScript Query Builder

Instead of manually writing string queries in your frontend applications, you may use
the [JavaScript query builder](https://github.com/sisimomo/Spring-Filter-Query-Builder).

```javascript
import { sfAnd, sfEqual, sfGt, sfIsNull, sfLike, sfNot, sfOr } from 'spring-filter-query-builder';

const filter = sfAnd([
  sfAnd([sfEqual('status', 'active'), sfGt('createdAt', '1-1-2000')]),
  sfOr([sfLike('value', '*hello*'), sfLike('name', '*world*')]),
  sfNot(sfOr([sfGt('id', 100), sfIsNull('category.order')])),
]);

const req = await fetch('http://api/person?filter=' + filter.toString());
```

### Angular Query Builder

Please see [documentation](https://github.com/68ociredef/spring-filter-ng).

## Syntax

### Fields

`field`, `field.nestedField`

### Inputs

`123`, `-321.123`, `true`, `false`, `'hello world'`, `'escape \' quote'`, `'1-01-2023'`

### Collections

`[1, 2, 3]`, `[field, ['x', 'y'], 99]`

### Functions

`f()`, `f(x)`, `f(x, y)`

### Placeholders

`` `place_holder` ``

### Priority

`x and (y or z)`

### Prefix Operators

`op expr`, `not ready`

### Infix Operators

`expr op expr`, `x and y`

### Postfix Operators

`expr op`, `field is null`

## Common Language

Below are listed the operators and functions which are supported by all integrations (JPA and Mongo).
Integrations may extend this common language.

### Logical Operators

<table>
  <tr> <th>Literal</th> <th>Description</th> <th>Example</th> </tr>
  <tr> <td>and</th> <td>and's two expressions</td> <td>status : 'active' <b>and</b> createdAt > '1-1-2000'</td> </tr>
  <tr> <td>or</th> <td>or's two expressions</td> <td>value ~ '*hello*' <b>or</b> name ~ '*world*'</td> </tr>
  <tr> <td>not</th> <td>not's an expression</td> <td> <b>not</b> (id > 100 or category.order is null) </td> </tr>
</table>

### Value Comparators

<table>
  <tr> <th>Literal</th> <th>Description</th> <th>Example</th> </tr>
  <tr> <td>~</th> <td>checks if the left (string) expression is similar to the right (string) expression</td> <td>catalog.name <b>~</b> '*electronic*'</td> </tr>
  <tr> <td>~~</th> <td>similar to the previous operator but case insensitive</td> <td>catalog.name <b>~~</b> 'ElEcTroNic*'</td> </tr>
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
  <tr> <td>in</th> <td>checks if an expression is present in the right expressions</td> <td>status <b>in [</b>'initialized'<b>,</b> 'active'<b>]</b></td> </tr>
  <tr> <td>not in</th> <td>checks if an expression is not present in the right expressions</td> <td>status <b>not in [</b>'failed'<b>,</b> 'closed'<b>]</b></td> </tr>
</table>

### Functions

<table>
  <tr> <th>Name</th> <th>Description</th> <th>Example</th> </tr>
  <tr> <td> size </th> <td> returns the collection's size </td> <td> <b>size(</b>accidents<b>)</b> </td> </tr>
</table>

## Articles

* [Easily filter entities in your Spring API](https://torshid.medium.com/easily-filter-entities-in-your-spring-api-f433537cfd41)

## Contributing

Ideas and pull requests are always welcome.
[Google's Java Style](https://github.com/google/styleguide/blob/gh-pages/eclipse-java-google-style.xml)
is used for formatting.

## Contributors

* Thanks to [@marcopag90](https://github.com/marcopag90) and [@glodepa](https://github.com/glodepa)
  for adding support to MongoDB.
* Thanks to [@sisimomo](https://github.com/sisimomo) for creating
  the [JavaScript query builder](https://github.com/sisimomo/Spring-Filter-Query-Builder).
* Thanks to [@68ociredef](https://github.com/68ociredef) for creating
  the [Angular query builder](https://github.com/68ociredef/spring-filter-ng).

## License

Distributed under the [MIT license](LICENSE).
