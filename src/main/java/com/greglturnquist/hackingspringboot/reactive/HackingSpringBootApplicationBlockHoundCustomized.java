package com.greglturnquist.hackingspringboot.reactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.thymeleaf.TemplateEngine;
import reactor.blockhound.BlockHound;

/**
 * Created by KMS on 2021/09/12.
 */
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
