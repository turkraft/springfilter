# Spring Filter

<p align="center">
  <a href="https://github.com/turkraft/springfilter">
    <img src="https://raw.githubusercontent.com/turkraft/springfilter/main/.github/logo.png?raw=true" alt="Spring Filter Logo">
  </a>
</p>

<p align="center">
  <a href="https://central.sonatype.com/artifact/com.turkraft.springfilter/core"><img src="https://img.shields.io/maven-central/v/com.turkraft.springfilter/core?label=Maven%20Central" alt="Maven Central"></a>
  <a href="https://github.com/turkraft/springfilter/actions/workflows/maven.yml"><img src="https://github.com/turkraft/springfilter/actions/workflows/maven.yml/badge.svg" alt="Build"></a>
  <a href="LICENSE"><img src="https://img.shields.io/github/license/turkraft/springfilter" alt="License"></a>
  <a href="https://github.com/turkraft/springfilter"><img src="https://img.shields.io/badge/Java-17+-blue" alt="Java 17+"></a>
</p>

> :us: [English](README.md) | :cn: [中文](README.zh-CN.md) | :jp: [日本語](README.ja.md) | :tr: [Türkçe](README.tr.md)

Filtrado dinámico de consultas para aplicaciones Spring. Pasa expresiones de filtro como parámetros URL y aplícalas a repositorios JPA, colecciones MongoDB u objetos Java en memoria.

La librería analiza las expresiones de filtro en árboles de sintaxis abstracta y luego los convierte en consultas JPA Criteria, consultas MongoDB o Predicates de Java según el módulo que uses. También puedes usar el filtro programático para construir consultas mediante código.

> ¡Ahora compatible con Spring Boot 4! ¿Buscas versiones anteriores? Consulta las ramas [2.x.x](https://github.com/turkraft/springfilter/tree/2.x.x) / [3.x.x](https://github.com/turkraft/springfilter/tree/3.x.x).

## Ecosistema

También disponible para JavaScript y TypeScript:

| Paquete | Descripción |
|---|---|
| [FilterKit](https://github.com/turkraft/filterkit) | Librería principal — filtra arrays, construye consultas, analiza expresiones |
| [FilterKit TanStack](https://github.com/turkraft/filterkit-tanstack) | Filtros de columna TanStack Table → Spring Filter |
| [FilterKit QueryBuilder](https://github.com/turkraft/filterkit-querybuilder) | Consultas react-querybuilder → Spring Filter |
| [FilterKit Prisma](https://github.com/turkraft/filterkit-prisma) | Expresiones de filtro → cláusulas where de Prisma |
| [FilterKit Drizzle](https://github.com/turkraft/filterkit-drizzle) | Expresiones de filtro → cláusulas where de Drizzle |

## Ejemplo ([pruébalo en vivo](https://springfilter-jpa.onrender.com/))

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

La librería maneja booleanos, fechas, enumerados, funciones y relaciones entre entidades. El módulo JPA genera consultas Criteria, el módulo MongoDB genera pipelines de agregación y el módulo Predicate filtra objetos en memoria.

## [Patrocinadores](https://github.com/sponsors/torshid)

Patrocina nuestro proyecto y recibe prioridad en tus issues.

<table>
<tr>
<td align="center"><a href="https://github.com/ixorbv"><img width="64" src="https://avatars.githubusercontent.com/u/127401397?v=4"/><br/>ixorbv</a></td>
<td align="center"><a href="https://github.com/marcopag90"><img width="64" src="https://avatars.githubusercontent.com/marcopag90"/><br/>marcopag90</a></td>
</tr>
</table>

## Módulos

### JPA

Filtra entidades JPA directamente en consultas de base de datos. El módulo convierte expresiones de filtro en Specifications de JPA Criteria API.

```xml
<dependency>
  <groupId>com.turkraft.springfilter</groupId>
  <artifactId>jpa</artifactId>
  <version>4.0.5</version>
</dependency>
```

```java
@GetMapping("/cars")
Page<Car> search(@Filter Specification<Car> spec, Pageable page) {
    return repository.findAll(spec, page);
}
```

El repositorio debe implementar `JpaSpecificationExecutor`. `SimpleJpaRepository` es una implementación estándar. Elimina el argumento `Pageable` si no necesitas paginación.

#### JPA con consultas nativas

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

#### JPA con proyecciones

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

Filtra documentos MongoDB usando consultas de Spring Data MongoDB.

```xml
<dependency>
  <groupId>com.turkraft.springfilter</groupId>
  <artifactId>mongo</artifactId>
  <version>4.0.5</version>
</dependency>
```

```java
@GetMapping("/cars")
Page<Car> search(@Filter(entityClass = Car.class) Query query, Pageable page) {
    return mongoTemplate.find(query.with(page), Car.class);
}
```

#### Con MongoRepository

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

Filtra colecciones en memoria usando Predicates de Java. Funciona con cualquier POJO, sin necesidad de base de datos.

```xml
<dependency>
  <groupId>com.turkraft.springfilter</groupId>
  <artifactId>predicate</artifactId>
  <version>4.0.5</version>
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

#### Conversión manual

```java
@Autowired FilterPredicateConverter converter;

public List<Car> filterCars(List<Car> cars, String filterExpression) {
    FilterPredicate<Car> predicate = converter.convert(filterExpression, Car.class);

    return cars.stream()
        .filter(predicate)
        .collect(Collectors.toList());
}
```

#### Casos de uso

El módulo Predicate es útil cuando:
- Filtros de datos en caché en memoria
- Filtros de respuestas API antes de devolver al cliente
- Pruebas de lógica de filtro sin base de datos
- Filtros de objetos de configuración o enumerados
- Procesamiento de datos por lotes en memoria

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

#### Soporte para la función size

```java
// Filtrar por tamaño de colección
GET /cars?filter=size(accidents) > 2
GET /owners?filter=size(cars) : 0
```

El módulo Predicate soporta todos los operadores estándar y la función `size()` para colecciones, arrays, mapas y strings.

### Filter Builder (Constructor de filtros)

Construye expresiones de filtro programáticamente en lugar de escribir manualmente cadenas de filtro.

```xml
<dependency>
  <groupId>com.turkraft.springfilter</groupId>
  <artifactId>core</artifactId>
  <version>4.0.5</version>
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

#### Consultas complejas

```java
FilterNode filter = fb.field("brand.name").in(
    fb.collection(fb.input("audi"), fb.input("bmw"))
).and(
    fb.field("year").greaterThan(fb.input(2020))
        .or(fb.field("km").lessThan(fb.input(50000)))
).get();
```

#### Con funciones

```java
@Autowired SizeFunction sizeFunction;

FilterNode filter = fb.function(sizeFunction, fb.field("accidents"))
    .greaterThan(fb.input(2))
    .and(fb.field("year").lessThan(fb.input(2015)))
    .get();
```

### Constructor tipado de filtros

Genera constructores de filtros tipados en tiempo de compilación a partir de tus entidades JPA. Autocompletado del IDE en cada campo, comparaciones con seguridad de tipos y protección ante refactorizaciones.

```xml
<dependency>
  <groupId>com.turkraft.springfilter</groupId>
  <artifactId>typesafe</artifactId>
  <version>4.0.5</version>
</dependency>
```

Añade el procesador de anotaciones a tu plugin de compilador:

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-compiler-plugin</artifactId>
  <version>3.11.0</version>
  <configuration>
    <annotationProcessorPaths>
      <path>
        <groupId>com.turkraft.springfilter</groupId>
        <artifactId>typesafe-processor</artifactId>
        <version>4.0.5</version>
      </path>
    </annotationProcessorPaths>
  </configuration>
</plugin>
```

Anota tu entidad:

```java
@Entity
@Filterable
public class Car {
    private int year;
    private String model;
    private double price;
    private boolean active;
    @ManyToOne private Brand brand;
}
```

En tiempo de compilación, se genera `CarFilter`. Úsalo:

```java
@Autowired FilterBuilder fb;

FilterNode f = CarFilter.where(fb)
    .year().between(2020, 2025)
    .and()
    .model().startsWith("Audi")
    .and()
    .price().greaterThan(100.0)
    .and()
    .active().isTrue()
    .build();

// Produce: year between 2020 and 2025 and model ~ 'Audi%' and price > 100.0 and active : true
```

Tipos de campo soportados: `int`, `long`, `double`, `boolean`, `String`, `Date`, `LocalDate`, `LocalDateTime`, enumerados, colecciones. Soporta encadenamiento `and()` / `or()` con precedencia correcta de operadores. Los campos anotados con `@Transient` o `@JsonIgnore` se omiten. Los campos heredados de superclases se incluyen.

## OpenAPI/Swagger

Añade documentación Swagger automática para endpoints con parámetros `@Filter`.

```xml
<dependency>
  <groupId>com.turkraft.springfilter</groupId>
  <artifactId>openapi</artifactId>
  <version>4.0.5</version>
</dependency>
```

Solo añade la dependencia. Swagger UI muestra automáticamente:
- Todos los campos filtrables con sus tipos
- Relaciones anidadas
- Valores de enumerados
- Ejemplos de consultas
- Referencia de operadores
- Funciones disponibles

Compatible con los módulos JPA, MongoDB y Predicate.

## Paginación, ordenación y selección de campos

El módulo `page-sort` proporciona anotaciones para paginación, ordenación y selección de campos.

```xml
<dependency>
  <groupId>com.turkraft.springfilter</groupId>
  <artifactId>page-sort</artifactId>
  <version>4.0.5</version>
</dependency>
```

### Uso básico

```java
@GetMapping("/cars")
Page<Car> search(@Filter Specification<Car> spec, @Pagination Pageable page) {
    return repository.findAll(spec, page);
}
```

Uso: `?page=0&size=20&sort=-year` (prefijo `-` para descendente)

### Nombres de parámetros personalizados

```java
@GetMapping("/cars")
Page<Car> search(
    @Pagination(pageParameter = "p", sizeParameter = "limit", sortParameter = "order") Pageable page) {
    return repository.findAll(page);
}
```

Ahora usa `?p=0&limit=50&order=-year`

### Parámetro Sort

```java
@GetMapping("/cars")
List<Car> search(@Sort org.springframework.data.domain.Sort sort) {
    return repository.findAll(sort);
}
```

Usa `?sort=-year` o `?sort=-year,name`

### Selección de campos

```java
@Fields
@GetMapping("/cars")
List<Car> search() {
    return repository.findAll();
}
```

Usa `?fields=id,brand.name,year` para devolver solo los campos especificados. Utiliza el filtrado de Jackson internamente.

```java
// Incluir campos específicos
?fields= id,name,email

// Excluir campos
?fields= *,-password,-ssn

// Campos anidados
?fields= id,brand.name,brand.country

// Comodines
?fields= user.*
```

#### Ámbito raíz

Usa `root` para delimitar la selección de campos dentro de una ruta contenedora. Útil para respuestas paginadas donde quieres filtrar campos dentro de `content` mientras siempre incluyes los metadatos de paginación (`totalElements`, `totalPages`):

```java
@Fields(root = "content")
@GetMapping("/cars")
Page<Car> search() {
    return repository.findAll(pageable);
}
```

Ahora `?fields=id,brand.name,year` solo afecta a las propiedades dentro de `content`. Los campos de metadatos fuera de `content` siempre se incluyen en la respuesta.

### Ejemplo combinado

```java
@Fields
@GetMapping("/cars")
Page<Car> search(
    @Filter Specification<Car> spec,
    @Pagination Pageable page) {
    return repository.findAll(spec, page);
}
```

Usa todas las funcionalidades juntas:
```
/cars?filter=year>2020&page=0&size=20&sort=-year&fields=id,brand.name,year
```

El módulo `openapi` genera automáticamente documentación para estos parámetros cuando ambas dependencias están presentes.

## Integración frontend

Usa [FilterKit](https://github.com/turkraft/filterkit) para construir expresiones de filtro en JavaScript/TypeScript. Comparte exactamente la misma sintaxis de expresión y AST que Spring Filter.

```ts
import { build, stringify } from '@turkraft/filterkit';

const query = build()
  .field('year').between(2020, 2025)
  .and(build().field('brand.name').in(['audi', 'bmw']))
  .get();

fetch(`/api/cars?filter=${encodeURIComponent(stringify(query))}`);
```

FilterKit también ofrece integraciones React para [TanStack Table](https://github.com/turkraft/filterkit-tanstack) y [react-querybuilder](https://github.com/turkraft/filterkit-querybuilder).

### Proyectos de la comunidad

- [spring-filter-query-builder](https://github.com/sisimomo/Spring-Filter-Query-Builder) — Constructor de consultas JavaScript
- [spring-filter-ng](https://github.com/68ociredef/spring-filter-ng) — Integración Angular

## Sintaxis del lenguaje

### Acceso a campos

```
field
field.nested
field.nested.deep
```

### Literales

```
123                 // entero
-321.123            // decimal
true, false         // booleano
'text'              // string
'1-01-2023'         // fecha (formato depende de Spring ConversionService)
'escape \' quote'   // string escapado
```

### Colecciones

```
[1, 2, 3]
['a', 'b', 'c']
[field, nested.field, 'literal']
[field, ['nested', 'array'], 99]
```

### Funciones

```
size(collection)
size(field.collection)
today()
jsonText(field, 'key')
jsonText(field, 'key1', 'key2', ...)
```

### Placeholders

```
`placeholder_name`
```

Los placeholders son resueltos por procesadores de placeholder personalizados que implementes.

### Operadores

```
a and b              // and lógico
a or b               // or lógico
a xor b              // xor lógico
not a                // not lógico
a : b                // igual
a ! b                // distinto
a > b                // mayor que
a >: b               // mayor o igual
a < b                // menor que
a <: b               // menor o igual
a between x and y    // entre (rango inclusivo)
a ~ 'pattern'        // like (comodines % y _)
a ~~ 'pattern'       // like sin distinción de mayúsculas
a in [x, y]          // en colección
a not in [x, y]      // no en colección
a is null            // comprobación de nulo
a is not null        // comprobación de no nulo
a is empty           // comprobación de vacío (colecciones/strings)
a is not empty       // comprobación de no vacío
```

### Precedencia

Usa paréntesis para controlar el orden de evaluación:

```
a and (b or c)
(status : 'active' or status : 'pending') and year > 2020
```

## Ejemplos de consultas

### Filtrado básico

```
// Igualdad simple
?filter= status : 'active'

// Múltiples condiciones
?filter= year > 2020 and km < 50000

// Rango (inclusivo)
?filter= year between 2020 and 2025

// Condiciones OR
?filter= color : 'red' or color : 'blue'
```

### Trabajo con relaciones

```
// Filtrar por campo de entidad relacionada
?filter= brand.name : 'audi'

// Relaciones anidadas
?filter= brand.manufacturer.country : 'germany'

// Comprobar si la relación existe
?filter= brand is not null

// Múltiples condiciones de relación
?filter= brand.name : 'audi' and dealer.city : 'berlin'
```

### Operaciones con colecciones

```
// Comprobar si el valor está en una lista
?filter= status in ['active', 'pending', 'review']

// Comprobar tamaño de colección
?filter= size(accidents) > 2

// Comprobar si la colección está vacía
?filter= accidents is empty

// Comprobar si la colección no está vacía
?filter= ratings is not empty
```

### Coincidencia de strings

```
// Like con comodines (% = cualquier carácter, _ = un carácter)
?filter= name ~ '%john%'

// Like sin distinción de mayúsculas
?filter= name ~~ 'JOHN'

// Empieza por
?filter= email ~ 'admin%'

// Termina por
?filter= filename ~ '%.pdf'

// Coincidencia de patrones
?filter= code ~ 'PRD-____-2023'

// Múltiples patrones
?filter= name ~ ['%john%', '%doe%']

// Múltiples patrones sin distinción de mayúsculas
?filter= name ~~ ['%JOHN%', '%DOE%']
```

### Filtrado por fecha

```
// Comparación de fechas (formato depende de la configuración de Spring)
?filter= createdAt > '2023-01-01'

// Rango de fechas
?filter= createdAt > '2023-01-01' and createdAt < '2023-12-31'

// Rango de fechas (between)
?filter= createdAt between '2023-01-01' and '2023-12-31'

// Fechas relativas con la función today()
?filter= createdAt > today()
```

### Consultas complejas

```
// Condiciones anidadas con precedencia
?filter= (year > 2020 and km < 30000) or (year > 2018 and km < 10000)

// Mezcla de diferentes operadores
?filter= brand.name in ['audi', 'bmw'] and year > 2020 and accidents is empty and color ! 'white'

// Tamaño de colección con relaciones
?filter= size(owner.vehicles) > 3 and status : 'active'
```

### Comprobaciones de nulo y vacío

```
// Comprobar nulo
?filter= deletedAt is null

// Comprobar no nulo
?filter= description is not null

// Colección vacía
?filter= tags is empty

// Colección no vacía
?filter= children is not empty
```

### Filtrado JSON/JSONB

```
// Extraer valor string por ruta de claves
?filter= jsonText(metadata, 'address', 'city') : 'New York'

// Verificar si el valor existe
?filter= jsonText(data, 'status') in ['active', 'pending']

// Combinar con conversión de tipo
?filter= jsonText(data, 'age') > 18

// Claves anidadas con coincidencia de patrones
?filter= jsonText(metadata, 'address', 'city') ~ '%York%'
```

## Tipos soportados

Todos los módulos manejan:
- Primitivos (int, long, double, boolean, etc.)
- Strings
- Enumerados
- Fechas (LocalDate, LocalDateTime, Date, Instant, etc.)
- Colecciones (List, Set)
- Arrays
- Relaciones de entidad (@ManyToOne, @OneToMany, @ManyToMany)

El análisis de fechas usa `ConversionService` de Spring. Configúralo para cambiar los formatos de fecha.

## Operadores personalizados

Define operadores personalizados extendiendo `FilterInfixOperator`, `FilterPrefixOperator` o `FilterPostfixOperator`:

```java
@Component
public class ContainsOperator extends FilterInfixOperator {
    public ContainsOperator() {
        super("contains", 5);
    }
}
```

Luego implementa procesadores para cada módulo que uses:

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
        // Implementación
    }
}
```

Registra el operador con autoconfiguración o configuración manual.

## Funciones personalizadas

Define funciones personalizadas extendiendo `FilterFunction`:

```java
@Component
public class LowerFunction extends FilterFunction {
    public LowerFunction() {
        super("lower");
    }
}
```

Implementa procesadores para cada módulo:

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

## Configuración

### Nombre de parámetro por defecto

Por defecto, los filtros se leen del parámetro de consulta `filter`. Sobrescríbelo:

```java
@GetMapping("/cars")
List<Car> search(@Filter(parameter = "q") Specification<Car> spec) {
    return repository.findAll(spec);
}
```

Ahora usa `?q=year > 2020` en lugar de `?filter=year > 2020`.

### Clase de entidad para MongoDB

MongoDB requiere especificación explícita de la clase de entidad:

```java
@GetMapping("/cars")
List<Car> search(@Filter(entityClass = Car.class) Query query) {
    return mongoTemplate.find(query, Car.class);
}
```

### Filtros opcionales

Usa `Optional` para manejar parámetros de filtro faltantes:

```java
@GetMapping("/cars")
List<Car> search(@Filter Optional<Specification<Car>> spec) {
    return repository.findAll(spec.orElse(null));
}
```

### Aplicación programática de filtros

Aplica filtros sin anotaciones Spring MVC:

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

## Configuración de Spring

Spring Filter usa `ConversionService` de Spring para conversiones de tipo. Configúralo para personalizar formatos de fecha, manejo de enumerados, etc.:

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

## Avanzado: ParseContext

`ParseContext` te permite interceptar y modificar expresiones de filtro durante el análisis. Proporciona dos hooks: mapeo de campos y mapeo de nodos.

### Alias de campos

Mapea nombres de campo de la API a nombres de campo de la base de datos:

```java
@Service
public class ProductService {

    @Autowired FilterParser parser;
    @Autowired FilterSpecificationConverter converter;

    public List<Product> search(String filter) {
        ParseContext ctx = new ParseContextImpl(field -> {
            return switch (field) {
                case "price" -> "unitPrice";
                case "category" -> "productCategory.name";
                case "inStock" -> "inventory.quantity";
                default -> field;
            };
        }, null);

        FilterNode node = parser.parse(filter, ctx);
        Specification<Product> spec = converter.convert(node);
        return repository.findAll(spec);
    }
}
```

Ahora consultas como `?filter=price > 100` se convierten automáticamente en `unitPrice > 100` a nivel de base de datos.

### Multi-tenancy

Inyecta automáticamente filtros de tenant en todas las consultas:

```java
@Service
public class TenantAwareFilterService {

    @Autowired FilterParser parser;
    @Autowired FilterSpecificationConverter converter;
    @Autowired FilterBuilder fb;

    public <T> Specification<T> parse(String filter, Long tenantId) {
        ParseContext ctx = new ParseContextImpl(null, userNode -> {
            FilterNode tenantFilter = fb.field("tenantId").equal(fb.input(tenantId)).get();
            return fb.and(tenantFilter, userNode).get();
        });

        FilterNode node = parser.parse(filter, ctx);
        return converter.convert(node);
    }
}
```

```java
@GetMapping("/products")
Page<Product> search(@Filter String filter, @AuthenticationPrincipal User user) {
    Specification<Product> spec = tenantService.parse(filter, user.getTenantId());
    return repository.findAll(spec, pageable);
}
```

El usuario consulta `status : 'active'` pero la consulta real se convierte en `tenantId : 123 and status : 'active'`.

### Filtros de seguridad

Inyecta filtros de seguridad a nivel de fila basados en permisos del usuario:

```java
@Service
public class SecureFilterService {

    @Autowired FilterParser parser;
    @Autowired FilterSpecificationConverter converter;
    @Autowired FilterBuilder fb;

    public Specification<Document> parseSecure(String userQuery, User user) {
        ParseContext ctx = new ParseContextImpl(null, userNode -> {
            if (user.hasRole("ADMIN")) {
                return userNode;
            }

            FilterNode securityFilter = fb.field("ownerId").equal(fb.input(user.getId()))
                .or(fb.field("department").equal(fb.input(user.getDepartment())))
                .get();

            return fb.and(securityFilter, userNode).get();
        });

        FilterNode node = parser.parse(userQuery, ctx);
        return converter.convert(node);
    }
}
```

Los usuarios regulares se filtran automáticamente a sus propios documentos o documentos de su departamento. Los administradores ven todo.

### Control dinámico de acceso a campos

Restringe qué campos puede filtrar cada usuario:

```java
public class FieldAccessControlContext implements ParseContext {

    private final Set<String> allowedFields;

    public FieldAccessControlContext(User user) {
        this.allowedFields = user.hasRole("ADMIN")
            ? Set.of("id", "name", "email", "salary", "ssn", "department")
            : Set.of("id", "name", "department");
    }

    @Override
    public UnaryOperator<String> getFieldMapper() {
        return field -> {
            if (!allowedFields.contains(field)) {
                throw new SecurityException("Access denied to field: " + field);
            }
            return field;
        };
    }
}
```

```java
@GetMapping("/employees")
List<Employee> search(@Filter String filter, @AuthenticationPrincipal User user) {
    ParseContext ctx = new FieldAccessControlContext(user);
    FilterNode node = parser.parse(filter, ctx);
    Specification<Employee> spec = converter.convert(node);
    return repository.findAll(spec);
}
```

Usuarios no administradores que intenten `?filter=salary > 50000` recibirán una `SecurityException`.

### Reescritura de consultas para borrado lógico

Filtra automáticamente los registros eliminados lógicamente:

```java
@Service
public class SoftDeleteFilterService {

    @Autowired FilterParser parser;
    @Autowired FilterSpecificationConverter converter;
    @Autowired FilterBuilder fb;

    public <T> Specification<T> parseWithSoftDelete(String userQuery) {
        ParseContext ctx = new ParseContextImpl(null, userNode -> {
            FilterNode notDeleted = fb.field("deletedAt").isNull().get();
            return fb.and(notDeleted, userNode).get();
        });

        FilterNode node = parser.parse(userQuery, ctx);
        return converter.convert(node);
    }
}
```

Todas las consultas incluyen automáticamente `deletedAt is null`.

### Registro de auditoría

Registra todas las consultas de filtro con contexto de usuario:

```java
@Service
public class AuditingFilterService {

    @Autowired FilterParser parser;
    @Autowired FilterSpecificationConverter converter;
    @Autowired AuditLogger auditLogger;

    public <T> Specification<T> parseWithAudit(String query, User user) {
        ParseContext ctx = new ParseContextImpl(null, node -> {
            auditLogger.log("User {} executed filter: {}", user.getId(), query);
            return node;
        });

        FilterNode node = parser.parse(query, ctx);
        return converter.convert(node);
    }
}
```

### Normalización de nombres de campo

Maneja diferentes convenciones de nomenclatura:

```java
public class NormalizingParseContext implements ParseContext {

    @Override
    public UnaryOperator<String> getFieldMapper() {
        return field -> {
            String normalized = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field);

            if (normalized.endsWith("_id")) {
                return normalized.substring(0, normalized.length() - 3);
            }

            return normalized;
        };
    }
}
```

```java
ParseContext ctx = new NormalizingParseContext();
FilterNode node = parser.parse("userId : 123 and userName ~ 'john%'", ctx);
```

Convierte `userId` a `user` y `userName` a `user_name` automáticamente.

### Combinando múltiples contextos

Encadena múltiples contextos de parseo para escenarios complejos:

```java
public class CompositeParseContext implements ParseContext {

    private final List<ParseContext> contexts;

    public CompositeParseContext(ParseContext... contexts) {
        this.contexts = Arrays.asList(contexts);
    }

    @Override
    public UnaryOperator<String> getFieldMapper() {
        return field -> {
            String result = field;
            for (ParseContext ctx : contexts) {
                result = ctx.getFieldMapper().apply(result);
            }
            return result;
        };
    }

    @Override
    public UnaryOperator<FilterNode> getNodeMapper() {
        return node -> {
            FilterNode result = node;
            for (ParseContext ctx : contexts) {
                result = ctx.getNodeMapper().apply(result);
            }
            return result;
        };
    }
}
```

```java
ParseContext ctx = new CompositeParseContext(
    new FieldAccessControlContext(user),
    new TenantFilterContext(user.getTenantId()),
    new SoftDeleteContext()
);
```

Aplica múltiples transformaciones en una sola pasada.

### Contexto con ámbito de petición

Usa el ámbito de petición de Spring para análisis contextual:

```java
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestParseContext implements ParseContext {

    @Autowired HttpServletRequest request;
    @Autowired UserService userService;

    @Override
    public UnaryOperator<FilterNode> getNodeMapper() {
        return node -> {
            User user = userService.getCurrentUser();
            FilterNode tenantFilter = fb.field("tenantId")
                .equal(fb.input(user.getTenantId())).get();
            return fb.and(tenantFilter, node).get();
        };
    }
}
```

```java
@GetMapping("/products")
Page<Product> search(@Filter String filter) {
    ParseContext ctx = applicationContext.getBean(RequestParseContext.class);
    FilterNode node = parser.parse(filter, ctx);
    Specification<Product> spec = converter.convert(node);
    return repository.findAll(spec, pageable);
}
```

El contexto automáticamente usa la información del usuario de la petición actual.

## Pruebas

### Pruebas unitarias con Filter Builder

```java
@Test
void testFilterBuilder() {
    FilterNode filter = fb.field("year").greaterThan(fb.input(2020)).get();
    Specification<Car> spec = jpaConverter.convert(filter);

    List<Car> result = repository.findAll(spec);

    assertTrue(result.stream().allMatch(car -> car.getYear() > 2020));
}
```

### Pruebas de integración

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

### Pruebas de Predicates

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

## Rendimiento

### JPA

El módulo JPA genera consultas Criteria optimizadas. La base de datos ejecuta el filtrado, por lo que el rendimiento es equivalente a consultas SQL nativas. Usa índices apropiados en los campos filtrados.

### MongoDB

El módulo MongoDB genera pipelines de agregación. El rendimiento depende de los índices. Analiza las consultas con el explain plan de MongoDB.

### Predicate

El módulo Predicate filtra en memoria. El rendimiento es O(n) donde n es el tamaño de la colección. Adecuado para:
- Colecciones pequeñas
- Datos en caché
- Colecciones ya cargadas
- Pruebas

Para conjuntos de datos grandes, prefiere los módulos JPA o MongoDB para filtrar a nivel de base de datos.

## Contribuir

Pull requests bienvenidos. Usa [Google Java Style](https://github.com/google/styleguide/blob/gh-pages/eclipse-java-google-style.xml) para el formato.

### Contribuidores

* [@marcopag90](https://github.com/marcopag90) y [@glodepa](https://github.com/glodepa) - Soporte MongoDB
* [@sisimomo](https://github.com/sisimomo) - [Constructor de consultas JavaScript](https://github.com/sisimomo/Spring-Filter-Query-Builder)
* [@68ociredef](https://github.com/68ociredef) - [Constructor de consultas Angular](https://github.com/68ociredef/spring-filter-ng)

## Artículos

* [Filtra entidades fácilmente en tu API Spring](https://torshid.medium.com/easily-filter-entities-in-your-spring-api-f433537cfd41)

## Licencia

MIT License - consulta el archivo [LICENSE](LICENSE).
