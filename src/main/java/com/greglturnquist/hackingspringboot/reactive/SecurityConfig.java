package com.greglturnquist.hackingspringboot.reactive;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.Arrays;
import java.util.Collections;

/**
 * 스프링 시큐리티 설정
 * Created by KMS on 2021/09/21.
 */
@Configuration
// 메서드 수준 보안 활성화
@EnableReactiveMethodSecurity
public class SecurityConfig {

    /**
     * ReactiveUserDetailsService 빈
     * @param repository
     * @return
     */
    @Bean
    public ReactiveUserDetailsService userDetailsService(UserRepository repository) {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        // ReactiveUserDetailService는 username을 인자로 받아서 Mono<UserDetails>를 반환하는
        // 단 하나의 메소드만 가지고 있다
        // 그래서 자바 8의 람다를 사용해서 간단하게 정의할 수 있으며
        // UserRepository의 findByName() 메소드를 사용하는 것으로 시작한다
        return username -> repository.findByName(username)
                // 개발자가 정의한 User 객체를 조회했으면 map()을 활용해서 스프링 시큐리티의 UserDetails 객체로 변환한다
                // 스프링 시큐리티의 User 타입은 username, password, authorities와 함께 비밀번호 인코더를 지정할 수 있는 평문형 API를 제공한다
                // .map(user -> User.withDefaultPasswordEncoder() // <3>
                //         .username(user.getName()) //
                //         .password(user.getPassword()) //
                //         .authorities(user.getRoles().toArray(new String[0])) //
                // @Deprecated 로 아래와 같이 변경
                .map(user -> User.withUsername(username)
                        .password(encoder.encode(user.getPassword()))
                        .authorities(user.getRoles().toArray(new String[0]))
                        // 마지막으로 평문형 API의 build() 메소드를 사용해서 UserDetails 객체를 만들어낸다
                        .build());
    }

    static final String USER = "USER";
    static final String INVENTORY = "INVENTORY";

    /**
     * 커스텀 정책 작성
     * 단순해진 보안 정책
     * @param http
     * @return
     */
    @Bean
    SecurityWebFilterChain myCustomSecurityPolicy(ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchanges -> exchanges
                        // 이 코드의 규칙에 어긋나는 모든 요청은 이 지점에서 더 이상 전진하지 못하며
                        // 사용자 인증을 거쳐야만 이 지점을 통과할 수 있다
                        .anyExchange().authenticated()
                        .and()
                        // HTTP BASIC 인증을 허용한다
                        .httpBasic()
                        .and()
                        // 로그인 정보를 HTTP FORM으로 전송하는 것을 허용한다
                        .formLogin())
                .csrf().disable()
                .build();
    }

    static String role(String auth) {
        return "ROLE_" + auth;
    }

    /**
     * 각기 역할이 다른 테스트용 사용자 추가
     * @param operations
     * @return
     */
    @Bean
    CommandLineRunner userLoader(MongoOperations operations) {
        return args -> {
            // USER
            operations.save(new com.greglturnquist.hackingspringboot.reactive.User(
                    "greg", "password", Collections.singletonList(role(USER))));

            // USER와 INVENTORY
            operations.save(new com.greglturnquist.hackingspringboot.reactive.User(
                    "manager", "password", Arrays.asList(role(USER), role(INVENTORY))));
        };
    }
}
