# 스프링 부트 실전 활용 마스터

# PART 1. 스프링 부트 웹 애플리케이션 만들기
## Mono
Mono는 0또는 1개의 원소만 담을 수 있는 리액티브 발행자(publisher)로서, 프로젝트 리액터에서 제공해주는 구현체다
프로젝트 리액터 개발 초기에 Mono의 필요성에 대한 고민 끝에 하나의 원소만 비동기적으로 반환하는 경우가 압도적으로 많음을 깨닫고
Mono를 추가하기로 했다
Mono는 함수형 프로그래밍 무기로 무장한 Future라고 생각해도 된다
리액티브 스트림은 배압과 지연을 지원한다

### Future는 제공해주지 않지만 Flux는 제공해주는 것
- 하나 이상의 Dish(요리) 포함 가능
- 각 Dish(요리)가 제공될 때 어떤 일이 발생하는지 지정 가능
- 성공과 실패의 두 가지 경로 모두에 대한 처리 방향 정의 가능
- 결과 폴링(poling) 불필요
- 함수형 프로그래밍 지원

Future는 정화하게 하나의 값을 제공하는 것이 목적
Flux는 다수의 값을 지원하는 것이 목적

## 1장에서 배운 내용
- 레스토랑 서빙 점원이 손님 및 주방과 어떻게 의사소통하는지 살펴보면서 리액티브 프로그래밍의 기초를 살펴봤다
- 리액티브 프로그래밍 개념을 스프링 웹플럭스 컨트롤러와 서비스에 적용해봤다
- 첫 번째 스프링 부트 애플리케이션을 실행하고 cURL을 사용해서 비동기 스트림으로 제공되는 요리가 사용되는 모습을 살펴봤다
- 첫 번째 타임리프 템플릿을 만들고 정적인 웹 페이지로 렌더링해서 화면에 표시했다

# PART 2. 스프링 부트를 활용한 데이터 엑세스
## 이커머스 도메인 정의
| 도메인 객체               | 설명                                             |
| ------------------------- | ------------------------------------------------ |
| 판매 상품(Inventory Item) | 일련번호, 가격, 설명 필요                        |
| 장바구니(Cart)            | 장바구니 식별자와 장바구니에 담긴 상품 목록 필요 |
| 구매 상품(Item in a Cart) | 장바구니에 담긴 판매 상품의 구매 수량 필요       |

## MongoOperation
수년 전에 스프링 팀은 JdbcTemplate에서 일부를 추출해서 JdbcOperations라는 인터페이스를 만들었다
인터페이스를 사용하면 계약과 세부 구현 내용을 분리할 수 있다
이 패턴은 스프링 포트폴리오에서 사용하는 거의 모든 템플릿에서 사용되고 있다
따라서 애플리케이션과 몽고디비 결합도를 낮추려면 MongoOperations 인터페이스를 사용하는 것이 좋다

## 스프링 데이터 몽고디비 쿼리 메소드 이름 규칙
| 쿼리 메소드                                                  | 설명                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| findBy**Description**                                        | description 값이 일치하는 데이터 질의                        |
| findBy**NameAndDescription**                                 | name 값과 description 값이 모두 일치하는 데이터 질의         |
| findBy**NameAndDistributorRegion**                           | name 값과 distributorRegion 값이 모두 일치하는 데이터 질의   |
| find**Top10**ByName<br />find**First10**ByName               | name 값이 일치하는 첫 10개의 데이터 질의                     |
| findByName**IgnoreCase**                                     | name 값이 대소문자 구분 없이 일치하는 데이터 질의            |
| findByNameAndDescription**AllIgnoreCase**                    | name 값과 description 값 모두 대소문자 구분 없이 일치하는 데이터 질의 |
| findByName**OrderByDescriptionAsc**                          | name 값이 일치하는 데이터를 description 값 기준 오름차순으로 정렬한 데이터 질의 |
| findByReleaseDate**Before**(Date date)                       | releaseDate 값이 date보다 이전인 데이터 질의                 |
| findByReleaseDate**After**(Date date)                        | releaseDate 값이 date 이후인 데이터 질의                     |
| findByAvailableUnits**GreaterThan**(int units)               | availableUnits 값이 units보다 큰 데이터 질의                 |
| findByAvailableUnits**GreaterThanEqual**(int units)          | availableUnits 값이 units보다 크거나 같은 데이터 질의        |
| findByAvailableUnits**LessThan**(int units)                  | availableUnits 값이 units보다 작은 데이터 질의               |
| findByAvailableUnits**LessThanEqual**(int units)             | availableUnits 값이 units보다 작거나 같은 데이터 질의        |
| findByAvailableUnits**Between**(int from, int to)            | availableUnits 값이 from과 to 사이에 있는 데이터 질의        |
| findByAvailableUnits**In**(Collection units)                 | availableUnits 값이 units 컬렉션에 포함돼 있는 데이터 질의   |
| findByAvailableUnits**NotIn**(Collection units)              | availableUnits 값이 unitss 컬렉션에 포함돼 있지 않은 데이터 질의 |
| fnidByName**NotNull**()<br />findByName**IsNotNull**()       | name 값이 null이 아닌 데이터 질의                            |
| fnidByName**Null**()<br />findByNameIs**Null**()             | name 값이 null인 데이터 질의                                 |
| fnidByName**Like**(String f)                                 | name 값이 문자열 f를 포함하는 데이터 질의                    |
| fnidByName**NotLike**(String f)<br />findByName**IsNotLike**(String f) | name 값이 문자열 f를 포함하지 않는 데이터 질의               |
| fnidByName**StartingWith**(String f)                         | name 값이 문자열 f로 시작하는 데이터 질의                    |
| fnidByName**EndingWith**(String f)                           | name 값이 문자열 f로 끝나는 데이터 질의                      |
| fnidByName**NotContaining**(String f)                        | name 값이 문자열 f를 포함하지 않는 데이터 질의               |
| fnidByName**Regex**(String pattern)                          | name 값이 pattern으로 표현되는 정규 표현식에 해당하는 데이터 질의 |
| fnidByLocation**Near**(Point p, Distance max)                | location 값이 p 지점 기준 거리 max 이내에서 가장 가까운 순서로 정렬된 데이터 질의 |
| fnidByLocation**Near**(Point p, Distance min, Distance max)  | location 값이 p 지점 기준 거리 min 이상 max 이내에서 가장 가까운 순서로 정렬된 데이터 질의 |
| fnidByLocation**Within**(Circle c)                           | location 값이 원 영역 c 안에 포함돼 있는 데이터 질의         |
| fnidByLocation**Within**(Box b)                              | location 값이 직사각형 영역 b 안에 포함돼 있는 데이터 질의   |
| fnidByActive**IsTrue**()                                     | active 값이 true인 데이터 질의                               |
| fnidByActive**IsFalse**()                                    | active 값이 false인 데이터 질의                              |
| fnidByLocation**Exists**(boolean e)                          | Location 속성의 존재 여부 기준으로 데이터 질의               |

## 몽고디비 리포지토리 메소드가 지원하는 반환 타입
- `Item(또는 primitive type)`
- `Iterable<Item>`
- `Iterator<Item>`
- `Collection<Item>`
- `List<Item>`
- `Optional<Item>(자바8 또는 Guava)`
- `Option<Item>(Scala 또는 Vavr)`
- `Stream<Item>`
- `Future<Item>`
- `CompletableFuture<Item>`
- `ListenableFuture<Item>`
- `@Async Future<Item>`
- `@Async CompletableFuture<Item>`
- `@Async ListenableFuture<Item>`
- `Slice<Item>`
- `Page<Item>`
- `GeoResult<Item>`
- `GeoResults<Item>`
- `GeoPage<Item>`
- `Mono<Item>`
- `Flux<Item>`

리액터 기반 프로그래밍에서는 호출자가 `subscribe()` 메소드를 호출할 수 있어야 하기 때문에
void 대신에 `Mono<Void>`를 반환타입을 사용함

## Example 쿼리
스프링 데이터 몽고디비는 Example 쿼리를 엄격한 타입(strictly-type) 방식으로 구현해서 `_class` 필드 정보가
합치되는 몽고디비 도큐먼트에 대해서만 Example 쿼리가 적용된다
이 타입 검사를 우회해서 모든 컬렉션에 대해 쿼리를 수행하려면 **ExampleMatcher** 대신 **UntypedExampleMatcher**를 사용해야 한다

## 쿼리 방법별 장단점
| 쿼리 방법             | 장점                                                         | 단점                                                         |
| --------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 표준 CRUD 메소드      | - 미리 정의돼 있음<br />- 소스 코드로 작성돼 있음<br />- 리액터 타입을 포함해서 다양한 반환타입 지원<br />- 데이터 스토어 간 호환성 | - 1개 또는 전부에만 사용 가능<br />- 도메인 객체별로 별도의 인터페이스 작성 필요 |
| 메소드 이름 기반 쿼리 | - 직관적<br />- 쿼리 자동 생성<br />- 리액터 타입을 포함한 다양한 반환타입 지원<br />- 여러 데이터 스토어에서 모두 지원 | - 도메인 객체마다 리포지토리 작성 필요<br />- 여러 필드와 조건이 포함된 복잡한 쿼리에 사용하면 메소드 이름이 매우 길어지고 불편 |
| Example 쿼리          | - 쿼리 자동 생성<br />- 모든 쿼리 조건을 미리 알 수 없을 때 유용<br />- JPA, 레디스(Redis)에서도 사용 가능 | - 도메인 객체마다 리포지토리 작성 필요                       |
| MongoOperations       | - 데이터 스토어에 특화된 기능까지 모두 사용 가능<br />- 도메인 객체마다 별도의 인터페이스 작성 불필요 | - 데이터 스토어에 종속적                                     |
| `@Query` 사용 쿼리    | - 몽고QL 사용 가능<br />- 긴 메소드 이름 불필요<br />- 모든 데이터 스토어에서 사용 가능 | - 데이터 스토어에 종속적                                     |
| 평문형 API            | - 직관적<br />- 도메인 객체마다 별도의 인터페이스 작성 불필요 | - 데이터 스토어에 종속적<br />- JPA와 레디스에서도 사용할 수 있지만 호환은 안 됨 |

## 2장에서 배운내용
- 완전한 리액티브 데이터 스토어에 필요한 조건
- 이커머스 애플리케이션 도메인 객체 정의
- 객체 저장 및 조회에 사용할 리포지토리 생성
- 커스텀 쿼리를 작성하는 여러 가지 방식
- 앞에서 다룬 모든 내용을 서비스에 옮겨 담아서 웹 계층과 분리하는 방법

# PART 3. 스프링 부트 개발자 도구

## devtools
```groovy
developmentOnly 'org.springframework.boot:spring-boot-devtools'
```

```yaml
  devtools:
    restart:
      # 재시작 유발 배제 경로 지정
      exclude: static/**,public/**
#      # 자동재시작 사용여부
#      enabled: true
#      # classpath 감지 주기 설정(선택사항)
#      # 빌드하는 시간이 오래걸려 재시작 주기를 길게 가져가고 싶다면 아래의 값을 이용해 갱신
#      # poll-interval 값은 항상 quiet-period 보다 커야한다
#      #poll-interval: 2s
#      #quiet-period: 1s
```

1. cmd + shift + A - Registry - compiler.automake.allow.when.app.running(체크)
2. Preferences - Build project automatically(체크)
3. 크롬 확장 프로그램(RemoteLiveReload) 설치

## 개발에서 Thmeleaf 캐시 비활성화

```yaml
spring:
  thymeleaf:
    # 개발환경에서 캐시 기능 비활성화
    cache: false
```

## 로깅

```yaml
logging:
  level:
    web: debug
```

## 리액터 플로우 디버깅

### Hooks.onOperatorDebug() 사용 스택 트레이스

리액터가 처리 흐름 조립 시점에서 호출부 세부정보를 수집하고 구독해서 실행되는 시점에 세부정보를 넘겨줌

리액터가 스레드별 스택 세부정보를 스레드 경계를 넘어서 전달하는 과정에는 굉장히 많은 비용이 든다
성능 문제를 일으킬 수 있으므로 실제 운영환경 또는 실제 벤치마크에서는 호출해서는 안됨

```java
public class ReactorDebuggingExample {

    public static void main(String[] args) {

        Hooks.onOperatorDebug(); // 리액터의 백트레이싱(backtracing) 활성화

        Mono<Integer> source;
        if (new Random().nextBoolean()) {
            source = Flux.range(1, 10).elementAt(5);
        } else {
            source = Flux.just(1, 2, 3, 4).elementAt(5); //89행
        }

        source
            .subscribeOn(Schedulers.parallel())
            .block(); //93행
    }
}
```

```
Assembly trace from producer [reactor.core.publisher.MonoElementAt] :
	reactor.core.publisher.Flux.elementAt(Flux.java:4859)
	com.greglturnquist.hackingspringboot.reactive.ReactorDebuggingExample.main(ReactorDebuggingExample.java:23)
Error has been observed at the following site(s):
	|_   Flux.elementAt ⇢ at com.greglturnquist.hackingspringboot.reactive.ReactorDebuggingExample.main(ReactorDebuggingExample.java:23)
	|_ Mono.subscribeOn ⇢ at com.greglturnquist.hackingspringboot.reactive.ReactorDebuggingExample.main(ReactorDebuggingExample.java:27)
```

## 블록하운드를 사용한 블로킹 코드 검출
개발자가 직접 작성한 코드뿐만 아니라 서드파티 라이브러리에 사용된 블로킹 메소드 호출을 모두 찾아내서 알려주는 자바 에이전트다
블록하운드는 JDK 자체에서 호출되는 블로킹 코드까지도 찾아낸다

블록하운드를 애플리케이션에 심은 후에 동작하지 않도록 제거하는 것은 불가능하다
블록하운드는 자바 에이전트 API를 사용하기 때문에 심어진 채로 애플리케이션이 실행된 후에는 제거할 수 없다
애플리케이션을 종료하고 `install()` 호출 코드를 제거해서 아예 심지 않고 애플리케이션을 다시 시작하는 것이 블록하운드 기능을 제거할 수 있는 유일한 방법이다

```groovy
implementation 'io.projectreactor.tools:blockhound:1.0.6.RELEASE'
```

```java
@SpringBootApplication
public class HackingSpringBootApplicationBlockHoundCustomized {

    public static void main(String[] args) {
        BlockHound.builder() // <1> SpringApplication.run()보다 먼저 실행된다
                .allowBlockingCallsInside( //
                        TemplateEngine.class.getCanonicalName(), "process") // <2> 허용 리스트에 추가한다
                .install(); // <3>호출하면 커스텀 설정이 적용된 블록하운드가 애플리케이션에 심어진다

        SpringApplication.run(HackingSpringBootApplicationBlockHoundCustomized.class, args);
    }
}
```

## 3장에서 배운 내용
- 스프링 부트 개발자 도구를 프로젝트에 추가하는 방법
- 소스 코드 변경 시 애플리케이션 자동 재시작
- 개발 모드로 실행 시 캐시 동작을 막는 방법
- 스프링 부트에 내장된 라이브 리로드 기능
- 스레드 경계를 넘는 스택 트레이스 설정
- 리액터의 로깅 연산자를 사용해서 로그 정보와 리액티브 스트림 시그널을 모두 로그에 남기는 방법
- JDK 메소드까지 포함해서 블로킹 코드 호출을 검출하는 블록하운드 사용법

# PART 4. 스프링 부트 테스트

```java
// 테스트 대상 클래스 아무런 애너테이션도 붙지 않으며 테스트할 때 초기화된다
InventoryService inventoryService;

// 테스트 대상이 아니므로 가짜 객체를 만들고 @MockBean 애너테이션을 붙여 스프링 빈으로 등록한다
// @MockBean 애너테이션을 보면 Mockito를 사용해서 가짜 객체를 만들고 이를 애플리케이션 컨텍스트에 빈으로 추가한다
@MockBean private ItemRepository itemRepository;
@MockBean private CartRepository cartRepository;
```

위의 코드는 아래의 코드와 같다

```java
@BeforeEach
void setUp() {
    itemRepository = mock(ItemRepository.class)
    cartRepository = mock(CartRepository.class);
}
```

리액티브 코드를 테스트할 때 핵심은 기능만을 검사하는 게 아니라 리액티브 스트림 시그널도 함께 검사해야 한다는 점이다
리액티브 스트림은 `onSubscribe`, `OnNext`, `onError`, `onComplete`를 말한다
`onNext`와 `onComplete`가 모두 발생하면 **successful path**라고 부른다

- `StepVerifier`: 결괏값을 얻기 위해 블로킹 방식으로 기다리는 대신에 리액터의 테스트 도구가 대신 구독을 하고 값을 확인할 수 있게 해준다
- `expectNextMatches()`: 값을 검증할 수 있는 적절한 함수를 `expectNextMatches()`에 람다식 인자로 전달해준다
- `verifyComplete()`: 호출해서 onComplete 시그널을 확인

테스트 대상 메소드 호출부를 테스트 코드 맨 위에 배치하고 리액터의 `as()` 연산자를 사용해서
테스트 대상 메소드 결괏값을 StepVerifier로 흘려보내는 탑 레벨 방식으로 작성하면
테스트 코드의 의도가 더 분명히 드러난다

리액터의 `StepVerifier`를 사용하는 모든 테스트 케이스에서 `onSubscribe` 시그널이 발생했다
하지만 `doOnSubscribe()`를 사용해서 `onSubscribe` 시그널 발생 시 특정 동작을 수행하도록 작성하지 않았다면
`onSubscribe` 시그널 발생은 자명하므로 별도로 테스트할 필요가 없다
`doOnSubscribe()`에 구독 시 실행돼야 하는 기능을 작성했다면 `expectSubscription()`을 사용해서 구독에 대한 테스트도 반드시 추가해야 한다

## 스프링 부트 슬라이스 테스트

단위 테스트와 종단 간 통합 테스트 중간 정도에 해당하는 테스트

- `@AutoConfigureRestDocs`
- `@DataJdbcTest`
- `@DataJpaTest`
- `@DataLdapTest`
- `@DataMongoTest`
- `@DataNeo4jTest`
- `@DataRedisTest`
- `@JdbcTest`
- `@JooqTest`
- `@JsonTest`
- `@RestClientTest`
- `@WebFluxTest`
- `@WebMvcTest`

### 몽고디비 슬라이스 테스트
스프링 데이터 몽고디비 관련 모든 기능을 사용할 수 있게 하고 그외 `@Component` 애너테이션이 붙어있는 다른 빈 정의를 무시한다
종단간 테스트와 마찬가지로 실제 데이터 베이스 연산을 포함하면서도 테스트 수행속도 개선 효과가 꽤 크다
테스트 케이스에서 가짜 객체를 전혀 사용하지 않으므로 테스트 결과에 대한 자신감은 더 높아지면서 테스트 성능은 거의 60%나 상승했다

### 블록하운드 사용 단위 테스트
블록하운드가 검출해 내는 것

- `java.lang.Thread#sleep()`
- 여러 가지 Socket 및 네트워크 연산
- 파일 접근 메소드 일부

검출될 수 있는 전체 메소드 목록은 `BlockHound` 클래스 안에 있는 `Builder` 클래스의 `blockingMethods` 해시맵에서 확인할 수 있음

## 4장에서 배운 내용

- `StepVerifier`를 사용해서 리액티브 테스트 작성
- 리액티브 스트림보다 하부 계층에 위치하는 도메인 객체를 간단하게 테스트
- `@MockBean`을 사용해서 만든 가짜 협력자와 `StepVerifier`를 사용해서 리액티브 서비스 테스트
- 리액티브 결과뿐 아니라 complete와 error 같은 리액티브 스트림 시그널도 검증
- 스프링 부트를 사용해서 완전한 기능을 갖춘 웹 컨테이너 실행
- `@WebFluxTest`나 `@DataMongoTest`를 사용해서 애플리케이션의 일부 계층만 더 빠르게 테스트할 수 있는 슬라이스 테스트
- 리액터 블록하운드 모듈을 사용해서 블로킹 코드 검출

# PART 5. 스프링 부트 운영
- 우버 JAR 파일을 만들고 운영환경에 배포
- 컨테이너 생성을 위한 계층형 Dockerfile 생성
- Dockerfile을 사용하지 않는 컨테이너 생성
- 애플리케이션 운영을 도와주는 스프링 부트 액추에이터
- 운영을 위해 사용할 기능과 사용하지 않을 기능 분별
- 애플리케이션 버전 세부 내용 추가
- 관리 서비스 라우트 지정

## 우버 JAR 배포

실행 가능한 JAR 파일 생성
```bash
./gradlew :bootJar
```
```bash
1:13:06 오전: Executing task 'bootJar'...

> Task :compileJava
> Task :processResources
> Task :classes
> Task :bootJarMainClassName
> Task :bootJar

BUILD SUCCESSFUL in 1s
4 actionable tasks: 4 executed
1:13:08 오전: Task execution finished 'bootJar'.
```

실행 가능한 JAR 파일 실행
```bash
java -jar build/libs/hacking-spring-boot-reactive-0.0.1-SNAPSHOT.jar
```

### JAR 파일 내부에 포함된 항목
- JAR 파일을 읽고 JAR 안에 포함돼 있는 JAR 파일에 있는 클래스를 로딩하기 위한 스프링 부트 커스텀 코드
- 애플리케이션 코드
- 사용하는 서드파티 라이브러리 전부

자바의 스펙에 따르면 JAR 파일 안에서 다른 JAR 파일을 읽을 수 없다  
그래서 스프링 부트는 중첩된 JAR 파일 안에 있는 클래스를 로딩하기 위해 커스텀 코드를 사용한다

## 도커 배포
애플리케이션을 컨테이너화하는 가장 간단한 Dockerfile
```dockerfile
FROM adoptopenjdk/openjdk11:latest
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

### 계층화 적용

#### 빌드 파일에서 계층형 JAR 사용하도록 지정

Maven 설정
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <layers>
                    <enabled>true</enabled>
                </layers>
            </configuration>
        </plugin>
    </plugins>
</build>
```

Gradle
```groovy
bootJar {
    layered {
        enabled = true
    }
}
```

#### 계층적 JAR clean 후 build

Maven
```bash
./mvnw clean package
```

Gradle
```bash
./gradlew :clean :build
```

#### 새 계층 확인
```bash
java -Djarmode=layertools -jar build/libs/hacking-spring-boot-reactive-0.0.1-SNAPSHOT.jar list

dependencies
spring-boot-loader
snapshot-dependencies
application
```

#### 계층화 적용 Dockerfile
```dockerfile
# 빌더(builder)로 사용할 컨테이너를 만든다
FROM adoptopenjdk/openjdk11:latest as builder
WORKDIR application
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
# extract 명령으로 레이어를 추출한다
RUN java -Djarmode=layertools -jar app.jar extract

# 두 번째 컨테이너를 만든다
FROM adoptopenjdk/openjdk11:latest
WORKDIR application
# 빌더 컨테이너에서 추출한 여러 레이어를 두 번째 컨테이너에 복사한다
# COPY 명령에는 도커의 계층 캐시 알고리즘이 적용된다
# 그래서 서드파티 라이브러리는 캐시될 수 있다
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
# snapshot 의존관계가 없어 해당 계층이 만들어지지 않으면 주석 처리
#COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./
# java -jar가 아니라 스프링 부트의 커스텀 런처(custom launcher)로 애플리케이션을 실행한다
# 이 린처는 애플리케이션 시작 시 불필요한 JAR 파일 압축 해제를 하지 않으므로 효율적이다
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
```


#### 컨테이너 이미지 빌드
```bash
docker build . --tag hacking-spring-boot-reactive
```

소스 코드를 변경한 후 다시 빌드하면 변경이 발생한 계층만 새로 빌드됨
변경된 application 계층 외의 나머지 계층은 캐시를 사용함

#### 도커로 애플리케이션 실행
```bash
docker run -it -p 8080:8080 hacking-spring-boot-reactive:latest
```

#### spring-boot로 도커 이미지 빌드

Maven `spring-boot:build-image`로 도커 이미지 빌드
```bash
./mvnw spring-boot:build-image
```
Gradle `:bootBuildImage`로 도커 이미지 빌드
```bash
./gradlew :bootBuildImage
```

```bash
> Task :bootBuildImage
Building image 'docker.io/library/hacking-spring-boot-reactive:0.0.1-SNAPSHOT'
 > Pulling builder image 'docker.io/paketobuildpacks/builder:base' ..................................................
 > Pulled builder image 'paketobuildpacks/builder@sha256:deeb603fef3faa8ef43b04f6d7d284a78a136a0872fcf7d6091f8d5a0a8fecc1'
 > Pulling run image 'docker.io/paketobuildpacks/run:base-cnb' ..................................................
 > Pulled run image 'paketobuildpacks/run@sha256:4a464d8cba60f5c5c6885fa619ea6ce9d3201e1fb0684aaa1d4514b326006b22'
 > Executing lifecycle version v0.11.4
 > Using build cache volume 'pack-cache-4bb50ae456fc.build'

 > Running creator
    [creator]     ===> DETECTING
    [creator]     5 of 18 buildpacks participating
    [creator]     paketo-buildpacks/ca-certificates   2.4.1
    [creator]     paketo-buildpacks/bellsoft-liberica 8.5.0
    [creator]     paketo-buildpacks/executable-jar    5.2.1
    [creator]     paketo-buildpacks/dist-zip          4.2.1
    [creator]     paketo-buildpacks/spring-boot       4.6.0
    [creator]     ===> ANALYZING
    [creator]     Previous image with name "docker.io/library/hacking-spring-boot-reactive:0.0.1-SNAPSHOT" not found
    [creator]     ===> RESTORING
    [creator]     ===> BUILDING
    [creator]     
    [creator]     Paketo CA Certificates Buildpack 2.4.1
    [creator]       https://github.com/paketo-buildpacks/ca-certificates
    [creator]       Launch Helper: Contributing to layer
    [creator]         Creating /layers/paketo-buildpacks_ca-certificates/helper/exec.d/ca-certificates-helper
    [creator]     
    [creator]     Paketo BellSoft Liberica Buildpack 8.5.0
    [creator]       https://github.com/paketo-buildpacks/bellsoft-liberica
    [creator]       Build Configuration:
    [creator]         $BP_JVM_TYPE                 JRE             the JVM type - JDK or JRE
    [creator]         $BP_JVM_VERSION              11.*            the Java version
    [creator]       Launch Configuration:
    [creator]         $BPL_HEAP_DUMP_PATH                          write heap dumps on error to this path
    [creator]         $BPL_JVM_HEAD_ROOM           0               the headroom in memory calculation
    [creator]         $BPL_JVM_LOADED_CLASS_COUNT  35% of classes  the number of loaded classes in memory calculation
    [creator]         $BPL_JVM_THREAD_COUNT        250             the number of threads in memory calculation
    [creator]         $JAVA_TOOL_OPTIONS                           the JVM launch flags
    [creator]       BellSoft Liberica JRE 11.0.12: Contributing to layer
    [creator]         Downloading from https://github.com/bell-sw/Liberica/releases/download/11.0.12+7/bellsoft-jre11.0.12+7-linux-amd64.tar.gz
    [creator]         Verifying checksum
    [creator]         Expanding to /layers/paketo-buildpacks_bellsoft-liberica/jre
    [creator]         Adding 129 container CA certificates to JVM truststore
    [creator]         Writing env.launch/BPI_APPLICATION_PATH.default
    [creator]         Writing env.launch/BPI_JVM_CACERTS.default
    [creator]         Writing env.launch/BPI_JVM_CLASS_COUNT.default
    [creator]         Writing env.launch/BPI_JVM_SECURITY_PROVIDERS.default
    [creator]         Writing env.launch/JAVA_HOME.default
    [creator]         Writing env.launch/JAVA_TOOL_OPTIONS.append
    [creator]         Writing env.launch/JAVA_TOOL_OPTIONS.delim
    [creator]         Writing env.launch/MALLOC_ARENA_MAX.default
    [creator]       Launch Helper: Contributing to layer
    [creator]         Creating /layers/paketo-buildpacks_bellsoft-liberica/helper/exec.d/active-processor-count
    [creator]         Creating /layers/paketo-buildpacks_bellsoft-liberica/helper/exec.d/java-opts
    [creator]         Creating /layers/paketo-buildpacks_bellsoft-liberica/helper/exec.d/jvm-heap
    [creator]         Creating /layers/paketo-buildpacks_bellsoft-liberica/helper/exec.d/link-local-dns
    [creator]         Creating /layers/paketo-buildpacks_bellsoft-liberica/helper/exec.d/memory-calculator
    [creator]         Creating /layers/paketo-buildpacks_bellsoft-liberica/helper/exec.d/openssl-certificate-loader
    [creator]         Creating /layers/paketo-buildpacks_bellsoft-liberica/helper/exec.d/security-providers-configurer
    [creator]         Creating /layers/paketo-buildpacks_bellsoft-liberica/helper/exec.d/security-providers-classpath-9
    [creator]       Java Security Properties: Contributing to layer
    [creator]         Writing env.launch/JAVA_SECURITY_PROPERTIES.default
    [creator]         Writing env.launch/JAVA_TOOL_OPTIONS.append
    [creator]         Writing env.launch/JAVA_TOOL_OPTIONS.delim
    [creator]     
    [creator]     Paketo Executable JAR Buildpack 5.2.1
    [creator]       https://github.com/paketo-buildpacks/executable-jar
    [creator]       Class Path: Contributing to layer
    [creator]         Writing env/CLASSPATH.delim
    [creator]         Writing env/CLASSPATH.prepend
    [creator]       Process types:
    [creator]         executable-jar: java org.springframework.boot.loader.JarLauncher (direct)
    [creator]         task:           java org.springframework.boot.loader.JarLauncher (direct)
    [creator]         web:            java org.springframework.boot.loader.JarLauncher (direct)
    [creator]     
    [creator]     Paketo Spring Boot Buildpack 4.6.0
    [creator]       https://github.com/paketo-buildpacks/spring-boot
    [creator]       Creating slices from layers index
    [creator]         dependencies
    [creator]         spring-boot-loader
    [creator]         snapshot-dependencies
    [creator]         application
    [creator]       Launch Helper: Contributing to layer
    [creator]         Creating /layers/paketo-buildpacks_spring-boot/helper/exec.d/spring-cloud-bindings
    [creator]       Spring Cloud Bindings 1.8.0: Contributing to layer
    [creator]         Downloading from https://repo.spring.io/release/org/springframework/cloud/spring-cloud-bindings/1.8.0/spring-cloud-bindings-1.8.0.jar
    [creator]         Verifying checksum
    [creator]         Copying to /layers/paketo-buildpacks_spring-boot/spring-cloud-bindings
    [creator]       Web Application Type: Contributing to layer
    [creator]         Reactive web application detected
    [creator]         Writing env.launch/BPL_JVM_THREAD_COUNT.default
    [creator]       4 application slices
    [creator]       Image labels:
    [creator]         org.springframework.boot.version
    [creator]     ===> EXPORTING
    [creator]     Adding layer 'paketo-buildpacks/ca-certificates:helper'
    [creator]     Adding layer 'paketo-buildpacks/bellsoft-liberica:helper'
    [creator]     Adding layer 'paketo-buildpacks/bellsoft-liberica:java-security-properties'
    [creator]     Adding layer 'paketo-buildpacks/bellsoft-liberica:jre'
    [creator]     Adding layer 'paketo-buildpacks/executable-jar:classpath'
    [creator]     Adding layer 'paketo-buildpacks/spring-boot:helper'
    [creator]     Adding layer 'paketo-buildpacks/spring-boot:spring-cloud-bindings'
    [creator]     Adding layer 'paketo-buildpacks/spring-boot:web-application-type'
    [creator]     Adding 5/5 app layer(s)
    [creator]     Adding layer 'launcher'
    [creator]     Adding layer 'config'
    [creator]     Adding layer 'process-types'
    [creator]     Adding label 'io.buildpacks.lifecycle.metadata'
    [creator]     Adding label 'io.buildpacks.build.metadata'
    [creator]     Adding label 'io.buildpacks.project.metadata'
    [creator]     Adding label 'org.springframework.boot.version'
    [creator]     Setting default process type 'web'
    [creator]     Saving docker.io/library/hacking-spring-boot-reactive:0.0.1-SNAPSHOT...
    [creator]     *** Images (3b5a539f8eb8):
    [creator]           docker.io/library/hacking-spring-boot-reactive:0.0.1-SNAPSHOT

Successfully built image 'docker.io/library/hacking-spring-boot-reactive:0.0.1-SNAPSHOT'


BUILD SUCCESSFUL in 53s
5 actionable tasks: 1 executed, 4 up-to-date
```

위의 명령을 실행하면 스프링 부트가 Paketo buildpack 프로젝트(https://paketo.io)에서 빌드팩을 가져와서 도커 컨테이너 이미지를 빌드함
이 과정에서 Dockerfile은 전혀 필요하지 않다

수동으로 컨테이너 이미지를 빌드할 수 있고, 프로젝트 빌드할 때 마다 컨테이너 이미지도 자동으로 빌드할 수 있다

### Dockerfile 이미지와 Paketo 이미지
| 방식                          | 장점                                                         | 단점                                                         |
| ----------------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 계층 기반 Dockerfile 이미지   | - Dockerfile을 직접 작성하므로 이미지 빌드 전체 과정 제어 가능<br />- 스프링 부트에서 계층화를 제공하므로 빌드 과정 최적화 가능 | - 컨테이너 직접 관리<br />- 컨테이너를 빌드 과정이 완전하지 않으면 보안에 취약한 계층 존재 위험 |
| Packet build pack 기반 이미지 | - Dockerfile을 직접 다룰 필요 없음<br />- 최신 패치와 SSL을 포함한 업계 표준 컨테이너 기술이 빌드 과정에 포함<br />- 개발에 더 집중 가능 | - Dockerfile에 비해 제어할 수 있는 것이 적음                 |

컨테이너화된 애플리케이션을 DockerHub(https://hub.docker.com) 같은 컨테이너 저장소에 업로드할 수 있음

Jenkins, Concource등의 지속적 통합 도구로 모든 과정을 자동화할 수도 있음

## 운영 애플리케이션 관리

---

### 애플리케이션 정상상태 점검: /actuator/health

호출 결과
```json
{
  "status": "UP"
}
```

서버 상태 세부정보 표시 설정
```yaml
# 액추에이터 설정
management:
  endpoint:
    health:
      # 서버 상태 세부정보 표시 설정
      show-details: always
```

```json
{
  "status": "UP",
  "components": {
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 1000240963584,
        "free": 759949463552,
        "threshold": 10485760,
        "exists": true
      }
    },
    "mongo": {
      "status": "UP",
      "details": {
        "version": "3.5.5"
      }
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

스프링 부트 액추에이터는 자동설정 정보를 사용해서 다음 정보를 반환한다

- 몽고디비 상태 및 버전 정보
- 디스크 상태 및 용량 정보

Redis, Cassandra, RabbitMQ, 관계형DB, 이메일등 다른 모듈을 스프링 부트와 함께 사용하면 스프링 부트 액추에이터가
해당 모듈의 **HealthIndicator** 구현체를 찾아 등록한다

각 구현체는 `UP`, `DOWN`, `OUT_OF_SERVICE`, `UNKNOWN` 중 하나를 **status** 값으로 반환한다
모든 개별 컴포넌트의 **status**에 따라 JSON  결과 최상위 **status**의 결괏값이 정해진다

### 애플리케이션 상세정보: /actuator/info

#### 애플리케이션 버전 정보 추가

Maven
```yaml
# 애플리케이션 버전 정보 추가
info:
  project:
    version: @project.version@
  java:
    version: @java.version@
  spring:
    framework:
      version: @spring-framework.version@
    data:
      version: @spring-data-bom.version@
```

Gradle  
참고: https://nevercaution.github.io/spring-boot-use-gradle-value/  
참고: https://tristanfarmer.dev/blog/gradle_property_expansion_spring_boot  
참고: https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#howto.properties-and-configuration
gradle 에서 properties 정보를 사용하기 위해 아래와 같이 설정
```groovy
buildscript {
    ext {
        springBootVersion = '2.5.4'
    }
}

processResources {
    filesMatching('application.yml') {
        expand(project.properties)
    }
}
```

```yaml
# 애플리케이션 버전 정보 추가
info:
  application:
    name: ${rootProject.name}
    group: ${group}
    version: ${version}
    springProperty: \${spring-boot.version}
  java:
    version: \${java.version}
  spring:
    boot:
      version: ${ext.springBootVersion}
```


#### Git 커밋과 브랜치 정보 확인

Gradle  
https://plugins.gradle.org/plugin/com.gorylenko.gradle-git-properties
```groovy
plugins {
    id "com.gorylenko.gradle-git-properties" version "2.3.1"
}
```

MAVEN
https://mvnrepository.com/artifact/pl.project13.maven/git-commit-id-plugin
```xml
<plugin>
    <groupId>pl.project13.maven</groupId>
    <artifactId>git-commit-id-plugin</artifactId>
</plugin>
```

http://localhost:8080/actuator/info 실행 결과
```
{
  "project": {
    "name": "hacking-spring-boot-reactive",
    "group": "com.greglturnquist",
    "version": "0.0.1-SNAPSHOT"
  },
  "java": {
    "version": "11.0.12"
  },
  "spring": {
    "boot": {
      "version": "2.5.4"
    }
  },
  "git": {
    "branch": "master",
    "commit": {
      "id": "450e316",
      "time": "2021-09-12T09:49:50Z"
    }
  },
  "build": {
    "artifact": "hacking-spring-boot-reactive",
    "name": "hacking-spring-boot-reactive",
    "time": "2021-09-14T17:37:41.420Z",
    "version": "0.0.1-SNAPSHOT",
    "group": "com.greglturnquist"
  }
}
```

### 로깅 정보 엔드포인트: /actuator/loggers
- ROOT 로거는 스프링 부트에 의해 INFO 레벨로 기본으로 추가된다
- 다른 정책으로 로그 레벨을 변경하지 않았으므로 실제 적용 레벨(effective level) 값도 INFO다
- 애플리케이션 최상위 패키지인 com은 로그 레벨이 명시적으로 지정되지 않았다
- com 패키지에 대한 로그 레벨이 INFO로 지정됐다

#### 스프링 부트 액추에이터로 로그 레벨 수정
```bash
curl -v -H 'Content-Type: application/json' -d '{"configuredLevel": "TRACE"}' http://localhost:8080/actuator/loggers/com.greglturnquist/
```

- 로그 레벨을 변경할 수 있는 액추에이터 엔드포인트는 `/actuator/loggers/{package}`
- `Content-Type`은 반드시 `application/json` 이어야 한다
- 저장에 사용되는 데이터는 반드시 `{"configuredLevel": "로그레벨값"}`으로 전송돼야 한다

로그 레벨 변경 결과 확인
```
"com.greglturnquist": {
  "configuredLevel": "TRACE",
  "effectiveLevel": "TRACE"
}
```

#### 로그 레벨 지정 해제
```bash
curl -v -H 'Content-Type: application/json' -d '{"configuredLevel": null}' http://localhost:8080/actuator/loggers/com.greglturnquist/
```

원래대로 복원된 로그 레벨
```
"com.greglturnquist": {
  "configuredLevel": null,
  "effectiveLevel": "INFO"
},
```

## 다양한 운영 데이터 확인
### 스레드 정보 확인: /actuator/threaddump

리액터 스레드 정보
```
{
  "threads": [
    {
      "threadName": "Reference Handler",
      "threadId": 2,
      "blockedTime": -1,
      "blockedCount": 5,
      "waitedTime": -1,
      "waitedCount": 0,
      "lockName": null,
      "lockOwnerId": -1,
```

### 힙 정보 확인: /actuator/heapdump

`/actuator/heapdump` 명령으로 hprof 파일 다운로드 후 확장자를 지우고 아래의 명령을 실행
```bash
jhat ~/Downloads/heapdump
```

heapdump로 확인 가능한 데이터
- 힙 히스토그램
- 플랫폼 포함 모든 클래스의 인스턴스 개수
- 플랫폼 제외 모든 클래스의 인스턴스 개수

jhat 명령은 JDK에 포함되어 있음  
더 자세한 분석을 위해 VisualVM(https://visualvm.github.io) 설치해서 사용

sdkman(https://sdkman.io) 사용시 아래와 같이 설치
```bash
sdk list visualvm
sdk install visualvm 2.0.6
visualvm --jdkhome $JAVA_HOME
```

비주얼 VM으로 힙 덤프 파일 읽기
1. File - Load
2. 다운로드한 힙 덤프 파일 폴더 탐색
3. 힘덤프 파일 선택 및 열기

### HTTP 호출 트레이싱: /actuator/httptrace
- 가장 많이 사용되는 클라이언트 유형
- 어떤 언어로 된 요청이 많은지, 세계화(i18n)가 필요한지
- 가장 많이 요청되는 엔드포인트
- 요청이 가장 많이 발생하는 지리적 위치

#### 인메모리 기반 HttpTraceRepository 빈 등록
```java
@Bean
HttpTraceRepository traceRepository() {
    return new InMemoryHttpTraceRepository();
}
```

/actuator/httptrace 엔드포인트를 자동으로 활성화 시킴  
스프링 웹플럭스와 연동해서 모든 웹 요청을 추적하고 로그를 남긴다

다음과 같은 정보가 포함돼 있다
- 타임스탬프
- 보안 상세정보
- 세션 ID
- 요청 상세정보(HTTP 메소드, URI, 헤더)
- 응답 상세정보(HTTP 상태 코드, 헤더)
- 처리 시간(밀리초)

메모리 기반 리포지토리이므로 다음과 같은 특징이 있음
- 트레이스 정보는 현재 인스턴스에만 존재한다  
  로드밸런서 뒤에 여러 대의 인스턴스가 존재한다면 인스턴스마다 자기 자신에게 들어온 요청에 대한 트레이스 정보가 생성된다
- 현재 인스턴스를 재시작하면 그동안의 트레이스 정보는 모두 소멸된다


### 몽고디비에 트레이스 정보 저장

요구사항  
- 애플리케이션이 재시작되도 트레이스 정보는 유지돼야 한다
- 모든 인스턴스에서 발생하는 트레이스 정보가 중앙화된 하나의 데이터 스토어에 저장돼야 한다

위의 요구사항을 충족하려면 인스턴스 외부에 있는 중앙화된 데이터베이스가 필요

#### HttpTrace 객체를 몽고디비에 저장하기 위해 사용할 래퍼 클래스
```java
public class HttpTraceWrapper {
  // 래퍼 클래스에는 스프링 데이터 커먼즈의 @Id 애너테이션이 붙은 id 속성이 있다
  // 이 id 값은 인스턴스 식별자로 사용된다
  private @Id String id;
  // 저장할 트레이스 정보를 담고 있는 실제 HttpTrace 객체가 들어 있다
  private HttpTrace httpTrace; // <2>
  // 저장할 HttpTrace 객체를 생성자로 받아들인다
  // id 값은 초기화하지 않으므로 null이지만 저장할 때 스프링 데이터 몽고디비가 새로운 id 값을 생성해서 저장한다
  public HttpTraceWrapper(HttpTrace httpTrace) { // <3>
    this.httpTrace = httpTrace;
  }
  // HttpTrace 정보를 읽으려면 게터 메소드가 있어야 한다
  public HttpTrace getHttpTrace() { // <4>
    return httpTrace;
  }
}
```

#### HttpTraceWrapperRepository 작성
```java
public interface HttpTraceWrapperRepository extends Repository<HttpTraceWrapper, String> {
    // findAll() 메소드는 저장된 모든 HttpTraceWrapper를 자바 11의 스트림으로 반환한다
    Stream<HttpTraceWrapper> findAll();
    // save() 메소드는 HttpTraceWrapper 객체를 저장한다
    void save(HttpTraceWrapper trace);
}
```

#### 스프링 데이터 리포지토리를 사용하는 HttpTraceRepository 구현 클래스
```java
public class SpringDataHttpTraceRepository implements HttpTraceRepository {

    private final HttpTraceWrapperRepository repository;

    public SpringDataHttpTraceRepository(HttpTraceWrapperRepository repository) {
        // 생성자를 통해 HttpTraceWrapperRepository 인스턴스를 주입받는다
        this.repository = repository;
    }

    @Override
    public List<HttpTrace> findAll() {
        // findAll() 메소드는 repository의 findAll() 메소드를 호출해서 HttpTraceWrapper 클래스 스트림을 받아온 후
        // getHttpTrace() 메소드를 매핑하고 리스트에 담아서 반환한다
        return repository.findAll()
                .map(HttpTraceWrapper::getHttpTrace) // <2>
                .collect(Collectors.toList());
    }

    @Override
    public void add(HttpTrace trace) {
        // HttpTrace 객체를 감싸는 HttpTraceWrapper 객체를 새로 생성하고 repository의 save() 메소드에 인자로 전달해서 저장한다
        repository.save(new HttpTraceWrapper(trace)); // <3>
    }
}
```

#### SpringDataHttpTraceRepository 빈 등록
```java
@Bean
HttpTraceRepository springDataTraceRepository(HttpTraceWrapperRepository repository) {
    return new SpringDataHttpTraceRepository(repository);
}
```

#### 몽고디비 Document를 HttpTraceWrapper로 변환하는 컨버터
```java
/**
 * 몽고디비 Document를 HttpTraceWrapper로 변환하는 컨버터
 */
static Converter<Document, HttpTraceWrapper> CONVERTER = //
        new Converter<Document, HttpTraceWrapper>() { //
            @Override
            public HttpTraceWrapper convert(Document document) {
                Document httpTrace = document.get("httpTrace", Document.class);
                Document request = httpTrace.get("request", Document.class);
                Document response = httpTrace.get("response", Document.class);

                return new HttpTraceWrapper(new HttpTrace( //
                        new HttpTrace.Request( //
                                request.getString("method"), //
                                URI.create(request.getString("uri")), //
                                request.get("headers", Map.class), //
                                null),
                        new HttpTrace.Response( //
                                response.getInteger("status"), //
                                response.get("headers", Map.class)),
                        httpTrace.getDate("timestamp").toInstant(), //
                        null, //
                        null, //
                        httpTrace.getLong("timeTaken")));
            }
        };
```

### 스프링 데이터 몽고디비에 커스텀 컨버터 등록
```java
/**
* 스프링 데이터 몽고디비에 커스텀 컨버터 등록
* @param context
* @return
*/
@Bean
public MappingMongoConverter mappingMongoConverter(MongoMappingContext context) {

        // 몽고디비의 DBRef 값에 해석이 필요할 때 UnsupportedOperationException을 던지는 NoOpDbRefResolver를 사용해서 MappingMongoConverter 객체를 생성한다
        // HttpTrace에는 DBRef 객체가 없으므로 DBRef 값 해석이 발생하지 않으며, NoOpDbRefResolver에 의해 예외가 발생할 일은 없다
        // 단지 MappingMongoConverter의 생성자가 DbRefResolver 타입의 인자를 받기 때문에 NoOpDbRefResolver를 전달하는 것이다
        MappingMongoConverter mappingConverter = 
                new MappingMongoConverter(NoOpDbRefResolver.INSTANCE, context);
        // MongoCustomConversions 객체를 mappingConverter에 설정한다
        mappingConverter.setCustomConversions(
                // 커스텀 컨버터 한 개로 구성된 리스트를 추가해서 MongoCustomConversions를 생성한다
                new MongoCustomConversions(Collections.singletonList(CONVERTER))); // <3>

        return mappingConverter;
    }
```

### 그 밖의 엔드포인트
| 액추에이터 엔드포인트 | 설명                                                         |
| --------------------- | ------------------------------------------------------------ |
| `/actuator/auditevents` | 감사(audit) 이벤트 표시                                      |
| `/actuator/beans`       | 직접 작성한 빈과 자동설정에 의해 애플리케이션 컨텍스트에 등록된 모든 빈 표시 |
| `/actuator/caches`      | 모든 캐시 정보 표시                                          |
| `/actuator/conditions`  | 스프링 부트 자동설정 기준 조건 표시                          |
| `/actuator/configprops` | 모든 환경설정 정보 표시                                      |
| `/actuator/env`         | 현재 시스템 환경 정보 표시                                   |
| `/actuator/flyway`      | 등록된 플라이웨이(Flyway) 데이터베이스 마이그레이션 도구 표시 |
| `/actuator/mappings`    | 모든 스프링 웹플럭스 경로 표시                               |
| `/actuator/metrics`     | 마이크로미터(micrometer)를 사용해서 수집하는 지표(metrics) 표시 |

## 관리 서비스 경로 수정
### 액추에이터 루트 엔드 포인트 변경
`/actuator` 대신 `/manage` 로 액추에이터 루트 엔드포인트 변경
```yaml
management:
  endpoints:
    web:
      base-path: /manage
```

### 액추에이터 상세 경로 변경
`/actuator/loggers`를 `/logs`로 상세 경로 변경 
```yaml
management:
  endpoints:
    web:
      base-path: /
      path-mapping:
        loggers: logs
```

## 5장에서 배운 내용
- 실행 가능한 JAR 파일 생성
- 계층 기반 Dockerfile 작성 및 컨테이너 생성
- 페이키토 빌드팩을 상요해서 Dockerfile 없이 컨테이너 생성
- 스프링 부트 액추에이터 추가
- 필요한 관리 서비스만 노출
- 애플리케이션 정보 및 빌드 정보 변경
- HTTP 트레이스 데이터를 몽고디비에 저장하고 조회하는 코드 작성
- 관리 서비스 경로 변경

# PART 6. 스프링 부트 API 서버 구축
- JSON 기반 웹 서비스 구축
- 스프링 REST Docs을 활용한 API 문서화
- 스프링 부트로 만든 API 포털에서 다양한 API 제공
- 스프링 HATEOAS를 사용한 하이퍼미디어 활용
- API 포털에 하이퍼미디어 링크 추가

## HTTP 웹 서비스 구축

`thenReturn()` 메소드는 스프링 웹의 `ResponseEntity.ok()` 헬퍼 메소드를 사용해서 교체 후 데이터를 HTTP 200 OK와 함께 반환한다

스프링 데이터에서 제공하는 `save()`나 `delete()` 메소드를 사용하고 이후에 `then***()` 메소드를 호출할 때는 항상 `flatMap()`을 사용해야 한다  
그렇지 않으면 저장도 삭제도 되지 않는다  
`flatMap()`을 사용해서 결괏값을 꺼내야 데이터 스토어에도 변경이 적용된다  

## API 포털 생성

Spring REST Docs은 API 문서화 작업을 도와준다  
사용자가 직접 사용해볼 수 있는 API 예제를 포함해서 API 문서를 쉽게 만들어낼 수 있다  
여러 분야에서 사용성이 입증된 Asciidoctor 문서화 도구를 사용해서 세부 내용도 쉽게 문서로 만들 수 있다  

### API 문서화를 위한 asciidoc 사용 설정
Asciidoc는 표준이고 Asciidoctor는 Asciidoc 표준을 Ruby 언어로 구현한 프로젝트이다

`asciidoctor-maven-plugin`은 확장자가 .adoc인 Asciidoc 파일을 HTML로 변환해준다  
**Spring REST Docs**는 `Asciidoc`파일의 주요 내용을 자동으로 생성해준다  
최종 HTML은 `target/generated-docs`에 저장된다  

Maven  
```xml
<plugin>
  <groupId>org.asciidoctor</groupId>
  <artifactId>asciidoctor-maven-plugin</artifactId>
  <version>2.2.1</version>
  <executions>
    <execution>
      <id>generate-docs</id>
      <phase>prepare-package</phase>
      <goals>
        <goal>process-asciidoc</goal>
      </goals>
      <configuration>
        <backend>html</backend>
        <doctype>book</doctype>
      </configuration>
    </execution>
  </executions>
  <dependencies>
    <dependency>
      <groupId>org.springframework.restdocs</groupId>
      <artifactId>spring-restdocs-asciidoctor</artifactId>
      <version>${spring-restdocs.version}</version>
    </dependency>
  </dependencies>
</plugin>
```

Gradle  
참조: https://subji.github.io/posts/2021/01/06/springrestdocsexample  
참조: https://gaemi606.tistory.com/entry/Spring-Boot-REST-Docs-%EC%A0%81%EC%9A%A9%ED%95%98%EA%B8%B0  
참조: https://velog.io/@hydroniumion/BE1%EC%A3%BC%EC%B0%A8-Spring-Rest-Docs-%EC%A0%81%EC%9A%A9%EA%B8%B0  
참조: https://jaehun2841.github.io/2019/08/04/2019-08-04-spring-rest-docs/  
참조: https://beemiel.tistory.com/13  
참조(Kotlin): https://dwony26.tistory.com/134  
최신버전 참조: https://velog.io/@max9106/Spring-Spring-rest-docs%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%9C-%EB%AC%B8%EC%84%9C%ED%99%94  
최신버전 참조: https://eclipse4j.tistory.com/364  
최신버전 참조(많은 도움됨): https://huisam.tistory.com/entry/RESTDocs  
최신버전 참조(많은 도움됨): https://hhseong.tistory.com/212  
Spring REST Docs: https://docs.spring.io/spring-restdocs/docs/current/reference/html5/  
Asciidoctor Gradle Plugin Document: https://asciidoctor.github.io/asciidoctor-gradle-plugin/master/user-guide/  
Asciidoc 기본 사용법: https://narusas.github.io/2018/03/21/Asciidoc-basic.html  
Gradle Docs: https://plugins.gradle.org/plugin/org.asciidoctor.jvm.convert  
우아한형제들 Spring Rest Docs 적용: https://techblog.woowahan.com/2597/  
```groovy
plugins {
  ..
  // gradle 7 부터는 org.asciidoctor.convert가 아닌asciidoctor.jvm.convert를 사용
  id "org.asciidoctor.jvm.convert" version "3.3.2"
}

ext {
  // Snippet 의 생성 위치를 지정
  set('snippetsDir', file('build/generated-snippets'))
}

// asciidoctor 추가
asciidoctor {
  attributes 'snippets': snippetsDir // adoc 파일 생성시 올바르게 include하기 위함
  // Snippets 디렉토리를 Input 디렉토리로 설정
  inputs.dir snippetsDir
  // 문서 생성 전 테스트가 실행되도록 test 에 종속 설정
  dependsOn test
}
// 기존에 존재하는 docs를 삭제
asciidoctor.doFirst {
  delete file('src/main/resources/static/docs')
}
// build/docs/asciidoc 파일을 src/main/resources/static/docs로 복사해준다
task copyDocument(type: Copy) {
  dependsOn asciidoctor
  from file("build/docs/asciidoc/")
  into file("src/main/resources/static/docs")
}

// https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/
// 빌드 파일에서 계층형 JAR 사용하도록 지정
bootJar {
  // bootJar 설정. 스니펫을 이용해 문서 작성 후, build - docs - asciidoc 하위에 생기는
  dependsOn asciidoctor
  // 생성된 문서를 static/docs 에 복사
  copy {
    from "${asciidoctor.outputDir}" // gradle은 src/docs/asciidoc 에 메인 adoc 파일을 직접 생성해주어야함
    into 'src/main/resources/static/docs' // asciidoctor로 만든 문서는 static/docs 디렉토리로
  }
}


build {
  dependsOn copyDocument
}

test {
  useJUnitPlatform()
  // Snippets 디렉토리를 출력으로 작업하도록 설정
  outputs.dir snippetsDir
}
```

**Spring REST Docs**는 기본적으로 `src/main/asciidoc`에서 Asciidoc 파일을 읽어서 주요 내용을 자동을 생성하므로  
`src/main/asciidoc/index.adoc` 파일을 만들고 새 API 포털의 도입부를 직접 작성해야 한다

### spring-restdocs-webtestclient 의존관계 추가
Spring WebFlux Controller를 테스트할 수 있게 해준다

Maven
```xml
<dependency>
    <groupId>org.springframework.restdocs</groupId>
    <artifactId>spring-restdocs-webtestclient</artifactId>
    <scope>test</scope>
</dependency>
```

Gradle
```groovy
dependencies {
    // Maven 과 같이 test Scope 에 대한 mockMvc 의존성을 추가 (WebClient, Assured 사용가능)
    // restassured를 사용하려면 restassured 의존성을 넣어주면된다
    testImplementation 'org.springframework.restdocs:spring-restdocs-webtestclient'
}
```

실행이 성공적으로 완료되면 다음 내용을 포함하는 문서 조각(snippet)이 생성됨

- cURL, HTTPie 형식에 맞는 요청 명령
- HTTP 형식에 맞는 요청 및 응답 메시지
- JSON 형식으로 된 요청 본문
- JSON 형식으로 된 응답 본문

snippet 파일들은 `document()` 메소드의 첫 번째 문자열로 지정해준 서브 디렉터리 아래에 생성됨

- Maven: `target/generated-snippets`
- Gradle: `build/generated-snippets`

`http://localhost:8080/docs/index.html` 에 접속하면 생성된 **Spring REST Docs 문서**를 볼 수 있다

**Spring REST Docs**은 API 문서를 다듬을 수 있는 요청 전처리기인 `preprocessRequest`와 응답 전처리기인 `preprocessResponse`를 제공한다

| 전처리기                                              | 설명                                                         |
| ----------------------------------------------------- | ------------------------------------------------------------ |
| `prettyPrint()`                                       | 요청이나 응답 메시지에 줄바꿈, 들여쓰기 등 적용              |
| `removeHeaders(String... headerNames)`                | 표시하지 않을 헤더 이름 지정<br />스프링의 HttpHeaders 유틸 클래스에 표준 헤더 이름이 상수로 등록돼 있으므로 함께 사용하면 편리하다 |
| `removeMatchingHeaders(String... headerNamePatterns)` | 표시하지 않을 헤더를 정규 표현식으로 지정                    |
| `maskLinks()`                                         | href 링크 항목 내용을 `...`로 대체<br />HAL(Hypertext Application Language)을 적용할 때 API 문서에 하드코딩된 URI 대신 링크를 통해 API 사용을 독려하기 위해 URI 링크를 '...'로 대체한다 |
| `maskLinks(String mask)`                              | href 항목을 대체할 문자열 명시                               |
| `replacePattern(Pattern pattern, String replacement)` | 정규 표현식에 매칭되는 문자열을 주어진 문자열로 대체         |
| `modifyParameters()`                                  | 평문형 API(fluent API)를 사용해서 요청 파라미터 추가, 변경, 제거 |
| `modifyUris()`                                        | 평문형 API를 사용해서 로컬 환경에서 테스트할 때 API 문서에 표시되는 URI 지정 |

**Spring REST Docs**을 사용하는 테스트 케이스에서는 mock을 통해 지정한 테스트 데이터가 API 문서에도 표시된다  
따라서 여러 API에 걸쳐 일관성 있는 내용이 표시되도록 테스트 데이터를 구성하는 것이 좋다

## API 진화 반영

장자끄 뒤브레 '버저닝 비용 이해(Understanding the Costs of Versioning)' 논문에서 세가지 API 변경 유형을 설명한다

- `매듭(knot)`: 모든 API 사용자가 단 하나의 버전에 묶여 있다
  API가 변경되면 모든 사용자도 함꼐 변경을 반영해야 하므로 엄청난 여파를 몰고 온다
- `점대점(point-to-point)`: 사용자마다 별도의 API 서버를 통해 API를 제공한다
  사용자별로 적절한 시점에 API를 변경할 수 있다
- `호환성 버저닝(compatible versioning)`: 모든 사용자가 호환 가능한 하나의 API 서비스 버전을 사용한다

## 하이퍼미디어 기반 웹 서비스 구축
링크를 따라 여러 문서를 오가면서 데이터를 활용할 수 있는 하이퍼미디어를  
API에 추가하면 더 유연하게 API를 진화시킬 수 있다

하이퍼미디어를 직접 작성하려면 비용이 많이든다  
그래서 이런 비용을 줄이기 위해 Spring HATEOS가 만들어졌다  
Spring HATEOS는 Spring WebFlux도 지원하며 서비스를 아주 쉽고 신속하게 하이퍼미디어 형식으로 표현할 수 있도록 도와준다

조회한 정보 전체를 교체(PUT)하거나, 일부를 변경(PATCH)하거나, 삭제(DELETE)할 수 있는 링크를 제공한다면  
사용자가 쉽게 해당 작업을 수행할 수 있다

### Spring HATEOS 의존관계 추가

스프링 MVC를 지원하는 용도로 만들어져서 스프링 MVC와 Tomcat을 사용할 수 있게 해주는 `spring-boot-starter-web` 이 포함돼어 있다  
Spring WebFlux와 Reactor Netty를 사용하는 웹 서비스를 만들고 있으므로 제외시킨다

MAVEN
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-hateoas</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

Gradle
```groovy
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-hateoas"){
      exclude group: 'org.springframework.boot', module: 'spring-boot-starter-web'
    }
}
```

하이퍼미디어 링크를 만들 때는 가장 먼저 도메인 객체와 링크를 조합해야 한다  
이작업을 쉽게 할 수 있도록 **Spring HATEOS**는 vendor-neutral 모델을 제공한다

- `RepresentationModel`: 링크 정보를 포함하는 도메인 객체를 정의하는 기본 타입
- `EntityModel`: 도메인 객체를 감싸고 링크를 추가할 수 있는 모델 `RepresentationModel`을 상속받는다
- `CollectionModel`: 도메인 객체 컬렉션을 감싸고 링크를 추가할 수 있는 모델 `RepresentationModel`을 상속받는다
- `PageModel`: 페이징 관련 메타데이터를 포함하는 모델 `CollectionModel`을 상속받는다

**Spring HATEOS**는 이 네 가지 모델과 `Link`, `Links` 객체를 기반으로 하이퍼미디어 기능을 제공한다

## 하이퍼미디어의 가치
데이터와 데이터 사용 방법에 대한 정보도 함께 제공하기 위해 하이퍼미디어를 사용한다  
그래서 하이퍼미디어 문서에 데이터에 대한 설명을 여러 가지 JSON 형식으로 제공하는 profile link가 종종 포함되기도 한다  
proflie link에 포함된 링크는 자바스크립트 라이브러리가 자동으로 생성/수정 입력 폼을 만드는데 사용될 수 있다

**Spring HATEOS**는 ALPS(Application-Level Profile Semantics)(http://alps.io)도 지원한다  
ALPS를 사용하는 웹 메소드를 작성하면 자신만의 프로파일을 만들어서 사용할 수도 있다

클라이언트가 직접적으로 도메인 지식에 의존하는 대신에 프로토콜에만 의존하게 만들면  
예를 들어 클라이언트가 주문에 대한 지식을 직접 사용하지 말고 단순히 링크를 읽고 따라가게 만든다면  
클라이언트는 백엔드의 변경에서 유발되는 잠재적인 문제를 피해갈 수도 있다

웹 사이트 변경이 웹 브라우저의 업데이트를 유발하지 않는다는 순수한 사실은 서버 쪽에서 변경이 발생해도  
클라이언트에 여ㅇ향을 미치지 않게 만드는 것이 가능하다는 증거가 된다

Roy Fielding 박사가 논문에서 제안한 개념이 적용된 API는 하위 호환성을 갖게 된다  
이런 API를 사용하면 시간이 지남에 따라 유지 관리에 드는 총 비용을 절감할 수 있다

## API에 행동 유도성 추가
동일한 URI를 가리키는 GET과 PUT을 함께 담으면 HAL 문서는 한 개의 링크만 생성한다  
그 결과 사용자는 원래는 GET, PUT 두 가지의 서로 다른 선택지가 존재했었다는 사실을 알 수 없게 된다

GET과 PUT을 다른 링크로 표현하도록 강제하더라도 클라이언트가 PUT 요청을 보내려면 어떤 속성 정보를 제공해야하는지  
클라이언트에 알려주지 않는다  
바로 이 지점에서 **Spring HATEOS**가 하이퍼미디어에 행동 유도성(affordance)을 추가한 API를 제공해준다

하나의 Item을 보여줄 때, 그 Item을 수정할 수 있는 행동 유도성을 추가해주는 것이 전형적인 사례이다  
**Spring HATEOS**는 관련 있는 메소드를 연결할 수 있는 수단을 제공한다  
Item 사례에서는 GET 연산에 대한 링크가 PUT 연산으로 이어질 수 있다

HAL로 표현되면 여전히 하나의 링크만 표시된다  
하지만 HAL-FORMS 같은 하이퍼미디어 형식은 추가 정보를 렌더링할 수 있는 연결 정보도 보여줄 수 있다  
행동 유도성을 추가할 수 있는 어떤 미디어 타입이라도 이런 메타데이터를 제공할 수 있다는 장점이 있다

> 행동 유도성 관점에서는 실제 데이터를 전부 제공하는 것이 중요하지 않다
> 하지만 가능하다면 id 필드 같은 정보를 제공하는 것이 좋다
> 그래야 PathVariable를 사용하는 하위 링크를 만들 수 있다

## 6장에서 배운 내용
- 원격 접근을 통해 시스템을 변경하는 API 생성
- **Spring REST Docs**을 사용해서 API 문서화 포털을 만드는 테스트 작성
- HAL 기반 링크 정보를 포함하는 하이퍼키디어 제공 컨트롤러 작성
- 링크 정보 및 관련 세부정보를 추가해서 문서화 테스트 보완
- 행동 유도성 소개 및 HAL-FORMS 형식 데이터와 데이터 템플릿 제공
- Asciidoc snippet을 합쳐서 API 문서화 포털 구축

# PART 7. 스프링 부트 메시징
- 스프링 부트에서 지원하는 다양한 메시징 솔루션
- 스프링 부트에서 직접 지원하지는 않지만 스프링 포트폴리오에서 지원하는 다양한 메시징 솔루션
- AMQP(Advanced Message Queuing Protocol)를 자세히 알아보고, 스프링 AMQP와 프로젝트 리액터를 활용해 웹 계층과 백엔드의 결합 관계 해소

## 메시징 솔루션 선택
메시징 솔루션은 JMS(Java Messaging Service), Apache Kafka, AMQP, Redis, GemFire, Apache Geode 등 매우 다양하다
메시징을 활용하고 리액티브 스트림 프로그래밍에 적절히 통합하는 방법을 다룬다

## 익숙한 패턴을 사용한 문제 해결
템플릿 패턴은 너무 강력해서 MainSender, JndiTemplate, HibernateTemplate, JdoTemplate등 여러 영역에서 두루 사용되고 있다

다음과 같은 비동기 메시징에서도 사용된다

- **JMS**: 자바 표준 메시징 API, 스프링 프레임워크는 JMS broker를 사용한 메시지 송신과 수신을 쉽게 처리할 수 있도록
  **JmsTemplate**과 **DefaultMessageListenerContainer**를 제공한다
- **Apache Kafka**: 빠른 속도로 대세로 자리 잡고 있는 브로커
  **Spring for Apache Kafka**는 **Apache Kafka**를 사용한 메시지 송신과 수신을 쉽게 처리할 수 있도록
  **KafkaTemplate**과 **KafkaMessageListenerContainer**를 제공한다
- **RabbitMQ**: 높은 처리량과 강한 회복력이 특징인 메시지 브로커, **스프링 AMQP**는 **RabbitMQ**를 사용한 메시지 송신과 수신을 쉽게 처리할 수 있도록
  **AmqpTemplateSimpleMessageListenerContainer**를 제공한다
- **Redis**: 빠른 속도를 무기로 가장 널리 사용되는 브로커
  **Spring Data Redis**는 **Redis**를 사용하는 메시지 송신과 수신을 쉽게 처리할 수 있도록 **RedisMessageListenerContainer**를 제공한다
  
## 손쉬운 테스트
Testcontainers(https://testcontainers.org)는 Docker를 활용하는 자바 테스트 지원 라이브러리이다  
테스트컨테이너는 도커에서 실행될 수만 있다면, 어떤 데이터베이스나 메시지 브로커, 서드파티 시스템도 테스트용으로 쉽게 쓸 수 있다  
테스트가 종료되면 테스트에 사용됐던 여러 컨테이너 자원도 남김없이 깔끔하게 종료된다  

### 테스트컨테이너 버전 지정
테스트가 종료되면 별도로 신경 쓰지 않아도 테스트에 사용된 컨테이너도 함께 종료된다

Maven
```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>testcontainers-bom</artifactId>
            <version>1.16.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>rabbitmq</artifactId>
        <scope>test</scope>
    </dependency>
    
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

Gradle
```groovy
dependencies {
    // Testcontainer 버전 지정
    implementation platform('org.testcontainers:testcontainers-bom:1.16.0')
    // RabbitMQ Testcontainer 의존관계 추가
    testImplementation 'org.testcontainers:rabbitmq'
    // Junit 5와 함께 사용하기 위해 추가
    testImplementation 'org.testcontainers:junit-jupiter'
}
```

## 테스트컨테이너 사용 테스트
지금까지 프로젝트 리액터를 사용하는 테스트에서는 **StepVerifier**를 사용해서 비동기 처리 흐름을 쉽게 테스트할 수 있엇고 지연효과를 흉내 낼 수도 있었다  
하지만 **RabbitMQ**를 사용하는 테스트에서는 **RabbitVerifier** 같은 것이 없어서 `Thread.sleep()`을 사용해야 한다

> 스프링 팀에서는 일반적으로 생성자 주입(constructor injection) 방식으로 컴포넌트를 주입하는 것을 권장하지만
> 테스트에서는 수명주기가 다르므로 생성자 주입이 아니라 필드 주입(field injection)을 사용해도 괜찮다

### 테스트 케이스 구성
웹 컨트롤러가 처리해야 할일

1. 새 Item 객체를 생성하기 위해 Item 데이터가 담겨 있는 HTTP POST 요청을 받는다
2. Item 데이터를 적절한 메시지로 변환한다
3. Item 생성 메시지를 브로커에 전송한다

메시지를 브로커 쪽에서 해야 할 일

1. 새 메시지를 받을 준비를 하고 기다린다
2. 새 메시지가 들어오면 꺼내서
3. 몽고디비에 저장한다

Spring AMQP 추가

MAVEN
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

Gradle
```groovy
// Spring AMQP 추가
implementation 'org.springframework.boot:spring-boot-starter-amqp'
```

RabbitMQ API는 작업 수행 중 현재 스레드를 블록한다  
결국에는 비동기 처리 과정으로 되돌아가더라도 어떤 API가 현재 스레드를 블로킹한다면 블로킹 API다

## 스케줄러를 사용해서 블로킹 API 감싸기
작업 수행 단계 중에 블로킹 API 호출이 포함된다면 리액터에게 알려서 블로킹 API를 별도의 스레드에서 호출하게 해야  
의도하지 않은 스레드 낭비를 방지할 수 있다

리액터는 다음과 같이 여러 방법으로 스레드를 사용할 수 있다

- `Schedulers.immediate()`: 현재 스레드
- `Schedulers.single()`: 재사용 가능한 하나의 스레드
  현재 수행 중인 리액터 플로우뿐만 아니라 호출되는 모든 작업이 동일한 하나의 스레드에서 실행된다
- `Schedulers.newSingle()`: 새로 생성한 전용 스레드
- `Schedulers.boundedElastic()`: 작업량에 따라 스레드 숫자가 늘어나거나 줄어드는 신축성 있는 스레드풀
- `Schedulers.paraller()`: 병렬 작업에 적합하도록 최적화된 고정 크기 워커 스레드 풀
- `Schedulers.fromExecutorService()`: ExecuterService 인스턴스를 감싸서 재사용

> single(), newSingle(), paraller()은 논블로킹 작업에 사용되는 스레드를 생성한다
> 이 세 가지 스케줄러에 의해 생성되는 스레드는 리액터의 NonBlocking 인터페이스를 구현한다
> 따라서 block(), blockFirst(), blockLast() 같은 블로킹 코드가 사용되면 IllegalStateException이 발생한다

리액터 플로우에서 스케줄러를 변경하는 방법 두가지

- `publishOn()`: 호출되는 시점 이후로는 지정한 스케줄러를 사용한다
  이 방법을 사용하면 사용하는 스케줄러를 여러 번 바꿀 수도 있다
- `subscribeOn()`: 플로우 전 단계에 걸쳐 사용되는 스케줄러를 지정한다
  플로우 전체에 영향을 미치므로 `publishOn()`에 비해 영향 범위가 더 넓다

## 컨슈머 작성
가장 단순한 방식은 `AmqpTemplate.receive(queueName)`이지만 가장 좋은 방식이라고 할 순 없다  
특히 부하가 많은 상황에서는 적합하지 않다  
더 많은 메시지를 polling 방식으로 처리할 수도 있고 콜백을 등록해서 처리할 수도 있지만  
`@RabbitListener`를 사용하는 것이 가장 유연하고 편리하다

### 익명 큐(anonymous queue)와 이름 있는 큐(named queue)의 차이
동일한 메시지를 여러 컨슈머가 사용해야 하는 상황에서는 용도에 맞게 설정하는 것이 중요하다  
만약 2개의 컨슈머가 동일한 큐를 사용하도록 설정되면 하나의 메시지는 두 컨슈머 중 하나의 컨슈머만 접근해서 사용할 수 있다  
하나의 큐에 있는 메시지는 하나의 클라이언트에 의해서만 소비될 수 있다  
동일한 라우팅 키를 사용하는 하나의 익스체인지에 2개의 컨슈머가 연결돼 있지만 각자 다른 큐를 사용한다면  
하나의 메시지가 다른 큐에 복제되므로 메시지 발행자 쪽을 변경하지 않고도 2개의 컨슈머가 모두 해당 메시지를 사용할 수 있다  

`@RabbitListener` 애너테이션을 메소드에 붙이면 Spring AMQP가 가능한 한 가장 효율적인 캐시 및 풀링 메커니즘을 적용하고   
백그라운드에서 리스너를 등록한다  

### Serializable을 피하는 것이 중요하다
역직렬화가 자바에 포함돼 있는 여러 보안 검사를 우회한다는 것은 잘 알려져 있다  
그래서 예전부터 다양한 보안 공격에 활용돼왔고 자바 개발 진영에게는 필요악과도 같은 존재다  
오라클의 자바 플랫폼 수석 아키텍트인 마크 레이놀드(Mark Reinhold)는  
Serializable을 1997년에 만들어진 끔찍한 실수라고 얘기하며 자바 명세에서 제외하고 싶다고 주장해왔다  
Serializable을 사용하는 것 보다는 Jackson 같은 라이브러리를 사용해서 더 엄격하게 제어하는 것이 더 낫다  
그래서 Jackson 을 사용해서 성능 저하가 발생한다는 확실한 벤치마크 결과가 나오지 않는 한  
일반적ㅇ로 Serializable 대신에 Jackson을 사용할 것을 추천한다

### 리액터, RabbitMQ, 스프링 데이터의 협업과정을 확인하기 위한 로그 설정 추가
```yaml
logging:
  level:
    org:
      springframework:
        amqp: DEBUG
        messaging: DEBUG
        data: DEBUG
    com:
      greglturnquist:
        hackingspringboot: DEBUG
    reactor: DEBUG
```

### 비동기 메시징 솔루션의 핵심 개념
- 블로킹 API는 감싸서 별도의 스레드에서 실행
- 하나의 메시지 발행
- 하나 혹은 둘 이상의 컨슈머가 메시지 소비
- 스프링 포트폴리오에 포함된 **RabbitTemplate, RabbitMessageTemplate, AmquTemplate, JmsTemplate, KafkaTemplate**등 다양한 템플릿 활용

## 7장에서 배운 내용
- **TestContainer**, **RabbitMQ**, **Spring AMQP** 설정
- 웹과 백엔드가 예상대로 동작하는지 검증하는 테스트 작성
- 동기적 웹 요청을 받아서 처리하는 웹플럭스 컨트롤러 작성
- 블로킹 API 호출부를 감싸서 리액터의 엘라스틱 스레드 풀에서 실행
- **RabbitTemplate**를 사용해서 비동기 메시지 브로커를 통해 메시지 전송
- `@RabbitListener`를 사용해서 **RabbitMQ Listener**를 설정하고 전송받은 메시지를 소

# PART 8. 스프링 부트 R소켓
리액티브 스트림 프로그래밍을 지원하기 위해 바닥부터 새로 만들고 있는 프로토콜인 R소켓

- Request, Response 클라이언트/서버를 구성하는 방법
- 양쪽 모두에서 트래픽을 발생시킬 수 있는 양방향 서비스 구성

## 리액티브 프로토콜 탄생
R소켓(https://rsocket.io)은 HTTP, 웹소켓과 마찬가지로 OSI 7 계층 프로토콜이다  
VMware, 페이스북, Netifi, Alibaba를 비롯한 여러 회사가 설립한 Reactive Foundation(https://reactive.foundation)에서 공동으로 만들었다  
R소켓은 자바뿐만 아니라 자바스크립트, Go, .Net, C++, Kotlin을 지원하는 명세를 포함하고 있다

> R소켓에서 사용되는 리액티브 스트림 자바 구현체는 리액터다
> 그래서 이 책을 통해 지금까지 배운 내용은 R소켓을 익히는 데도 도움이 된다
> 리액티브 스트림 커뮤니티에서는 여러 도구 사이의 상호운용성도 보장해 줄 정도로 R소켓을 더욱 발전시켰으며
> R소켓과 스프링 부트에서 리액터가 공통적으로 사용된다는 점은 큰 장점이다

R소켓은 웹소켓, TCP, Aeron등 여러 가지 프로토콜 위에서 동작하도록 설계됐다  
웹소켓은 아주 가볍고 유연해서 R소켓이 필요로하는 모든 것을 지원한다

TCP는 OSI 4계층에 위치하는 강력한 프로토콜이다  
HTTP는 TCP의 연결 관리를 사용해서 TCP 위에서 동작한다  
R소켓도 TCP를 사용해서 장애내성(fault-tolerant)과 확장성을 가진 리액티브 연결을 만들 수 있다

Aeron은 UDP 위에서 동작하는 메시징 프로토콜이다  
UDP는 신뢰성 있는 연결을 필요하지 않는 프로토콜이다  
리액터 애플리케이션은 작업 부하(workload) 사이를 오가는 워커 스레드를 사용하므로  
작업 부하가 여러 가지 메시지로부터 만들어진다는 사실은 어렵지 않게 유추할 수 있다

## R소켓 패러다임
R소켓은 단순히 연결에 사용되는 채널에 다른 API를 추가한 것이라고 이해할 수 있따

- 요청-응답(1개의 스트림)
- 요청-스트림(다수의 유한한 스트림)
- 실행 후 망각(fire-and-forget)(무응답)
- 채널(양방향)

### 요청-응답
실제로 통신에서 일반적으로 필요한 요구사항의 80%는 요청-응답 방식으로 해결할 수 있다

### 요청-스트림
한 번의 요청을 보내고 스트림 형태로 응답을 계속 받을 수 있으므로 좀 더 효율적인 요청 방식이다  
주식 종목의 가격 정보를 요청하고 변화되는 주식 가격을 스트림 형태로 계속 응답받는 상황이
요청-스트림 방식의 대표적인 사례라고 할 수 있다

### 실행 후 망각
요청을 보내고 나서 응답은 신경 쓰지 않는 뒤끝 없는 방식이지만 별로 대단해 보이지는 않을 수도 있다

### 채널
클라이언트와 서버는 다음과 같은 세가지 선택지를 가지고 있다

- 응답 대기
- 응답 대기 안 함
- 무한 응답 대기

채널 패러다임은 진정한 message-driven 양방향 통신 채널을 실현한다  
채널의 어느 쪽이든 상대방에게 메시지를 전송할 수 있고  
양쪽 모두 리액티브 메시지 Listener를 반드시 등록해야 한다

## R소켓 서버생성

Maven
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-rsocket</artifactId>
</dependency>
```

Gradle
```groovy
implementation 'org.springframework.boot:spring-boot-starter-rsocket'
```

의존관계를 통해 추가되는 기능

- RSocket Core와 Transport Netty
- Reactor Netty
- Spring Messaging
- Jackson

세가지 주요 요소

1. **R소켓**: 자바로 구현된 R소켓 프로토콜
2. **리액터 네티**: 네티는 리액티브 메시지 관리자 역할도 충분히 수행할 수 있다
   리액터로 감싸져서 더 강력한 서버로 만들어졌다
3. **스프링 + Jackson**: 메시지가 선택되고 직렬화되며 전송되고 역직렬화되고 라우팅되는 것은 프로토콜 리액티브 속성만큼이나 중요하다
   스프링의 입증된 메시지 처리 아키텍처와 Jackson을 함께 사용하는 사례는 무수히 많으며 현장에서 충분히 검증됐다
 
> Spring Messaging은 스프링 프레임워크의 패러다임으로서 Spring Integration, Spring AMQP, Spring for Apache Kafka
> Spring Cloud Stream을 포함하는 다양한 스프링 포트폴리오 프로젝트에서 사용돼왔다                             
> 메시지 본문을 받아서 헤더를 추가하고 비동기로 전송하고 필터링하고 압축을 푸는 개념은 메시지를 받는 쪽에서도 널리 사용되고 있다

**FluxProcessor** 클래스의 요구사항 정의

- 가장 최근 메시지만 보내야 한다면 **EmitterProcessor**가 필요
- 최근 N개의 메시지를 보관하고 새로운 구독자에게 **N**개의 메시지를 모두 보내야 한다면
  **ReplayProcessor**가 필요하다
- 단 하나의 컨슈머만을 대상으로 한다면 **UnicastProcessor**가 필요하다

### EmitterProcessor
누군가 구독했을 때 최근 메시지만 보내는 Processor

> FluxProcessor와 EmitProcessor는 스프링 리액터 3.4에서 Deprecated되었고 3.5부터는 제거 예정이다
> 스프링 부트의 최신 안정 버전은 2021 9월 현재 2.5.4이며 스프링 리액터 3.4.9가 사용되므로 FluxProcessor와 EmitterProcessor를 
> 사용하는 예제 코드는 스프링 부트 2.5에서도 유효할 것으로 보인다
> 다만 스프링 부트 2.5.4 기준으로 Deprecated 상태이므로 API 문서에 나오는 내용을 토대로 Sinks를 사용한 대체 구현 코드를 주석으로 추가한다

**EmitterProcessor**는 단지 Flux를 상속받은 특별한 버전의 Flux  
리액티브 스트림이 사용되는 곳 어디에나 **EmitterProcessor**를 전달할 수 있고  
새 Item 객체를 주입하는 동안 구독하게 할 수 있다

> R소켓 프로토콜 덕분에 전체 리액티브 스트림 체인이 네트워크를 넘어서도 동작할 수 있으므로
> 처리 과정 중에 무언가 잘못되면 Mono.error()가 전달된다
> 이를 통해 요청을 보낸 쪽에서 무언가 잘못됐다는 알림을 받을 수 있다는 점은 요청-응답 방식과 실행 후 망각 방식이 같다
> 하지만 모든 작업이 적절하게 완료됐을 때 수행되는 일은 요청-응답 방식과 실행 후 망각 방식에서 다르다
> 이는 개발자와 비즈니스 로직에 달려 있다


> 리액터 네티 컨테이너를 실행하면 스프링 부트는 자동으로 리액터 네티 컨테이너를 사용해서 TCP R소켓 서비스를 호스팅한다
> 이 방법이 가장 간단하므로 여기에서도 이 방법을 택한다

### 몽고디비 실행
실습을 위해 몽고디비가 필요하다

아래의 도커명령어를 실행해서 몽고디비를 실행하자

```bash
docker run -p 27017-27019:27017-27019 mongo
```

### 애플리케이션 실행
R소켓 포트는 7000, 서버 메인 포트는 9000로 구동되며 도메인 객체를 탐색하고  
몽고디비에 연결한 후에 스프링 부트 액추에이터도 활성화 한다

## R소켓 클라이언트 생성
R소켓 클라이언트는 외부로부터 HTTP 요청을 받아서 R소켓 연결을 통해 백엔드 서버로 요청을 전달한다  
그래서 HTTP 요청을 받을 수 있는 WebFlux 컨트롤러가 필요하다

R소켓에 스프링의 메시징 패러다임은 포함되지 않았다  
**RSocketRequester**를 사용해야 스프링 프레임워크와 연동된다  
이렇게 하면 도착지를 기준으로 라우팅할 수 있다  
그리고 보너스로 트래픽의 인코딩/디코딩도 쉽게 할 수 있다  
**RSocketRequester**를 사용하지 않으면 클라이언트와 서버 양쪽의 모든 R소켓 연결에서 데이털르 직접 관리해야 한다

리액터의 **Mono** 패러다임은 연결을 R소켓 연결 세부정보를 포함하는 지연 구조체로 전환한다  
아무도 연결하지 않으면 R소켓은 열리지 않는다  
누군가 구독을 해야 세부정보가 여러 구독자에게 공유될 수 있다

하나의 R소켓만으로 모든 구독자에게 서비스할 수 있다는 점도 중요하다  
R소켓을 구독자마다 1개씩 만들 필요가 없다  
대신에 하나의 R소켓 파이프에 대해 구독자별로 하나씩 연결을 생성한다

이렇게 준비 과정을 마쳐야 R소켓이 네트워크를 통해 오가는 데이터 프레임을 리액티브하게 전송하고 배압을 처리하는 데 집중할 수 있다

### 웹플럭스 요청을 R소켓 요청-응답으로 전환
스프링 웹플럭스와 R소켓 API가 모두 프로젝트 리액터를 사용하는 덕분에 둘을 아주 매끄럽게 함께 사용할 수 있다  
둘은 하나의 플로우 안에서 체이닝으로 연결될 수 있어서 HTTP 웹요청을 받아서 R소켓 연결에 전달하고 응답을 받아서  
클라이언트에 리액티브하게 반환할 수 있다

요청-응답 서비스와 클라이언트는 테스트하기도 편리하다

### 웹플럭스 요청을 R소켓 요청-스트림으로 전환
스트림을 반환해야 하므로 데이터를 `Mono<ResponseEntity>`에 담지 않고 **Flux**에 담아 반환한다  
미디어 타입도 `application/x-ndjson`으로 지정해야 스트림 방식으로 반환할 수 있다  
**ndjson**은 **Newline Delimited JSON**의 약자인데 결국 여러 **JSON** 객체를 줄바꿈으로 구분해서 여러 번에 걸쳐 스트림으로 반환한다는 뜻이다

검증 첫 부분에 **StepVerifier**가 나오지 않고 `returnResult()`, `getResponseBody()`를 통해 일단 플로우에서 빠져나온 다음에  
**StepVerifier**를 사용해서 검증을 시작한다

### 웹플럭스 요청을 R소켓 실행 후 망각으로 전환
함수형 프로그래밍에서 비어 있는 Void를 무시하는 것은 `map()`이나 `flatMap()`이나 마찬가지다  
그래서 `Mono<Void>`를 `map()`이나 `flatMap()`을 사용해서 다른 것으로 전환하는 것은 불가능하다

### 웹플럭스 요청을 R소켓 채널로 전환
R소켓의 양방향 채널 지원을 테스트
이벤트 흐름을 구독할 수 있는 단일 메시지를 전송하는 예제 

클라이언트 애플리케이션을 재실행하고 터미널에서 `curl -v localhost:8080/items`를 실행하면  
클라이언트로부터의 결과를 기다린다  
앞에서 작성한 두 가지 테스트 케이스를 실행하면 `curl` 실행 중인 터미널에 새로 생성된 Item 결과가 표시되는 것을 볼 수 있다

`curl` 명령을 실행하면 R소켓 클라이언트가 **Content-Type** 헤더값이 `text/event-stream`인 스트림을 응답한다  
스트림 응답을 받으면 `curl`은 전체 결과를 모두 가져올 수 있을 때까지 기다렸다가 모두 받은 후 실행을 종료하는 방식으로 동작하지 않고  
결괏값이 생길 때 마다 결과를 화면에 표시하고 실행을 종료하지 않고 추가로 응답을 받을 수 있는 대기 상태로 남는다

새 Item을 몽고디비에 저장한 후 요청-응답 방식처럼 반환하고 끝내는 것이 아니라  
양방향 채널을 통해 결괏값을 지속적으로 보내는 동작 방식을 보여준다

> R소켓 클라이언트 애플리케이션을 재실행하고 앞에서 작성한 요청-응답, 요청-스트림, 실행 후 망각 방식 테스트 케이스를 실행하면서
> 실질적으로 R소켓 애플리케이션 인스턴스를 또 실행하는 구조라서 불필요하게 복잡해 보인다
> R소켓 클라이언트 애플리케이션을 재실행하고, 터미널(A)에서 curl -v localhost:8080/items를 실행해서 R소켓 클라이언트로부터 들어오는
> 스트림을 받을 수 있게 해두고, 별도의 새 터미널(B)에서 ㄷ4ㅏ음 명령을 각각 실행해서 R소켓 클라이언트에 요청을 보내면
> R소켓 클라이언트 애플리케이션 인스턴스를 1개만 띄우고도 거의 같은 시나리오로 동작한다

A 터미널에서 `curl -v localhost:8080/items`를 실행하면 다음과 같이 응답을 대기한다
```bash
$ curl -v localhost:8080/items
*   Trying ::1...
* TCP_NODELAY set
* Connected to localhost (::1) port 8080 (#0)
> GET /items HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.64.1
> Accept: */*
> 
< HTTP/1.1 200 OK
< transfer-encoding: chunked
< Content-Type: text/event-stream;charset=UTF-8
<
```

B 터미널에서 다음과 같이 **요청-응답** 쪽에 Item 생성 요청을 전송하면 다음과 같이 생성된 Item 데이터가 결과로 표시된다
```bash
$ curl -X POST -H "Content-Type:application/json" localhost:8080/items/request-response -d "{\"name\":\"Alf alarm clock\",\"description\":\"nothing important\",\"price\":19.99}"
{"id":"6148b752e2921e0fdafa7475","name":"Alf alarm clock","description":"nothing important","price":19.99}%
```

A 터미널에도 다음과 같이 생성된 Item 데이터가 결과로 표시된다
```bash
< HTTP/1.1 200 OK
< transfer-encoding: chunked
< Content-Type: text/event-stream;charset=UTF-8
< 
data:{"id":"6148a9dde2921e0fdafa7472","name":"Alf alarm clock","description":"nothing important","price":19.99}
```

B 터미널에 다음과 같이 **실행 후 망각** 쪽으로 Item 생성 요청을 전송하면 Item 데이터 없이 헤더 정보만 결과로 표시 된다
```bash
$ curl -X POST -H "Content-Type:application/json" -i localhost:8080/items/fire-and-forget -d "{\"name\":\"Smurf TV tray\",\"description\":\"kids TV tray\",\"price\":24.99}"
HTTP/1.1 201 Created
Location: /items/fire-and-forget
content-length: 0
```

A 터미널에도 다음과 같이 생성된 Item 데이터가 결과로 표시된다
```bash
< HTTP/1.1 200 OK
< transfer-encoding: chunked
< Content-Type: text/event-stream;charset=UTF-8
< 
data:{"id":"6148a9dde2921e0fdafa7472","name":"Alf alarm clock","description":"nothing important","price":19.99}

data:{"id":"6148b892e2921e0fdafa7476","name":"Smurf TV tray","description":"kids TV tray","price":24.99}
```

이제 B 터미널에서 **요청-스트림** 조회 요청을 전송하면 앞에서 저장된 2건의 Item 정보가 1초에 1개씩 표시되는 것을 확인할 수 있다
```bash
$ curl -H "Accept:application/x-ndjson" localhost:8080/items/request-stream
{"id":"6148b64be2921e0fdafa7473","name":"Alf alarm clock","description":"nothing important","price":19.99}
{"id":"6148b892e2921e0fdafa7476","name":"Smurf TV tray","description":"kids TV tray","price":24.99}
```

A 터미널에서는 2건의 Item 정보가 한 번에 표시된다  
왜냐하면 A 터미널에 표시되는 **/items** 쪽에는 `delayElements()`를 추가하지 않았기 때문이다

예제는 한쪽에서 요청을 보내고 다른 한쪽은 응답을 반환하고 있지만 실제로는 양방향이기 때문에  
양쪽 모두 서로에게 요청을 보내고 응답을 받을 수 있다

> 예제 코드에서는 R소켓 사용 방식을 URI에 포함했지만 실제 애플리케이션에서는 fire-and-forget이나 request-response 같은 이름을
> URI로 사용하지는 않을 것이다
> 대신에 newItem.save 같은 비즈니스 기능을 이름으로 사용해서 의도를 더 분명히 나타낼 수 있다

## 8장에서 배운 내용
- 네 가지 R소켓 패러다임: 요청-응답, 요청-스트림, 실행 후 망각, 채널
- 네티를 웹 컨테이너로 사용하고 TCP를 전송 프로토콜로 사용하는 R소켓 서버 생성
- 웹 요청을 R소켓을 통해 전달하는 R소켓 클라이언트 설정
- 스프링 포트폴리오와 리액터를 활용해서 기능적 코드와 전송 프로토콜인 R소켓을 매끄럽게 연동하는 방법

# PART 9. 스프링 부트 애플리케이션 보안
- 다양한 사용자 정보 저장소를 사용하는 스프링 시큐리티 설정
- HTTP 엔드포인트에 라우트 기반 보안 설정 적용
- 리액티브 엔드포인트에 메소드 수준 보안 적용
- 권한 검사를 위한 스프링 시큐리티 컨텍스트 연동

## 스프링 시큐리티 시작하기

### 스프링 부트 프로젝트에 스프링 시큐리티 의존관계 추가

Maven
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
```

Gradle
```groovy
implementation 'org.springframework.boot:spring-boot-starter-security'
testImplementation 'org.springframework.security:spring-security-test'
```

서버기동시 password 출력
```bash
Using generated security password: 76944ff8-5790-4f9a-b749-823ed6ee61c1
```

기본 로그인    
- username: user
- passworkd: 76944ff8-5790-4f9a-b749-823ed6ee61c1(무작위로 생성된 PASSWORD)

스프링 시큐리티는 다음과 같은 다중 계층 방식으로 광범위한 보안을 적용  
- 여러가지 필터가 생성되고 적절한 순서로 등록된다
- 웹 페이지에 다양한 지시어가 추가된다
  - 바람직하지 않은 정보가 브라우저 캐시에 유입되는 것 방지
  - clickjacking(https://owasp.org/www-community/attacks/Clickjacking, https://ko.wikipedia.org/wiki/%ED%81%B4%EB%A6%AD%EC%9E%AC%ED%82%B9)
  - session fixation(https://owasp.org/www-community/attacks/Session_fixation, https://anjoliena.tistory.com/10)
  - XSS projections(https://owasp.org/www-community/attacks/xss/, https://ko.wikipedia.org/wiki/%EC%82%AC%EC%9D%B4%ED%8A%B8_%EA%B0%84_%EC%8A%A4%ED%81%AC%EB%A6%BD%ED%8C%85)
  - 등 보안 위험 방어
  - 서버 응답에 적절한 보안 헤더 추가
  - Cross Site Request Forgery, CSRF(https://owasp.org/www-community/attacks/csrf, https://ko.wikipedia.org/wiki/%EC%82%AC%EC%9D%B4%ED%8A%B8_%EA%B0%84_%EC%9A%94%EC%B2%AD_%EC%9C%84%EC%A1%B0
    방지 활성화

## 실무 적용
개발자가 작성한 몽고디비 리포지토리와 스프링 시큐리티를 연결해서 사용자 세부정보를 스프링 시큐리티를 통해 관리할 수 있게됨

SecurityConfig 클래스에서 MongoOperations를 사용해서 미리 저장해둔 사용자 정보로 로그인하면 된다

> 자동설정을 통해 MongoOperations 빈을 사용하려면 pom.xml 파일에 mongodb-driver-sync 의 의존관계를 추가해야 한다
> 그렇지 않으면 리액티브 스프링 데이터 몽고디비 스타터는 기본적으로 MongoOperations가 아니라 ReactiveMongoOperations 빈을 사용한다

- Username에 'greg', Password에 'password'를 입력하고 Sign in 버튼을 클릭해서 로그인한다
- 로그인이 성공하면 초기 페이지로 다시 리다이렉트 된다

스프링 부트는 스프링 시큐리티가 제공하는 `@EnableWebFluxSecurity` 애너테이션을 적용할지 말지 결정한다  
`@EnableWebFluxSecurity`가 적용되면 스프링 시큐리티는 기본적으로 다음 기능을 활성화 한다  
- HTTP BASIC을 활성화해서 cURL 같은 도구로도 계정명/비밀번호 값을 전송할 수 있다
- HTTP FORM을 활성화해서 로그인되지 않은 사용자는 브라우저의 기본 로그인 팝업 창 대신에
  스프링 시큐리티가 제공하는 로그인 페이지로 리다이렉트한다
- 사용자가 로그인에 성공해서 인증이 완료되면 애플리케이션의 모든 자원에 접근 가능하다
  이는 인증만 받으면 애플리케이션 자원에 접근하기 위해 추가적인 허가가 필요하지 않음을 의미한다

> 스프링 부트는 개발자가 무엇을 하려는지 추측을 하고 그에 따라 필요한 빈을 자동설정을 통해 등록한다
> 그래서 클래스패스에 웹플럭스가 있으면 리액터 네티 인스턴스를 실행하고 몇 가지 뷰 리졸버를 자동으로 사용할 수 있게 해준다
> 하지만 스프링 시큐리티는 설정 방법이 매우 다양하고 애플리케이션마다 천차만별이라서 스프링 시큐리티가 클래스패스에 있다는 것만으로 절절한 추측을 하는 것이 사실상 불가능하다

> 스프링 부트 1.X 버전까지는 어느 정도 추측을 하고 일정한 선택지를 제공했다
> 하지만 스프링 부트가 하려는 것과 스프링 시큐리티가 하려는 것을 파악하기가 쉽지 않다
> 그래서 2.X 버전부터는 스프링 부트는 스프링 시큐리티를 단순히 활성화하는 역할만 맡는다
> 하지만 @EnableWebFluxSecurity를 직접 명시할 때 생성되는 WebFilterChainProxy 빈을 찾으면 스프링 부트는 스프링 시큐리티 설정과 관련한 권한을 개발자에게 모두 넘겨준다

> 이번 절의 예제에서처럼 개발자가 만든 ReactiveUserDetailsService를 사용해도 기본값이 사용되는 것을 허용한다
> 스프링 시큐리티는 기존의 의존관계 주입을 통해서 여러 빈이 등록되는 것을 허용한다
> 하지만 다음 절부터 알아볼 여러 가지 정책을 지정하면서 스프링 부트는 결국 개발자에게 운전석을 넘겨준다