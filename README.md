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