package com.greglturnquist.hackingspringboot.reactive;

import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.data.annotation.Id;

/**
 * Created by KMS on 2021/09/16.
 */
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
