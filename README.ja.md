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

> :us: [English](README.md) | :cn: [中文](README.zh-CN.md) | :es: [Español](README.es.md) | :tr: [Türkçe](README.tr.md)

Spring アプリケーション向けの動的クエリフィルタリング。フィルタ式を URL パラメータとして渡し、JPA リポジトリ、MongoDB コレクション、またはメモリ内の Java オブジェクトに適用します。

このライブラリはフィルタ式を抽象構文木にパースし、使用するモジュールに応じて JPA Criteria クエリ、MongoDB クエリ、または Java Predicate に変換します。フィルタビルダーを使ってプログラム的にクエリを構築することもできます。

> Spring Boot 4 に対応！以前のバージョンは [2.x.x](https://github.com/turkraft/springfilter/tree/2.x.x) / [3.x.x](https://github.com/turkraft/springfilter/tree/3.x.x) ブランチをご覧ください。

## エコシステム

JavaScript と TypeScript でも利用可能です：

| パッケージ | 説明 |
|---|---|
| [FilterKit](https://github.com/turkraft/filterkit) | コアライブラリ — 配列のフィルタリング、クエリ構築、式の解析 |
| [FilterKit TanStack](https://github.com/turkraft/filterkit-tanstack) | TanStack Table カラムフィルタ → Spring Filter |
| [FilterKit QueryBuilder](https://github.com/turkraft/filterkit-querybuilder) | react-querybuilder クエリ → Spring Filter |
| [FilterKit Prisma](https://github.com/turkraft/filterkit-prisma) | フィルタ式 → Prisma where 句 |
| [FilterKit Drizzle](https://github.com/turkraft/filterkit-drizzle) | フィルタ式 → Drizzle where 句 |

## 例（[ライブデモ](https://springfilter-jpa.onrender.com/)）

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

ブーリアン、日付、列挙型、関数、エンティティリレーションを処理します。JPA モジュールは Criteria クエリを生成し、MongoDB モジュールは集計パイプラインを生成し、Predicate モジュールはメモリ内オブジェクトをフィルタリングします。

## [スポンサー](https://github.com/sponsors/torshid)

プロジェクトをスポンサーしていただくと、Issue が優先されます。

<table>
<tr>
<td align="center"><a href="https://github.com/ixorbv"><img width="64" src="https://avatars.githubusercontent.com/u/127401397?v=4"/><br/>ixorbv</a></td>
<td align="center"><a href="https://github.com/marcopag90"><img width="64" src="https://avatars.githubusercontent.com/marcopag90"/><br/>marcopag90</a></td>
</tr>
</table>

## モジュール

### JPA

データベースクエリで直接 JPA エンティティをフィルタリングします。フィルタ式を JPA Criteria API の Specification に変換します。

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

リポジトリは `JpaSpecificationExecutor` を実装する必要があります。`SimpleJpaRepository` が標準実装です。ページネーションが不要な場合は `Pageable` 引数を削除してください。

#### JPA ネイティブクエリ

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

#### JPA プロジェクション

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

Spring Data MongoDB クエリを使用して MongoDB ドキュメントをフィルタリングします。

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

#### MongoRepository との統合

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

Java Predicate を使用してメモリ内コレクションをフィルタリングします。データベース不要で、任意の POJO で動作します。

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

#### 手動変換

```java
@Autowired FilterPredicateConverter converter;

public List<Car> filterCars(List<Car> cars, String filterExpression) {
    FilterPredicate<Car> predicate = converter.convert(filterExpression, Car.class);

    return cars.stream()
        .filter(predicate)
        .collect(Collectors.toList());
}
```

#### ユースケース

Predicate モジュールは以下の場合に便利です：
- メモリ内のキャッシュデータのフィルタリング
- クライアントに返す前の API レスポンスのフィルタリング
- データベースなしでのフィルタロジックのテスト
- 設定オブジェクトや列挙型のフィルタリング
- メモリ内でのバッチデータ処理

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

#### size 関数のサポート

```java
// コレクションのサイズでフィルタ
GET /cars?filter=size(accidents) > 2
GET /owners?filter=size(cars) : 0
```

Predicate モジュールは全ての標準演算子と、コレクション、配列、Map、文字列の `size()` 関数をサポートします。

### Filter Builder

フィルタ文字列を手動で書く代わりに、プログラム的にフィルタ式を構築します。

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

#### 複雑なクエリ

```java
FilterNode filter = fb.field("brand.name").in(
    fb.collection(fb.input("audi"), fb.input("bmw"))
).and(
    fb.field("year").greaterThan(fb.input(2020))
        .or(fb.field("km").lessThan(fb.input(50000)))
).get();
```

#### 関数の使用

```java
@Autowired SizeFunction sizeFunction;

FilterNode filter = fb.function(sizeFunction, fb.field("accidents"))
    .greaterThan(fb.input(2))
    .and(fb.field("year").lessThan(fb.input(2015)))
    .get();
```

### 型安全フィルタビルダー

JPA エンティティからコンパイル時に型安全なフィルタビルダーを生成します。各フィールドでの IDE オートコンプリート、型安全な比較、リファクタリング安全性を提供します。

```xml
<dependency>
  <groupId>com.turkraft.springfilter</groupId>
  <artifactId>typesafe</artifactId>
  <version>4.0.5</version>
</dependency>
```

コンパイラプラグインにアノテーションプロセッサを追加：

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

エンティティにアノテーションを付与：

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

コンパイル時に `CarFilter` が生成されます。使用方法：

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

// 生成結果: year between 2020 and 2025 and model ~ 'Audi%' and price > 100.0 and active : true
```

サポートされるフィールド型：`int`、`long`、`double`、`boolean`、`String`、`Date`、`LocalDate`、`LocalDateTime`、列挙型、コレクション。正しい演算子優先順位で `and()` / `or()` チェーンをサポート。`@Transient` または `@JsonIgnore` が付与されたフィールドはスキップされます。スーパークラスから継承されたフィールドは含まれます。

## OpenAPI/Swagger

`@Filter` パラメータを持つエンドポイントに Swagger ドキュメントを自動追加します。

```xml
<dependency>
  <groupId>com.turkraft.springfilter</groupId>
  <artifactId>openapi</artifactId>
  <version>4.0.5</version>
</dependency>
```

依存関係を追加するだけです。Swagger UI が自動的に以下を表示します：
- すべてのフィルタ可能フィールドとその型
- ネストされたリレーション
- 列挙値
- サンプルクエリ
- 演算子リファレンス
- 利用可能な関数

JPA、MongoDB、Predicate モジュールに対応。

## ページネーション、ソート、フィールド選択

`page-sort` モジュールはページネーション、ソート、フィールド選択のアノテーションを提供します。

```xml
<dependency>
  <groupId>com.turkraft.springfilter</groupId>
  <artifactId>page-sort</artifactId>
  <version>4.0.5</version>
</dependency>
```

### 基本的な使い方

```java
@GetMapping("/cars")
Page<Car> search(@Filter Specification<Car> spec, @Pagination Pageable page) {
    return repository.findAll(spec, page);
}
```

使用例：`?page=0&size=20&sort=-year`（降順の場合は接頭辞 `-`）

### カスタムパラメータ名

```java
@GetMapping("/cars")
Page<Car> search(
    @Pagination(pageParameter = "p", sizeParameter = "limit", sortParameter = "order") Pageable page) {
    return repository.findAll(page);
}
```

これで `?p=0&limit=50&order=-year` が使用できます。

### Sort パラメータ

```java
@GetMapping("/cars")
List<Car> search(@Sort org.springframework.data.domain.Sort sort) {
    return repository.findAll(sort);
}
```

`?sort=-year` または `?sort=-year,name` を使用。

### フィールド選択

```java
@Fields
@GetMapping("/cars")
List<Car> search() {
    return repository.findAll();
}
```

`?fields=id,brand.name,year` で指定されたフィールドのみを返します。内部で Jackson のフィルタリングを使用します。

```java
// 特定のフィールドを含める
?fields= id,name,email

// フィールドを除外
?fields= *,-password,-ssn

// ネストされたフィールド
?fields= id,brand.name,brand.country

// ワイルドカード
?fields= user.*
```

#### ルートスコープ

`root` を使用して、ラッパーパス内でフィールド選択をスコープします。ページネーションメタデータ（`totalElements`、`totalPages`）を常に含めつつ、`content` 内のフィールドのみをフィルタしたいページネーション付きレスポンスに便利です：

```java
@Fields(root = "content")
@GetMapping("/cars")
Page<Car> search() {
    return repository.findAll(pageable);
}
```

これで `?fields=id,brand.name,year` は `content` 内のプロパティのみにマッチし、`content` 外のメタデータフィールドは常にレスポンスに含まれます。

### 組み合わせ例

```java
@Fields
@GetMapping("/cars")
Page<Car> search(
    @Filter Specification<Car> spec,
    @Pagination Pageable page) {
    return repository.findAll(spec, page);
}
```

すべての機能を同時に使用：
```
/cars?filter=year>2020&page=0&size=20&sort=-year&fields=id,brand.name,year
```

両方の依存関係がある場合、`openapi` モジュールがこれらのパラメータのドキュメントを自動生成します。

## フロントエンド統合

JavaScript/TypeScript でフィルタ式を構築するには [FilterKit](https://github.com/turkraft/filterkit) を使用します。Spring Filter と全く同じ式構文と AST を共有します。

```ts
import { build, stringify } from '@turkraft/filterkit';

const query = build()
  .field('year').between(2020, 2025)
  .and(build().field('brand.name').in(['audi', 'bmw']))
  .get();

fetch(`/api/cars?filter=${encodeURIComponent(stringify(query))}`);
```

FilterKit は [TanStack Table](https://github.com/turkraft/filterkit-tanstack) と [react-querybuilder](https://github.com/turkraft/filterkit-querybuilder) 向けの React 統合も提供しています。

### コミュニティプロジェクト

- [spring-filter-query-builder](https://github.com/sisimomo/Spring-Filter-Query-Builder) — JavaScript クエリビルダー
- [spring-filter-ng](https://github.com/68ociredef/spring-filter-ng) — Angular 統合

## 言語構文

### フィールドアクセス

```
field
field.nested
field.nested.deep
```

### リテラル

```
123                 // 整数
-321.123            // 小数
true, false         // 真偽値
'text'              // 文字列
'1-01-2023'         // 日付（形式は Spring ConversionService に依存）
'escape \' quote'   // エスケープ文字列
```

### コレクション

```
[1, 2, 3]
['a', 'b', 'c']
[field, nested.field, 'literal']
[field, ['nested', 'array'], 99]
```

### 関数

```
size(collection)
size(field.collection)
today()
jsonText(field, 'key')
jsonText(field, 'key1', 'key2', ...)
```

### プレースホルダー

```
`placeholder_name`
```

プレースホルダーは、実装したカスタムプレースホルダープロセッサによって解決されます。

### 演算子

```
a and b              // 論理積
a or b               // 論理和
a xor b              // 排他的論理和
not a                // 否定
a : b                // 等しい
a ! b                // 等しくない
a > b                // より大きい
a >: b               // 以上
a < b                // より小さい
a <: b               // 以下
a between x and y    // 範囲（両端を含む）
a ~ 'pattern'        // パターンマッチ（% と _ のワイルドカード）
a ~~ 'pattern'       // 大文字小文字を区別しないパターンマッチ
a in [x, y]          // コレクションに含まれる
a not in [x, y]      // コレクションに含まれない
a is null            // null チェック
a is not null        // 非 null チェック
a is empty           // 空チェック（コレクション/文字列）
a is not empty       // 非空チェック
```

### 優先順位

括弧を使用して評価順序を制御します：

```
a and (b or c)
(status : 'active' or status : 'pending') and year > 2020
```

## クエリ例

### 基本的なフィルタリング

```
// 単純な等価
?filter= status : 'active'

// 複数条件
?filter= year > 2020 and km < 50000

// 範囲（両端を含む）
?filter= year between 2020 and 2025

// OR 条件
?filter= color : 'red' or color : 'blue'
```

### リレーションの操作

```
// 関連エンティティのフィールドでフィルタ
?filter= brand.name : 'audi'

// ネストされたリレーション
?filter= brand.manufacturer.country : 'germany'

// リレーションの存在確認
?filter= brand is not null

// 複数のリレーション条件
?filter= brand.name : 'audi' and dealer.city : 'berlin'
```

### コレクション操作

```
// 値がリストに含まれるか確認
?filter= status in ['active', 'pending', 'review']

// コレクションのサイズ確認
?filter= size(accidents) > 2

// コレクションが空か確認
?filter= accidents is empty

// コレクションが空でないか確認
?filter= ratings is not empty
```

### 文字列マッチング

```
// ワイルドカード付きパターンマッチ（% = 任意の文字, _ = 1文字）
?filter= name ~ '%john%'

// 大文字小文字を区別しないパターンマッチ
?filter= name ~~ 'JOHN'

// 前方一致
?filter= email ~ 'admin%'

// 後方一致
?filter= filename ~ '%.pdf'

// パターンマッチング
?filter= code ~ 'PRD-____-2023'

// 複数パターン
?filter= name ~ ['%john%', '%doe%']

// 大文字小文字を区別しない複数パターン
?filter= name ~~ ['%JOHN%', '%DOE%']
```

### 日付フィルタリング

```
// 日付比較（形式は Spring の設定に依存）
?filter= createdAt > '2023-01-01'

// 日付範囲
?filter= createdAt > '2023-01-01' and createdAt < '2023-12-31'

// 日付範囲（between）
?filter= createdAt between '2023-01-01' and '2023-12-31'

// today() 関数を使用した相対日付
?filter= createdAt > today()
```

### 複雑なクエリ

```
// 優先順位付きのネストされた条件
?filter= (year > 2020 and km < 30000) or (year > 2018 and km < 10000)

// 異なる演算子の組み合わせ
?filter= brand.name in ['audi', 'bmw'] and year > 2020 and accidents is empty and color ! 'white'

// リレーション付きコレクションサイズ
?filter= size(owner.vehicles) > 3 and status : 'active'
```

### Null と空のチェック

```
// null チェック
?filter= deletedAt is null

// 非 null チェック
?filter= description is not null

// 空コレクション
?filter= tags is empty

// 非空コレクション
?filter= children is not empty
```

### JSON/JSONB フィルタリング

```
// キーパスで文字列値を抽出
?filter= jsonText(metadata, 'address', 'city') : 'New York'

// 値が存在するか確認
?filter= jsonText(data, 'status') in ['active', 'pending']

// 型キャストと組み合わせ
?filter= jsonText(data, 'age') > 18

// パターンマッチング付きのネストされたキー
?filter= jsonText(metadata, 'address', 'city') ~ '%York%'
```

## サポートされる型

すべてのモジュールで以下を処理します：
- プリミティブ型（int、long、double、boolean など）
- 文字列
- 列挙型
- 日付（LocalDate、LocalDateTime、Date、Instant など）
- コレクション（List、Set）
- 配列
- エンティティリレーション（@ManyToOne、@OneToMany、@ManyToMany）

日付の解析は Spring の `ConversionService` を使用します。設定で日付形式を変更できます。

## カスタム演算子

`FilterInfixOperator`、`FilterPrefixOperator`、`FilterPostfixOperator` を継承してカスタム演算子を定義します：

```java
@Component
public class ContainsOperator extends FilterInfixOperator {
    public ContainsOperator() {
        super("contains", 5);
    }
}
```

次に、各モジュールのプロセッサを実装します：

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
        // 実装
    }
}
```

自動設定または手動設定で演算子を登録します。

## カスタム関数

`FilterFunction` を継承してカスタム関数を定義します：

```java
@Component
public class LowerFunction extends FilterFunction {
    public LowerFunction() {
        super("lower");
    }
}
```

各モジュールのプロセッサを実装します：

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

## 設定

### デフォルトパラメータ名

デフォルトでは、フィルタは `filter` クエリパラメータから読み取られます。上書きするには：

```java
@GetMapping("/cars")
List<Car> search(@Filter(parameter = "q") Specification<Car> spec) {
    return repository.findAll(spec);
}
```

これで `?filter=year > 2020` の代わりに `?q=year > 2020` が使用できます。

### MongoDB のエンティティクラス

MongoDB ではエンティティクラスの明示的な指定が必要です：

```java
@GetMapping("/cars")
List<Car> search(@Filter(entityClass = Car.class) Query query) {
    return mongoTemplate.find(query, Car.class);
}
```

### オプショナルフィルタ

`Optional` を使用してフィルタパラメータの欠落を処理します：

```java
@GetMapping("/cars")
List<Car> search(@Filter Optional<Specification<Car>> spec) {
    return repository.findAll(spec.orElse(null));
}
```

### プログラム的フィルタ適用

Spring MVC アノテーションなしでフィルタを適用します：

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

## Spring 設定

Spring Filter は型変換に Spring の `ConversionService` を使用します。日付形式や列挙型の処理をカスタマイズするには：

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

## 高度な機能：ParseContext

`ParseContext` を使用すると、解析中にフィルタ式をインターセプトして変更できます。フィールドマッピングとノードマッピングの2つのフックを提供します。

### フィールドエイリアス

API のフィールド名をデータベースのフィールド名にマッピングします：

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

これで `?filter=price > 100` のようなクエリがデータベースレベルで自動的に `unitPrice > 100` になります。

### マルチテナンシー

すべてのクエリにテナントフィルタを自動注入します：

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

ユーザーは `status : 'active'` とクエリしますが、実際のクエリは `tenantId : 123 and status : 'active'` になります。

### セキュリティフィルタ

ユーザー権限に基づいて行レベルのセキュリティフィルタを注入します：

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

一般ユーザーは自動的に自分のドキュメントまたは部門のドキュメントに制限されます。管理者はすべてを見ることができます。

### 動的フィールドアクセス制御

ユーザーがフィルタできるフィールドを制限します：

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

管理者以外のユーザーが `?filter=salary > 50000` を試みると `SecurityException` が発生します。

### 論理削除のクエリ書き換え

論理削除されたレコードを自動的に除外します：

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

すべてのクエリに自動的に `deletedAt is null` が含まれます。

### 監査ログ

ユーザーコンテキスト付きですべてのフィルタクエリをログに記録します：

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

### フィールド名の正規化

異なる命名規則を処理します：

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

`userId` を `user` に、`userName` を `user_name` に自動変換します。

### 複数コンテキストの組み合わせ

複雑なシナリオのために複数の解析コンテキストを連結します：

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

1回のパスで複数の変換を適用します。

### リクエストスコープのコンテキスト

Spring のリクエストスコープを使用してコンテキスト認識の解析を行います：

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

コンテキストは自動的に現在のリクエストのユーザー情報を使用します。

## テスト

### Filter Builder を使用したユニットテスト

```java
@Test
void testFilterBuilder() {
    FilterNode filter = fb.field("year").greaterThan(fb.input(2020)).get();
    Specification<Car> spec = jpaConverter.convert(filter);

    List<Car> result = repository.findAll(spec);

    assertTrue(result.stream().allMatch(car -> car.getYear() > 2020));
}
```

### 統合テスト

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

### Predicate のテスト

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

## パフォーマンス

### JPA

JPA モジュールは最適化された Criteria クエリを生成します。データベースがフィルタリングを実行するため、パフォーマンスはネイティブ SQL クエリと同等です。フィルタ対象のフィールドに適切なインデックスを作成してください。

### MongoDB

MongoDB モジュールは集計パイプラインを生成します。パフォーマンスはインデックスに依存します。MongoDB の explain plan でクエリをプロファイリングしてください。

### Predicate

Predicate モジュールはメモリ内でフィルタリングします。パフォーマンスは O(n) で、n はコレクションのサイズです。以下に適しています：
- 小規模なコレクション
- キャッシュデータ
- 既に読み込まれているコレクション
- テスト

大規模なデータセットの場合は、JPA または MongoDB モジュールを使用してデータベースレベルでフィルタリングしてください。

## コントリビューション

プルリクエスト歓迎。フォーマットには [Google Java Style](https://github.com/google/styleguide/blob/gh-pages/eclipse-java-google-style.xml) を使用してください。

### コントリビューター

* [@marcopag90](https://github.com/marcopag90) と [@glodepa](https://github.com/glodepa) - MongoDB サポート
* [@sisimomo](https://github.com/sisimomo) - [JavaScript クエリビルダー](https://github.com/sisimomo/Spring-Filter-Query-Builder)
* [@68ociredef](https://github.com/68ociredef) - [Angular クエリビルダー](https://github.com/68ociredef/spring-filter-ng)

## 記事

* [Spring API でエンティティを簡単にフィルタする](https://torshid.medium.com/easily-filter-entities-in-your-spring-api-f433537cfd41)

## ライセンス

MIT License - [LICENSE](LICENSE) ファイルを参照してください。
