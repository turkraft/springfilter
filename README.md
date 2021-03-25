# [Spring Filter](https://spring-filter.herokuapp.com/)

You need a way to dynamically filter entities without any effort? Just add me to your `pom.xml`.
Your API will gain a full featured search functionality. You don't work with APIs? No problem, you may still not want to mess with SQL, JPA predicates, security, and all of that I guess. From a technical point of view, I compile a simple syntax to JPA predicates.

## Example
*/search?filter=* **average**(ratings) **>** 4.5 **and** brand.name **in** ('audi', 'land rover') **and** (year **>** 2018 **or** km **<** 50000) and color **:** 'white' **and** accidents **is empty**

> You may also apply sorting using `sort`, for example: `&sort=name,-brand.id` (where `-` means descending)

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
    <version>0.9.1</version>
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
/* Using static methods */
import static com.turkraft.springfilter.FilterQueryBuilder.*;
Filter filter = filter(like("name", "%jose%"));
```
```java
/* Using lombok builder */
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
```
```java
String input = filter.generate(); // name ~ '%jose%'
Predicate predicate = filter.generate(Root<?> r, CriteriaQuery<?> cq, CriteriaBuilder cb);
Specification<Entity> spec = new FilterSpecification<Entity>(filter);
```

## Syntax

### Fields
Field names should be directly given without any extra literals. Dots indicate nested fields. For example: `category.updatedAt`

### Inputs
Numbers should be directly given. Booleans should also directly be given, valid values are `true` and `false` (case insensitive). Others such as strings, enums, dates, should be quoted. For example: `status : 'active'`

### Operators
<table>
  <tr> <th>Literal (case insensitive)</th> <th>Description</th> <th>Example</th> </tr>
  <tr> <td>and</th> <td>and's two expressions</td> <td>status : 'active' <b>and</b> createdAt > '1-1-2000'</td> </tr>
  <tr> <td>or</th> <td>or's two expressions</td> <td>value ~ 'hello' <b>or</b> name ~ 'world'</td> </tr>
  <tr> <td>not</th> <td>not's an expression</td> <td> <b>not</b> (id > 100 or category.order is null) </td> </tr>
</table>

> You may prioritize operators using parentheses, for example: `x and (y or z)`

### Comparators
<table>
  <tr> <th>Literal (case insensitive)</th> <th>Description</th> <th>Example</th> </tr>
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

### Functions
A function is characterized by its name (case insensitive) followed by parentheses. For example: `currentTime()`. Some functions might also take arguments, arguments are seperated with commas. For example: `min(ratings) > 3`
<table>
  <tr> <th>Name</th> <th>Description</th> <th>Example</th> </tr>
  <tr> <td> absolute </th> <td> returns the absolute </td> <td> <b>absolute(</b>x<b>)</b> </td> </tr>
  <tr> <td> average </th> <td> returns the average </td> <td> <b>average(</b>ratings<b>)</b> </td> </tr>
  <tr> <td> min </th> <td> returns the minimum </td> <td> <b>min(</b>ratings<b>)</b> </td> </tr>
  <tr> <td> max </th> <td> returns the maximum </td> <td> <b>max(</b>ratings<b>)</b> </td> </tr>
  <tr> <td> sum </th> <td> returns the sum </td> <td> <b>sum(</b>scores<b>)</b> </td> </tr>
  <tr> <td> currentDate </th> <td> returns the current date </td> <td> <b>currentDate()</b> </td> </tr>
  <tr> <td> currentTime </th> <td> returns the current time </td> <td> <b>currentTime()</b> </td> </tr>
  <tr> <td> currentTimestamp </th> <td> returns the current time stamp </td> <td> <b>currentTimestamp()</b> </td> </tr>
  <tr> <td> size </th> <td> returns the collection's size </td> <td> <b>size(</b>accidents<b>)</b> </td> </tr>
  <tr> <td> length </th> <td> returns the string's length </td> <td> <b>length(</b>name<b>)</b> </td> </tr>
  <tr> <td> trim </th> <td> returns the trimmed string </td> <td> <b>trim(</b>name<b>)</b> </td> </tr>
</table>

## Configuration
You may want to customize the behavior of the different processes taking place. For now, you can only change the date format but advanced customization will be soon available in order to let you completely personalize the tokenizer, the parser, the query builder, with the possibility of adding custom functions and much more.

### Date format
You are able to change the date format by setting the static `DATE_FORMATTER` field of the `FilterConfig` class. You can also set it with the property `turkraft.springfilter.dateformatter.pattern`

## Contributing
Ideas and pull requests are always welcome.

## License
Distributed under the [MIT license](LICENSE).
