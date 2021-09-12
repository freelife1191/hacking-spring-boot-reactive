package com.greglturnquist.hackingspringboot.reactive;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 몽고디비 슬라이스 테스트
 * Created by KMS on 2021/09/12.
 */
// tag::code[]
// @ExtendWith({SpringExtension.class})를 포함하고 있으므로 JUnit 5 기능을 사용할 수 있음
@DataMongoTest // <1>스프링 데이터 몽고디비 활용에 초점을 둔 몽고디비 테스트 관련 기능을 활성화
public class MongoDbSliceTest {

    @Autowired //빈 주입
    ItemRepository repository; // <2>

    @Test
        // <3>
    void itemRepositorySavesItems() {
        Item sampleItem = new Item( //
                "name", "description", 1.99);

        // StepVerifier를 사용한 테스트 작성
        repository.save(sampleItem) //
                .as(StepVerifier::create) //
                .expectNextMatches(item -> {
                    assertThat(item.getId()).isNotNull();
                    assertThat(item.getName()).isEqualTo("name");
                    assertThat(item.getDescription()).isEqualTo("description");
                    assertThat(item.getPrice()).isEqualTo(1.99);

                    return true;
                }) //
                .verifyComplete();
    }
}
// end::code[]