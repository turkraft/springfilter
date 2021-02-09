# Spring Filter

You need a way to dynamically filter entities without any effort? Just add me to your `pom.xml`.
Your API will gain a full featured search functionality. You don't work with APIs? No problem, you may still not want to mess with SQL, JPA predicates, security, and all of that I guess.

## Example
*?search=* (brand.name **:** audi **or** brand.name : 'land rover') **and** (year **>** 2018 **or** km **<** 50000) and color **:** white **and** accidents **is empty**

```java

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

Yes we support booleans, dates, enums, **and even searching over relations**! Need something else? [Tell us here](https://github.com/torshid/spring-filter/issues).



## Installation

```xml
<dependency>
    <groupId>com.torshid</groupId>
    <artifactId>spring-filter</artifactId>
    <version>1.0.0</version>
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

:warning: ** If you need to search over relations** you also require **hibernate-core**

## Syntax

### Operators
<table>
  <tr> <th>Literal (case insensitive)</th> <th>Type</th> <th>Description</th> <th>Example</th> </tr>
  <tr> <td>and</th> <td>infix</td> <td>and's two expressions</td> <td>status : active <b>and</b> createdAt > 1-1-2000</td> </tr>
  <tr> <td>or</th> <td>infix</td> <td>or's two expressions</td> <td>value ~ 'hello' <b>or</b> name ~ 'world'</td> </tr>
  <tr> <td>not</th> <td>prefix</td> <td>not's an expression</td> <td> <b>not</b> (id > 100 or category.order is null) </td> </tr>
</table>

## Contributing
Ideas and pull requests are always welcome.

## License
Distributed under the [MIT license](LICENSE).
