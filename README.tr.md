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

> :us: [English](README.md) | :cn: [中文](README.zh-CN.md) | :es: [Español](README.es.md) | :jp: [日本語](README.ja.md)

Spring uygulamaları için dinamik sorgu filtreleme. Filtre ifadelerini URL parametresi olarak iletin ve JPA repository'lerine, MongoDB koleksiyonlarına veya bellek içi Java nesnelerine uygulayın.

Kütüphane filtre ifadelerini soyut sözdizimi ağaçlarına (AST) ayrıştırır, ardından kullandığınız modüle bağlı olarak JPA Criteria sorgularına, MongoDB sorgularına veya Java Predicate'lerine dönüştürür. Filtre oluşturucuyu kullanarak programatik olarak sorgu da oluşturabilirsiniz.

> Artık Spring Boot 4 ile uyumlu! Eski sürümler için [2.x.x](https://github.com/turkraft/springfilter/tree/2.x.x) / [3.x.x](https://github.com/turkraft/springfilter/tree/3.x.x) branch'lerine bakın.

## Ekosistem

JavaScript ve TypeScript için de mevcut:

| Paket | Açıklama |
|---|---|
| [FilterKit](https://github.com/turkraft/filterkit) | Çekirdek kütüphane — dizileri filtrele, sorgular oluştur, ifadeleri ayrıştır |
| [FilterKit TanStack](https://github.com/turkraft/filterkit-tanstack) | TanStack Table sütun filtreleri → Spring Filter |
| [FilterKit QueryBuilder](https://github.com/turkraft/filterkit-querybuilder) | react-querybuilder sorguları → Spring Filter |
| [FilterKit Prisma](https://github.com/turkraft/filterkit-prisma) | Filtre ifadeleri → Prisma where cümleleri |
| [FilterKit Drizzle](https://github.com/turkraft/filterkit-drizzle) | Filtre ifadeleri → Drizzle where cümleleri |

## Örnek ([canlı dene](https://springfilter-jpa.onrender.com/))

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

Kütüphane boolean, tarih, enum, fonksiyon ve entity ilişkilerini yönetir. JPA modülü Criteria sorguları üretir, MongoDB modülü aggregation pipeline'ları oluşturur, Predicate modülü ise bellek içi nesneleri filtreler.

## [Sponsorlar](https://github.com/sponsors/torshid)

Projemize sponsor olun, issue'larınız öncelik kazansın.

<table>
<tr>
<td align="center"><a href="https://github.com/ixorbv"><img width="64" src="https://avatars.githubusercontent.com/u/127401397?v=4"/><br/>ixorbv</a></td>
<td align="center"><a href="https://github.com/marcopag90"><img width="64" src="https://avatars.githubusercontent.com/marcopag90"/><br/>marcopag90</a></td>
</tr>
</table>

## Modüller

### JPA

JPA entity'lerini doğrudan veritabanı sorgularında filtreleyin. Modül, filtre ifadelerini JPA Criteria API Specification'larına dönüştürür.

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

Repository `JpaSpecificationExecutor` arayüzünü uygulamalıdır. `SimpleJpaRepository` standart bir uygulamadır. Sayfalama gerekmiyorsa `Pageable` argümanını kaldırın.

#### JPA ile Native Sorgular

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

#### JPA ile Projeksiyonlar

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

Spring Data MongoDB sorguları kullanarak MongoDB dokümanlarını filtreleyin.

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

#### MongoRepository ile

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

Java Predicate'leri kullanarak bellek içi koleksiyonları filtreleyin. Herhangi bir POJO ile çalışır, veritabanı gerektirmez.

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

#### Manuel Dönüşüm

```java
@Autowired FilterPredicateConverter converter;

public List<Car> filterCars(List<Car> cars, String filterExpression) {
    FilterPredicate<Car> predicate = converter.convert(filterExpression, Car.class);

    return cars.stream()
        .filter(predicate)
        .collect(Collectors.toList());
}
```

#### Kullanım Senaryoları

Predicate modülü şu durumlarda kullanışlıdır:
- Bellekteki önbellek verilerini filtreleme
- İstemciye dönmeden önce API yanıtlarını filtreleme
- Veritabanı olmadan filtre mantığını test etme
- Yapılandırma nesnelerini veya enum'ları filtreleme
- Bellekte toplu veri işleme

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

#### size Fonksiyonu Desteği

```java
// Koleksiyon boyutuna göre filtrele
GET /cars?filter=size(accidents) > 2
GET /owners?filter=size(cars) : 0
```

Predicate modülü tüm standart operatörleri ve koleksiyonlar, diziler, map'ler ve string'ler için `size()` fonksiyonunu destekler.

### Filter Builder (Filtre Oluşturucu)

Filtre string'lerini elle yazmak yerine, filtre ifadelerini programatik olarak oluşturun.

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

#### Karmaşık Sorgular

```java
FilterNode filter = fb.field("brand.name").in(
    fb.collection(fb.input("audi"), fb.input("bmw"))
).and(
    fb.field("year").greaterThan(fb.input(2020))
        .or(fb.field("km").lessThan(fb.input(50000)))
).get();
```

#### Fonksiyonlarla Kullanım

```java
@Autowired SizeFunction sizeFunction;

FilterNode filter = fb.function(sizeFunction, fb.field("accidents"))
    .greaterThan(fb.input(2))
    .and(fb.field("year").lessThan(fb.input(2015)))
    .get();
```

### Tip-Güvenli Filtre Oluşturucu

JPA entity'lerinizden derleme zamanında tip-güvenli filtre oluşturucular üretin. Her alan için IDE otomatik tamamlama, tip-güvenli karşılaştırmalar ve refactoring güvenliği.

```xml
<dependency>
  <groupId>com.turkraft.springfilter</groupId>
  <artifactId>typesafe</artifactId>
  <version>4.0.5</version>
</dependency>
```

Derleyici eklentinize annotation processor'ı ekleyin:

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

Entity'nizi işaretleyin:

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

Derleme zamanında `CarFilter` otomatik oluşturulur. Kullanımı:

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

// Üretir: year between 2020 and 2025 and model ~ 'Audi%' and price > 100.0 and active : true
```

Desteklenen alan tipleri: `int`, `long`, `double`, `boolean`, `String`, `Date`, `LocalDate`, `LocalDateTime`, enum'lar, koleksiyonlar. Doğru operatör önceliği ile `and()` / `or()` zincirlemeyi destekler. `@Transient` veya `@JsonIgnore` ile işaretlenmiş alanlar atlanır. Üst sınıflardan miras alınan alanlar dahil edilir.

## OpenAPI/Swagger

`@Filter` parametrelerine sahip endpoint'ler için otomatik Swagger dokümantasyonu ekleyin.

```xml
<dependency>
  <groupId>com.turkraft.springfilter</groupId>
  <artifactId>openapi</artifactId>
  <version>4.0.5</version>
</dependency>
```

Sadece bağımlılığı ekleyin. Swagger UI otomatik olarak şunları gösterir:
- Tüm filtrelenebilir alanlar ve tipleri
- İç içe ilişkiler
- Enum değerleri
- Örnek sorgular
- Operatör referansı
- Kullanılabilir fonksiyonlar

JPA, MongoDB ve Predicate modülleriyle çalışır.

## Sayfalama, Sıralama ve Alan Seçimi

`page-sort` modülü sayfalama, sıralama ve alan seçimi için anotasyonlar sağlar.

```xml
<dependency>
  <groupId>com.turkraft.springfilter</groupId>
  <artifactId>page-sort</artifactId>
  <version>4.0.5</version>
</dependency>
```

### Temel Kullanım

```java
@GetMapping("/cars")
Page<Car> search(@Filter Specification<Car> spec, @Pagination Pageable page) {
    return repository.findAll(spec, page);
}
```

Kullanım: `?page=0&size=20&sort=-year` (azalan sıra için `-` öneki)

### Özel Parametre Adları

```java
@GetMapping("/cars")
Page<Car> search(
    @Pagination(pageParameter = "p", sizeParameter = "limit", sortParameter = "order") Pageable page) {
    return repository.findAll(page);
}
```

Artık `?p=0&limit=50&order=-year` kullanabilirsiniz

### Sort Parametresi

```java
@GetMapping("/cars")
List<Car> search(@Sort org.springframework.data.domain.Sort sort) {
    return repository.findAll(sort);
}
```

`?sort=-year` veya `?sort=-year,name` kullanın

### Alan Seçimi

```java
@Fields
@GetMapping("/cars")
List<Car> search() {
    return repository.findAll();
}
```

Sadece belirtilen alanları döndürmek için `?fields=id,brand.name,year` kullanın. Dahili olarak Jackson'ın filtrelemesini kullanır.

```java
// Belirli alanları dahil et
?fields= id,name,email

// Alanları hariç tut
?fields= *,-password,-ssn

// İç içe alanlar
?fields= id,brand.name,brand.country

// Joker karakterler
?fields= user.*
```

#### Kök Kapsamı

`root` kullanarak alan seçimini bir sarmalayıcı yol içinde kapsamlandırın. Sayfalama meta verilerini (`totalElements`, `totalPages`) her zaman dahil ederken `content` içindeki alanları filtrelemek istediğiniz sayfalı yanıtlar için kullanışlıdır:

```java
@Fields(root = "content")
@GetMapping("/cars")
Page<Car> search() {
    return repository.findAll(pageable);
}
```

Artık `?fields=id,brand.name,year` yalnızca `content` içindeki özellikleri eşleştirir. `content` dışındaki meta veri alanları her zaman yanıta dahil edilir.

### Birleşik Örnek

```java
@Fields
@GetMapping("/cars")
Page<Car> search(
    @Filter Specification<Car> spec,
    @Pagination Pageable page) {
    return repository.findAll(spec, page);
}
```

Tüm özellikleri bir arada kullanın:
```
/cars?filter=year>2020&page=0&size=20&sort=-year&fields=id,brand.name,year
```

`openapi` modülü her iki bağımlılık da mevcut olduğunda bu parametreler için otomatik olarak dokümantasyon üretir.

## Frontend Entegrasyonu

JavaScript/TypeScript'te filtre ifadeleri oluşturmak için [FilterKit](https://github.com/turkraft/filterkit) kullanın. Spring Filter ile tamamen aynı ifade sözdizimini ve AST'yi paylaşır.

```ts
import { build, stringify } from '@turkraft/filterkit';

const query = build()
  .field('year').between(2020, 2025)
  .and(build().field('brand.name').in(['audi', 'bmw']))
  .get();

fetch(`/api/cars?filter=${encodeURIComponent(stringify(query))}`);
```

FilterKit ayrıca [TanStack Table](https://github.com/turkraft/filterkit-tanstack) ve [react-querybuilder](https://github.com/turkraft/filterkit-querybuilder) için React entegrasyonları da sunar.

### Topluluk projeleri

- [spring-filter-query-builder](https://github.com/sisimomo/Spring-Filter-Query-Builder) — JavaScript sorgu oluşturucu
- [spring-filter-ng](https://github.com/68ociredef/spring-filter-ng) — Angular entegrasyonu

## Dil Sözdizimi

### Alan Erişimi

```
field
field.nested
field.nested.deep
```

### Değişmez Değerler

```
123                 // tamsayı
-321.123            // ondalık
true, false         // boolean
'text'              // string
'1-01-2023'         // tarih (format Spring ConversionService'e bağlıdır)
'escape \' quote'   // escape edilmiş string
```

### Koleksiyonlar

```
[1, 2, 3]
['a', 'b', 'c']
[field, nested.field, 'literal']
[field, ['nested', 'array'], 99]
```

### Fonksiyonlar

```
size(collection)
size(field.collection)
today()
jsonText(field, 'key')
jsonText(field, 'key1', 'key2', ...)
```

### Yer Tutucular (Placeholders)

```
`placeholder_name`
```

Yer tutucular, sizin uyguladığınız özel yer tutucu işleyicileri tarafından çözümlenir.

### Operatörler

```
a and b              // mantıksal ve
a or b               // mantıksal veya
a xor b              // mantıksal özel veya
not a                // mantıksal değil
a : b                // eşittir
a ! b                // eşit değildir
a > b                // büyüktür
a >: b               // büyük eşittir
a < b                // küçüktür
a <: b               // küçük eşittir
a between x and y    // arasında (dahil)
a ~ 'pattern'        // like (% ve _ joker karakterleri)
a ~~ 'pattern'       // büyük/küçük harf duyarsız like
a in [x, y]          // koleksiyonda var
a not in [x, y]      // koleksiyonda yok
a is null            // null kontrolü
a is not null        // null değil kontrolü
a is empty           // boş kontrolü (koleksiyonlar/string'ler)
a is not empty       // boş değil kontrolü
```

### Öncelik

Değerlendirme sırasını kontrol etmek için parantez kullanın:

```
a and (b or c)
(status : 'active' or status : 'pending') and year > 2020
```

## Sorgu Örnekleri

### Temel Filtreleme

```
// Basit eşitlik
?filter= status : 'active'

// Çoklu koşul
?filter= year > 2020 and km < 50000

// Aralık (dahil)
?filter= year between 2020 and 2025

// VEYA koşulları
?filter= color : 'red' or color : 'blue'
```

### İlişkilerle Çalışma

```
// İlişkili entity alanına göre filtrele
?filter= brand.name : 'audi'

// İç içe ilişkiler
?filter= brand.manufacturer.country : 'germany'

// İlişkinin var olup olmadığını kontrol et
?filter= brand is not null

// Çoklu ilişki koşulları
?filter= brand.name : 'audi' and dealer.city : 'berlin'
```

### Koleksiyon İşlemleri

```
// Değerin listede olup olmadığını kontrol et
?filter= status in ['active', 'pending', 'review']

// Koleksiyon boyutunu kontrol et
?filter= size(accidents) > 2

// Koleksiyonun boş olup olmadığını kontrol et
?filter= accidents is empty

// Koleksiyonun boş olmadığını kontrol et
?filter= ratings is not empty
```

### String Eşleştirme

```
// Joker karakterli like (% = herhangi bir karakter, _ = tek karakter)
?filter= name ~ '%john%'

// Büyük/küçük harf duyarsız like
?filter= name ~~ 'JOHN'

// İle başlayan
?filter= email ~ 'admin%'

// İle biten
?filter= filename ~ '%.pdf'

// Desen eşleştirme
?filter= code ~ 'PRD-____-2023'

// Çoklu desen
?filter= name ~ ['%john%', '%doe%']

// Büyük/küçük harf duyarsız çoklu desen
?filter= name ~~ ['%JOHN%', '%DOE%']
```

### Tarih Filtreleme

```
// Tarih karşılaştırması (format Spring yapılandırmanıza bağlıdır)
?filter= createdAt > '2023-01-01'

// Tarih aralığı
?filter= createdAt > '2023-01-01' and createdAt < '2023-12-31'

// Tarih aralığı (between)
?filter= createdAt between '2023-01-01' and '2023-12-31'

// today() fonksiyonu ile göreceli tarihler
?filter= createdAt > today()
```

### Karmaşık Sorgular

```
// Öncelikli iç içe koşullar
?filter= (year > 2020 and km < 30000) or (year > 2018 and km < 10000)

// Farklı operatörlerin karışımı
?filter= brand.name in ['audi', 'bmw'] and year > 2020 and accidents is empty and color ! 'white'

// İlişkilerle koleksiyon boyutu
?filter= size(owner.vehicles) > 3 and status : 'active'
```

### Null ve Boş Kontrolleri

```
// Null kontrolü
?filter= deletedAt is null

// Null değil kontrolü
?filter= description is not null

// Boş koleksiyon
?filter= tags is empty

// Boş olmayan koleksiyon
?filter= children is not empty
```

### JSON/JSONB Filtreleme

```
// Anahtar yoluna göre string değeri çıkar
?filter= jsonText(metadata, 'address', 'city') : 'New York'

// Değerin var olup olmadığını kontrol et
?filter= jsonText(data, 'status') in ['active', 'pending']

// Tip dönüşümü ile birleştir
?filter= jsonText(data, 'age') > 18

// Desen eşleştirme ile iç içe anahtarlar
?filter= jsonText(metadata, 'address', 'city') ~ '%York%'
```

## Desteklenen Tipler

Tüm modüller şunları işler:
- İlkel tipler (int, long, double, boolean, vb.)
- String'ler
- Enum'lar
- Tarihler (LocalDate, LocalDateTime, Date, Instant, vb.)
- Koleksiyonlar (List, Set)
- Diziler
- Entity ilişkileri (@ManyToOne, @OneToMany, @ManyToMany)

Tarih ayrıştırma, Spring'in `ConversionService`'ini kullanır. Tarih formatlarını değiştirmek için yapılandırın.

## Özel Operatörler

`FilterInfixOperator`, `FilterPrefixOperator` veya `FilterPostfixOperator`'ı genişleterek özel operatörler tanımlayın:

```java
@Component
public class ContainsOperator extends FilterInfixOperator {
    public ContainsOperator() {
        super("contains", 5);
    }
}
```

Ardından kullandığınız her modül için işleyiciler uygulayın:

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
        // Uygulama
    }
}
```

Operatörü otomatik yapılandırma veya manuel yapılandırma ile kaydedin.

## Özel Fonksiyonlar

`FilterFunction`'ı genişleterek özel fonksiyonlar tanımlayın:

```java
@Component
public class LowerFunction extends FilterFunction {
    public LowerFunction() {
        super("lower");
    }
}
```

Her modül için işleyiciler uygulayın:

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

## Yapılandırma

### Varsayılan Parametre Adı

Varsayılan olarak, filtreler `filter` sorgu parametresinden okunur. Bunu geçersiz kılın:

```java
@GetMapping("/cars")
List<Car> search(@Filter(parameter = "q") Specification<Car> spec) {
    return repository.findAll(spec);
}
```

Artık `?filter=year > 2020` yerine `?q=year > 2020` kullanın.

### MongoDB için Entity Sınıfı

MongoDB, entity sınıfının açıkça belirtilmesini gerektirir:

```java
@GetMapping("/cars")
List<Car> search(@Filter(entityClass = Car.class) Query query) {
    return mongoTemplate.find(query, Car.class);
}
```

### İsteğe Bağlı Filtreler

Eksik filtre parametrelerini yönetmek için `Optional` kullanın:

```java
@GetMapping("/cars")
List<Car> search(@Filter Optional<Specification<Car>> spec) {
    return repository.findAll(spec.orElse(null));
}
```

### Programatik Filtre Uygulaması

Spring MVC anotasyonları olmadan filtreleri uygulayın:

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

## Spring Yapılandırması

Spring Filter, tip dönüşümleri için Spring'in `ConversionService`'ini kullanır. Tarih formatlarını, enum işlemeyi vb. özelleştirmek için yapılandırın:

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

## Gelişmiş: ParseContext

`ParseContext`, ayrıştırma sırasında filtre ifadelerini yakalamanıza ve değiştirmenize olanak tanır. İki kanca sağlar: alan eşleme ve düğüm eşleme.

### Alan Takma Adları

API alan adlarını veritabanı alan adlarına eşleyin:

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

Artık `?filter=price > 100` gibi sorgular veritabanı seviyesinde otomatik olarak `unitPrice > 100` haline gelir.

### Çoklu Kiracı (Multi-Tenancy)

Tüm sorgulara otomatik olarak kiracı filtresi enjekte edin:

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

Kullanıcı `status : 'active'` diye sorgular ancak gerçek sorgu `tenantId : 123 and status : 'active'` haline gelir.

### Güvenlik Filtreleri

Kullanıcı izinlerine göre satır seviyesinde güvenlik filtreleri enjekte edin:

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

Normal kullanıcılar otomatik olarak kendi dokümanlarına veya departman dokümanlarına filtrelenir. Yöneticiler her şeyi görür.

### Dinamik Alan Erişim Kontrolü

Kullanıcıların hangi alanlarda filtreleme yapabileceğini kısıtlayın:

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

Yönetici olmayan kullanıcılar `?filter=salary > 50000` denediğinde `SecurityException` alır.

### Soft Delete için Sorgu Yeniden Yazımı

Soft delete edilmiş kayıtları otomatik olarak filtreleyin:

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

Tüm sorgular otomatik olarak `deletedAt is null` içerir.

### Denetim Günlüğü (Audit Logging)

Tüm filtre sorgularını kullanıcı bağlamıyla günlüğe kaydedin:

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

### Alan Adı Normalleştirme

Farklı adlandırma kurallarını yönetin:

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

`userId`'ı `user`'a ve `userName`'i `user_name`'e otomatik dönüştürür.

### Birden Fazla Context'i Birleştirme

Karmaşık senaryolar için birden fazla ParseContext'i zincirleyin:

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

Tek geçişte birden fazla dönüşüm uygulayın.

### İstek Kapsamlı Context

Bağlam duyarlı ayrıştırma için Spring'in istek kapsamını kullanın:

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

Context otomatik olarak mevcut isteğin kullanıcı bilgisini kullanır.

## Test

### Filter Builder ile Birim Testi

```java
@Test
void testFilterBuilder() {
    FilterNode filter = fb.field("year").greaterThan(fb.input(2020)).get();
    Specification<Car> spec = jpaConverter.convert(filter);

    List<Car> result = repository.findAll(spec);

    assertTrue(result.stream().allMatch(car -> car.getYear() > 2020));
}
```

### Entegrasyon Testi

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

### Predicate Testi

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

## Performans

### JPA

JPA modülü optimize edilmiş Criteria sorguları üretir. Filtrelemeyi veritabanı gerçekleştirir, bu nedenle performans native SQL sorgularıyla eşdeğerdir. Filtrelenen alanlarda uygun indeksler kullanın.

### MongoDB

MongoDB modülü aggregation pipeline'ları üretir. Performans indekslere bağlıdır. Sorguları MongoDB'nin explain plan'ı ile profilleyin.

### Predicate

Predicate modülü bellekte filtreler. Performans O(n)'dir, n koleksiyon boyutudur. Şunlar için uygundur:
- Küçük koleksiyonlar
- Önbellek verileri
- Zaten yüklenmiş koleksiyonlar
- Test

Büyük veri setleri için, veritabanı seviyesinde filtrelemek üzere JPA veya MongoDB modüllerini tercih edin.

## Katkıda Bulunma

Pull request'lere açığız. Formatlama için [Google Java Style](https://github.com/google/styleguide/blob/gh-pages/eclipse-java-google-style.xml) kullanın.

### Katkıda Bulunanlar

* [@marcopag90](https://github.com/marcopag90) ve [@glodepa](https://github.com/glodepa) - MongoDB desteği
* [@sisimomo](https://github.com/sisimomo) - [JavaScript sorgu oluşturucu](https://github.com/sisimomo/Spring-Filter-Query-Builder)
* [@68ociredef](https://github.com/68ociredef) - [Angular sorgu oluşturucu](https://github.com/68ociredef/spring-filter-ng)

## Makaleler

* [Spring API'nizde entity'leri kolayca filtreleyin](https://torshid.medium.com/easily-filter-entities-in-your-spring-api-f433537cfd41)

## Lisans

MIT License - [LICENSE](LICENSE) dosyasına bakın.
