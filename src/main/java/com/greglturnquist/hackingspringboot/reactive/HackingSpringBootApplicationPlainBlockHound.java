package com.greglturnquist.hackingspringboot.reactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.blockhound.BlockHound;

/**
 * Created by KMS on 2021/09/12.
 */
@SpringBootApplication
public class HackingSpringBootApplicationPlainBlockHound {

    // tag::blockhound[]
    public static void main(String[] args) {
        BlockHound.install(); //블록하운드 등록

        SpringApplication.run(HackingSpringBootApplicationPlainBlockHound.class, args);
    }
    // end::blockhound[]
}
