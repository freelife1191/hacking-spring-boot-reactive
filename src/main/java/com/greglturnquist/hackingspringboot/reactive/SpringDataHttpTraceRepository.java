package com.greglturnquist.hackingspringboot.reactive;

import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by KMS on 2021/09/16.
 */
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
