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
