package com.greglturnquist.hackingspringboot.reactive;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

/**
 * Created by KMS on 2021/09/11.
 */
@Controller
public class HomeController {

    @GetMapping
    Mono<String> home() {
        return Mono.just("home");
    }
}
