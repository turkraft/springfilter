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

> :us: [English](README.md) | :es: [Español](README.es.md) | :jp: [日本語](README.ja.md) | :tr: [Türkçe](README.tr.md)

Spring 应用程序的动态查询过滤库。将过滤表达式作为 URL 参数传递，并将其应用到 JPA 仓库、MongoDB 集合或内存 Java 对象中。

该库将过滤表达式解析为抽象语法树，然后根据您使用的模块将其转换为 JPA Criteria 查询、MongoDB 查询或 Java Predicate。您还可以使用过滤器构建器以编程方式构造查询。

> 现已兼容 Spring Boot 4！需要旧版本？请查看 [2.x.x](https://github.com/turkraft/springfilter/tree/2.x.x) / [3.x.x](https://github.com/turkraft/springfilter/tree/3.x.x) 分支。

## 生态系统

也适用于 JavaScript 和 TypeScript：

| 包 | 描述 |
|---|---|
| [FilterKit](https://github.com/turkraft/filterkit) | 核心库 — 过滤数组、构建查询、解析表达式 |
| [FilterKit TanStack](https://github.com/turkraft/filterkit-tanstack) | TanStack Table 列过滤器 → Spring Filter |
| [FilterKit QueryBuilder](https://github.com/turkraft/filterkit-querybuilder) | react-querybuilder 查询 → Spring Filter |
| [FilterKit Prisma](https://github.com/turkraft/filterkit-prisma) | 过滤表达式 → Prisma where 子句 |
| [FilterKit Drizzle](https://github.com/turkraft/filterkit-drizzle) | 过滤表达式 → Drizzle where 子句 |

## 示例（[在线试用](https://springfilter-jpa.onrender.com/)）

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

该库支持布尔值、日期、枚举、函数以及实体关联。JPA 模块生成 Criteria 查询，MongoDB 模块生成聚合管道，Predicate 模块过滤内存对象。

## [赞助者](https://github.com/sponsors/torshid)

赞助我们的项目，让您的 issue 获得优先处理。

<table>
<tr>
<td align="center"><a href="https://github.com/ixorbv"><img width="64" src="https://avatars.githubusercontent.com/u/127401397?v=4"/><br/>ixorbv</a></td>
<td align="center"><a href="https://github.com/marcopag90"><img width="64" src="https://avatars.githubusercontent.com/marcopag90"/><br/>marcopag90</a></td>
</tr>
</table>

## 模块

### JPA

直接在数据库查询中过滤 JPA 实体。该模块将过滤表达式转换为 JPA Criteria API 的 Specification。

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

仓库必须实现 `JpaSpecificationExecutor`。`SimpleJpaRepository` 是标准实现。如果不需要分页，可以移除 `Pageable` 参数。

#### JPA 原生查询

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

#### JPA 投影查询

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

使用 Spring Data MongoDB 查询过滤 MongoDB 文档。

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

#### 结合 MongoRepository

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

使用 Java Predicate 过滤内存中的集合。适用于任何 POJO，无需数据库。

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

#### 手动转换

```java
@Autowired FilterPredicateConverter converter;

public List<Car> filterCars(List<Car> cars, String filterExpression) {
    FilterPredicate<Car> predicate = converter.convert(filterExpression, Car.class);

    return cars.stream()
        .filter(predicate)
        .collect(Collectors.toList());
}
```

#### 使用场景

Predicate 模块适用于以下场景：
- 过滤内存中的缓存数据
- 在返回给客户端之前过滤 API 响应
- 无需数据库即可测试过滤逻辑
- 过滤配置对象或枚举
- 在内存中处理批量数据

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

#### size 函数支持

```java
// 按集合大小过滤
GET /cars?filter=size(accidents) > 2
GET /owners?filter=size(cars) : 0
```

Predicate 模块支持所有标准操作符以及用于集合、数组、Map 和字符串的 `size()` 函数。

### Filter Builder（过滤器构建器）

以编程方式构建过滤表达式，无需手动编写过滤字符串。

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

#### 复杂查询

```java
FilterNode filter = fb.field("brand.name").in(
    fb.collection(fb.input("audi"), fb.input("bmw"))
).and(
    fb.field("year").greaterThan(fb.input(2020))
        .or(fb.field("km").lessThan(fb.input(50000)))
).get();
```

#### 使用函数

```java
@Autowired SizeFunction sizeFunction;

FilterNode filter = fb.function(sizeFunction, fb.field("accidents"))
    .greaterThan(fb.input(2))
    .and(fb.field("year").lessThan(fb.input(2015)))
    .get();
```

### 类型安全过滤器构建器

从您的 JPA 实体生成编译期类型安全的过滤器构建器。每个字段均有 IDE 自动补全、类型安全比较，且重构安全。

```xml
<dependency>
  <groupId>com.turkraft.springfilter</groupId>
  <artifactId>typesafe</artifactId>
  <version>4.0.5</version>
</dependency>
```

在编译插件中加入注解处理器：

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

在实体上添加注解：

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

编译时自动生成 `CarFilter`。使用方式：

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

// 生成: year between 2020 and 2025 and model ~ 'Audi%' and price > 100.0 and active : true
```

支持的字段类型：`int`、`long`、`double`、`boolean`、`String`、`Date`、`LocalDate`、`LocalDateTime`、枚举、集合。支持 `and()` / `or()` 链式调用并保持正确的运算符优先级。带有 `@Transient` 或 `@JsonIgnore` 注解的字段将被跳过。来自父类的继承字段会被包含。

## OpenAPI/Swagger

为带有 `@Filter` 参数的端点自动添加 Swagger 文档。

```xml
<dependency>
  <groupId>com.turkraft.springfilter</groupId>
  <artifactId>openapi</artifactId>
  <version>4.0.5</version>
</dependency>
```

只需添加依赖，Swagger UI 会自动显示：
- 所有可过滤字段及其类型
- 嵌套关联
- 枚举值
- 示例查询
- 操作符参考
- 可用函数

兼容 JPA、MongoDB 和 Predicate 模块。

## 分页、排序和字段选择

`page-sort` 模块提供分页、排序和字段选择的注解。

```xml
<dependency>
  <groupId>com.turkraft.springfilter</groupId>
  <artifactId>page-sort</artifactId>
  <version>4.0.5</version>
</dependency>
```

### 基本用法

```java
@GetMapping("/cars")
Page<Car> search(@Filter Specification<Car> spec, @Pagination Pageable page) {
    return repository.findAll(spec, page);
}
```

使用方式：`?page=0&size=20&sort=-year`（前缀 `-` 表示降序）

### 自定义参数名

```java
@GetMapping("/cars")
Page<Car> search(
    @Pagination(pageParameter = "p", sizeParameter = "limit", sortParameter = "order") Pageable page) {
    return repository.findAll(page);
}
```

现在使用 `?p=0&limit=50&order=-year`

### Sort 参数

```java
@GetMapping("/cars")
List<Car> search(@Sort org.springframework.data.domain.Sort sort) {
    return repository.findAll(sort);
}
```

使用 `?sort=-year` 或 `?sort=-year,name`

### 字段选择

```java
@Fields
@GetMapping("/cars")
List<Car> search() {
    return repository.findAll();
}
```

使用 `?fields=id,brand.name,year` 仅返回指定字段。内部使用 Jackson 过滤机制。

```java
// 包含特定字段
?fields= id,name,email

// 排除字段
?fields= *,-password,-ssn

// 嵌套字段
?fields= id,brand.name,brand.country

// 通配符
?fields= user.*
```

#### 根路径限定

使用 `root` 将字段选择限制在包装路径内。这对于分页响应特别有用，您可能只想过滤 `content` 内的字段，同时始终包含分页元数据（`totalElements`、`totalPages`）：

```java
@Fields(root = "content")
@GetMapping("/cars")
Page<Car> search() {
    return repository.findAll(pageable);
}
```

现在 `?fields=id,brand.name,year` 仅匹配 `content` 内的属性 — `content` 外部的元数据字段始终包含在响应中。

### 组合示例

```java
@Fields
@GetMapping("/cars")
Page<Car> search(
    @Filter Specification<Car> spec,
    @Pagination Pageable page) {
    return repository.findAll(spec, page);
}
```

同时使用所有功能：
```
/cars?filter=year>2020&page=0&size=20&sort=-year&fields=id,brand.name,year
```

当两个依赖都存在时，`openapi` 模块会自动为这些参数生成文档。

## 前端集成

使用 [FilterKit](https://github.com/turkraft/filterkit) 在 JavaScript/TypeScript 中构建过滤表达式。它与 Spring Filter 共享完全相同的表达式语法和 AST。

```ts
import { build, stringify } from '@turkraft/filterkit';

const query = build()
  .field('year').between(2020, 2025)
  .and(build().field('brand.name').in(['audi', 'bmw']))
  .get();

fetch(`/api/cars?filter=${encodeURIComponent(stringify(query))}`);
```

FilterKit 还提供了 [TanStack Table](https://github.com/turkraft/filterkit-tanstack) 和 [react-querybuilder](https://github.com/turkraft/filterkit-querybuilder) 的 React 集成。

### 社区项目

- [spring-filter-query-builder](https://github.com/sisimomo/Spring-Filter-Query-Builder) — JavaScript 查询构建器
- [spring-filter-ng](https://github.com/68ociredef/spring-filter-ng) — Angular 集成

## 语言语法

### 字段访问

```
field
field.nested
field.nested.deep
```

### 字面量

```
123                 // 整数
-321.123            // 小数
true, false         // 布尔值
'text'              // 字符串
'1-01-2023'         // 日期（格式取决于 Spring ConversionService）
'escape \' quote'   // 转义字符串
```

### 集合

```
[1, 2, 3]
['a', 'b', 'c']
[field, nested.field, 'literal']
[field, ['nested', 'array'], 99]
```

### 函数

```
size(collection)
size(field.collection)
today()
jsonText(field, 'key')
jsonText(field, 'key1', 'key2', ...)
```

### 占位符

```
`placeholder_name`
```

占位符由您实现的自定义占位符处理器解析。

### 操作符

```
a and b              // 逻辑与
a or b               // 逻辑或
a xor b              // 逻辑异或
not a                // 逻辑非
a : b                // 等于
a ! b                // 不等于
a > b                // 大于
a >: b               // 大于等于
a < b                // 小于
a <: b               // 小于等于
a between x and y    // 范围（包含边界）
a ~ 'pattern'        // 模糊匹配（% 和 _ 通配符）
a ~~ 'pattern'       // 不区分大小写的模糊匹配
a in [x, y]          // 属于集合
a not in [x, y]      // 不属于集合
a is null            // 空值判断
a is not null        // 非空判断
a is empty           // 空判断（集合/字符串）
a is not empty       // 非空判断
```

### 优先级

使用括号控制求值顺序：

```
a and (b or c)
(status : 'active' or status : 'pending') and year > 2020
```

## 查询示例

### 基本过滤

```
// 简单相等
?filter= status : 'active'

// 多个条件
?filter= year > 2020 and km < 50000

// 范围（包含边界）
?filter= year between 2020 and 2025

// OR 条件
?filter= color : 'red' or color : 'blue'
```

### 关联操作

```
// 按关联实体字段过滤
?filter= brand.name : 'audi'

// 嵌套关联
?filter= brand.manufacturer.country : 'germany'

// 判断关联是否存在
?filter= brand is not null

// 多关联条件
?filter= brand.name : 'audi' and dealer.city : 'berlin'
```

### 集合操作

```
// 判断值是否在列表中
?filter= status in ['active', 'pending', 'review']

// 判断集合大小
?filter= size(accidents) > 2

// 判断集合是否为空
?filter= accidents is empty

// 判断集合是否非空
?filter= ratings is not empty
```

### 字符串匹配

```
// 模糊匹配（% = 任意字符, _ = 单个字符）
?filter= name ~ '%john%'

// 不区分大小写的模糊匹配
?filter= name ~~ 'JOHN'

// 开头匹配
?filter= email ~ 'admin%'

// 结尾匹配
?filter= filename ~ '%.pdf'

// 模式匹配
?filter= code ~ 'PRD-____-2023'

// 多模式匹配
?filter= name ~ ['%john%', '%doe%']

// 不区分大小写的多模式匹配
?filter= name ~~ ['%JOHN%', '%DOE%']
```

### 日期过滤

```
// 日期比较（格式取决于 Spring 配置）
?filter= createdAt > '2023-01-01'

// 日期范围
?filter= createdAt > '2023-01-01' and createdAt < '2023-12-31'

// 日期范围（between）
?filter= createdAt between '2023-01-01' and '2023-12-31'

// 使用 today() 函数的相对日期
?filter= createdAt > today()
```

### 复杂查询

```
// 带优先级的嵌套条件
?filter= (year > 2020 and km < 30000) or (year > 2018 and km < 10000)

// 混合多种操作符
?filter= brand.name in ['audi', 'bmw'] and year > 2020 and accidents is empty and color ! 'white'

// 集合大小与关联结合
?filter= size(owner.vehicles) > 3 and status : 'active'
```

### Null 和空判断

```
// 判断 null
?filter= deletedAt is null

// 判断非 null
?filter= description is not null

// 空集合
?filter= tags is empty

// 非空集合
?filter= children is not empty
```

### JSON/JSONB 过滤

```
// 通过键路径提取字符串值
?filter= jsonText(metadata, 'address', 'city') : 'New York'

// 判断值是否存在
?filter= jsonText(data, 'status') in ['active', 'pending']

// 结合类型转换
?filter= jsonText(data, 'age') > 18

// 嵌套键与模糊匹配
?filter= jsonText(metadata, 'address', 'city') ~ '%York%'
```

## 支持的类型

所有模块均支持：
- 基本类型（int、long、double、boolean 等）
- 字符串
- 枚举
- 日期（LocalDate、LocalDateTime、Date、Instant 等）
- 集合（List、Set）
- 数组
- 实体关联（@ManyToOne、@OneToMany、@ManyToMany）

日期解析使用 Spring 的 `ConversionService`，可通过配置更改日期格式。

## 自定义操作符

通过继承 `FilterInfixOperator`、`FilterPrefixOperator` 或 `FilterPostfixOperator` 定义自定义操作符：

```java
@Component
public class ContainsOperator extends FilterInfixOperator {
    public ContainsOperator() {
        super("contains", 5);
    }
}
```

然后为每个使用的模块实现处理器：

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
        // 实现逻辑
    }
}
```

通过自动配置或手动配置注册该操作符。

## 自定义函数

通过继承 `FilterFunction` 定义自定义函数：

```java
@Component
public class LowerFunction extends FilterFunction {
    public LowerFunction() {
        super("lower");
    }
}
```

为每个模块实现处理器：

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

## 配置

### 默认参数名

默认情况下，过滤器从 `filter` 查询参数中读取。可以覆盖：

```java
@GetMapping("/cars")
List<Car> search(@Filter(parameter = "q") Specification<Car> spec) {
    return repository.findAll(spec);
}
```

现在使用 `?q=year > 2020` 代替 `?filter=year > 2020`。

### MongoDB 的实体类

MongoDB 需要显式指定实体类：

```java
@GetMapping("/cars")
List<Car> search(@Filter(entityClass = Car.class) Query query) {
    return mongoTemplate.find(query, Car.class);
}
```

### 可选过滤器

使用 `Optional` 处理缺失的过滤参数：

```java
@GetMapping("/cars")
List<Car> search(@Filter Optional<Specification<Car>> spec) {
    return repository.findAll(spec.orElse(null));
}
```

### 编程式过滤器应用

无需 Spring MVC 注解即可应用过滤器：

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

## Spring 配置

Spring Filter 使用 Spring 的 `ConversionService` 进行类型转换。通过配置自定义日期格式、枚举处理等：

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

## 高级：ParseContext

`ParseContext` 允许您在解析过程中拦截和修改过滤表达式。它提供两个钩子：字段映射和节点映射。

### 字段别名

将 API 字段名映射到数据库字段名：

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

现在 `?filter=price > 100` 这类查询会自动在数据库层面转换为 `unitPrice > 100`。

### 多租户

自动向所有查询注入租户过滤器：

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

用户查询 `status : 'active'`，但实际执行的查询变为 `tenantId : 123 and status : 'active'`。

### 安全过滤器

根据用户权限注入行级安全过滤器：

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

普通用户自动被限制只能查看自己的文档或部门文档。管理员可以查看所有内容。

### 动态字段访问控制

限制用户可以过滤的字段：

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

非管理员用户尝试 `?filter=salary > 50000` 将抛出 `SecurityException`。

### 软删除的查询重写

自动过滤已软删除的记录：

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

所有查询自动包含 `deletedAt is null`。

### 审计日志

记录所有过滤查询及其用户上下文：

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

### 字段名规范化

处理不同的命名约定：

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

自动将 `userId` 转换为 `user`，将 `userName` 转换为 `user_name`。

### 组合多个上下文

为复杂场景串联多个解析上下文：

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

在单次处理中应用多个转换。

### 请求作用域上下文

使用 Spring 的请求作用域进行上下文感知解析：

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

上下文自动使用当前请求的用户信息。

## 测试

### 使用 Filter Builder 的单元测试

```java
@Test
void testFilterBuilder() {
    FilterNode filter = fb.field("year").greaterThan(fb.input(2020)).get();
    Specification<Car> spec = jpaConverter.convert(filter);

    List<Car> result = repository.findAll(spec);

    assertTrue(result.stream().allMatch(car -> car.getYear() > 2020));
}
```

### 集成测试

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

### 测试 Predicate

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

## 性能

### JPA

JPA 模块生成优化的 Criteria 查询。数据库执行过滤，因此性能与原生 SQL 查询相当。建议在过滤字段上建立适当的索引。

### MongoDB

MongoDB 模块生成聚合管道。性能取决于索引。可使用 MongoDB 的 explain plan 分析查询。

### Predicate

Predicate 模块在内存中过滤。性能为 O(n)，其中 n 为集合大小。适用于：
- 小集合
- 缓存数据
- 已经加载的集合
- 测试

对于大数据集，建议使用 JPA 或 MongoDB 模块在数据库层面过滤。

## 贡献

欢迎提交 Pull Request。请使用 [Google Java Style](https://github.com/google/styleguide/blob/gh-pages/eclipse-java-google-style.xml) 进行代码格式化。

### 贡献者

* [@marcopag90](https://github.com/marcopag90) 和 [@glodepa](https://github.com/glodepa) - MongoDB 支持
* [@sisimomo](https://github.com/sisimomo) - [JavaScript 查询构建器](https://github.com/sisimomo/Spring-Filter-Query-Builder)
* [@68ociredef](https://github.com/68ociredef) - [Angular 查询构建器](https://github.com/68ociredef/spring-filter-ng)

## 文章

* [轻松过滤 Spring API 中的实体](https://torshid.medium.com/easily-filter-entities-in-your-spring-api-f433537cfd41)

## 许可证

MIT License - 详见 [LICENSE](LICENSE) 文件。
