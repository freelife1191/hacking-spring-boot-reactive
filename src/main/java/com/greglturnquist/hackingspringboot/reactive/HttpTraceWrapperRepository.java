package com.greglturnquist.hackingspringboot.reactive;

import org.springframework.data.repository.Repository;

import java.util.stream.Stream;

/**
 * Created by KMS on 2021/09/16.
 */
public interface HttpTraceWrapperRepository extends Repository<HttpTraceWrapper, String> {
    // findAll() 메소드는 저장된 모든 HttpTraceWrapper를 자바 11의 스트림으로 반환한다
    Stream<HttpTraceWrapper> findAll();
    // save() 메소드는 HttpTraceWrapper 객체를 저장한다
    void save(HttpTraceWrapper trace);
}
