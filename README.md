# Spring Filter

You need a way to dynamically filter entities without any effort? Just add me to your `pom.xml`.
Your API will gain a full featured search functionality. You don't work with APIs? No problem, you may still not want to mess with SQL, JPA predicates, security, and all of that I guess. From a technical point of view, I try to compile a simple syntax to JPA predicates.

## Example
> */search?filter=* (brand.name **:** 'audi' **or** brand.name : 'land rover') **and** (year **>** 2018 **or** km **<** 50000) and color **:** 'white' **and** accidents **is empty**

```java
// Entity used in the query above
@Entity public class Car {
  @Id long id;
      int year;
      int km;
  @Enumerated Color color;
  @ManyToOne Brand brand;
  @OneToMany List<Accident> accidents;
  // ...
}
```

> :rocket: Yes we support booleans, dates, enums, **and even searching over relations**! Need something else? [Tell us here](https://github.com/torshid/spring-filter/issues).



## Installation

```xml
<dependency>
    <groupId>com.turkraft</groupId>
    <artifactId>spring-filter</artifactId>
    <version>0.7.2</version>
</dependency>
```

## Usages

### a. Controller
> Requires **javax.persistence-api**, **spring-data-jpa**, **spring-web** and **spring-webmvc**
```java
@GetMapping(value = "/search")
public List<Entity> search(@EntityFilter Specification<Entity> spec) {
  return repo.findAll(spec);
}
```

### b. Specification
> Requires **javax.persistence-api**, **spring-data-jpa**, **spring-web**
```java
Specification<Entity> spec = new FilterSpecification<Entity>(input);
```

### c. Predicate
> Requires **javax.persistence-api**, **spring-data-jpa**
```java
Predicate predicate = FilterCompiler.parse(String input, Root<?> r, CriteriaQuery<?> q, CriteriaBuilder cb);
```

> :warning: **If you need to search over relations**, you also require **hibernate-core**

### d. Builder
```java
Filter filter = Filter.builder()
    .body(ConditionInfix.builder()
        .left(Field.builder()
            .name("name")
            .build())
        .comparator(Comparator.LIKE)
        .right(Input.builder()
            .value(Text.builder()
                .value("%jose%")
                .build())
            .build())
        .build())
    .build();
String input = filter.generate(); // name ~ 'jose'
Predicate predicate = filter.generate(Root<?> r, CriteriaQuery<?> cq, CriteriaBuilder cb);
Specification<Entity> spec = new FilterSpecification<Entity>(filter);
```

> :construction: The builder will change in the future in order to be simpler, it currently uses Lombok

## Syntax

### Operators
<table>
  <tr> <th>Literal (case insensitive)</th> <th>Description</th> <th>Example</th> </tr>
  <tr> <td>and</th> <td>and's two expressions</td> <td>status : 'active' <b>and</b> createdAt > 1-1-2000</td> </tr>
  <tr> <td>or</th> <td>or's two expressions</td> <td>value ~ 'hello' <b>or</b> name ~ 'world'</td> </tr>
  <tr> <td>not</th> <td>not's an expression</td> <td> <b>not</b> (id > 100 or category.order is null) </td> </tr>
</table>

> You may prioritize operators using parentheses, for example: `x and (y or z)`

### Comparators
<table>
  <tr> <th>Literal (case insensitive)</th> <th>Description</th> <th>Example</th> </tr>
  <tr> <td>~</th> <td>checks if the left (string) expression is similar to the right (string) expression</td> <td>catalog.name <b>~</b> 'electronic%'</td> </tr>
  <tr> <td>:</th> <td>checks if the left expression is equal to the right expression</td> <td>id <b>:</b> 5</td> </tr>
  <tr> <td>></th> <td>checks if the left expression is greater than the right expression</td> <td>distance <b>></b> 100</td> </tr>
  <tr> <td>>:</th> <td>checks if the left expression is greater or equal to the right expression</td> <td>distance <b>>:</b> 100</td> </tr>
  <tr> <td><</th> <td>checks if the left expression is smaller than the right expression</td> <td>distance <b><</b> 100</td> </tr>
  <tr> <td><:</th> <td>checks if the left expression is smaller or equal to the right expression</td> <td>distance <b><:</b> 100</td> </tr>
  <tr> <td>is null</th> <td>checks if an expression is null</td> <td>status <b>is null</b></td> </tr>
  <tr> <td>is not null</th> <td>checks if an expression is not null</td> <td>status <b>is not null</b></td> </tr>
  <tr> <td>is empty</th> <td>checks if the (collection) expression is empty</td> <td>children <b>is empty</b></td> </tr>
  <tr> <td>is not empty</th> <td>checks if the (collection) expression is not empty</td> <td>children <b>is not empty</b></td> </tr>
</table>

## Contributing
Ideas and pull requests are always welcome.

## License
Distributed under the [MIT license](LICENSE).
