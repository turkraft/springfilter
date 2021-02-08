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

## Usage

```java
// Coming very soon!
```

## Contributing
Ideas and pull requests are always welcome.

## License
Distributed under the [MIT license](LICENSE).
