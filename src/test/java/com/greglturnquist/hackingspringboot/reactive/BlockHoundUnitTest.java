package com.greglturnquist.hackingspringboot.reactive;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

/**
 * 단위 테스트 블로킹 코드 검출
 * Created by KMS on 2021/09/12.
 */
class BlockHoundUnitTest {

    // tag::obvious-failure[]
    @Test
    void threadSleepIsABlockingCall() {
        // Mono.delay() 코드를 추가해서 전체 플로우를 리액터 스레드에서 실행되게 만든다
        // 블록하운드는 리액터 스레드 안에서 사용되는 블로킹 코드를 검출할 수 있다
        Mono.delay(Duration.ofSeconds(1)) // <1>
                .flatMap(tick -> {
                    try {
                        // 현재 스레드를 멈추게 하는 블로킹 호출
                        Thread.sleep(10); // <2>
                        return Mono.just(true);
                    } catch (InterruptedException e) {
                        return Mono.error(e);
                    }
                }) //
                .as(StepVerifier::create) //
                .verifyComplete();
                // 테스트 케이스를 통과시키려면 .verifyComplete()를 다음의 코드로 변경하면됨
                // .verifyErrorMatches(throwable -> {
                //     assertThat(throwable.getMessage()) //
                //             .contains("Blocking call! java.lang.Thread.sleep");
                //     return true;
                // });

    }
    // end::obvious-failure[]

}