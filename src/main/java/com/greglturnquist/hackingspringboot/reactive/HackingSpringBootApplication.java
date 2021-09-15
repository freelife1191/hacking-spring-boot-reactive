package com.greglturnquist.hackingspringboot.reactive;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.convert.NoOpDbRefResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

@SpringBootApplication
public class HackingSpringBootApplication implements ApplicationRunner {

    @Value("${info.project.name}")
    private String name;
    @Value("${info.project.version}")
    private String version;
    @Value("${info.project.group}")
    private String group;
    @Value("${info.java.version}")
    private String javaVersion;
    @Value("${info.spring.boot.version}")
    private String springVersion;

    public static void main(String[] args) {
        SpringApplication.run(HackingSpringBootApplication.class, args);
    }

    // 인메모리 기반 HttpTraceRepository 빈 등록
    @Bean
    HttpTraceRepository traceRepository() {
        return new InMemoryHttpTraceRepository();
    }

    // SpringDataHttpTraceRepository 빈 등록
    @Bean
    HttpTraceRepository springDataTraceRepository(HttpTraceWrapperRepository repository) {
        return new SpringDataHttpTraceRepository(repository);
    }

    /**
     * 몽고디비 Document를 HttpTraceWrapper로 변환하는 컨버터
     */
    static Converter<Document, HttpTraceWrapper> CONVERTER = //
            new Converter<Document, HttpTraceWrapper>() { //
                @Override
                public HttpTraceWrapper convert(Document document) {
                    Document httpTrace = document.get("httpTrace", Document.class);
                    Document request = httpTrace.get("request", Document.class);
                    Document response = httpTrace.get("response", Document.class);

                    return new HttpTraceWrapper(new HttpTrace( //
                            new HttpTrace.Request( //
                                    request.getString("method"), //
                                    URI.create(request.getString("uri")), //
                                    request.get("headers", Map.class), //
                                    null),
                            new HttpTrace.Response( //
                                    response.getInteger("status"), //
                                    response.get("headers", Map.class)),
                            httpTrace.getDate("timestamp").toInstant(), //
                            null, //
                            null, //
                            httpTrace.getLong("timeTaken")));
                }
            };

    /**
     * 스프링 데이터 몽고디비에 커스텀 컨버터 등록
     * @param context
     * @return
     */
    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoMappingContext context) {

        // 몽고디비의 DBRef 값에 해석이 필요할 때 UnsupportedOperationException을 던지는 NoOpDbRefResolver를 사용해서 MappingMongoConverter 객체를 생성한다
        // HttpTrace에는 DBRef 객체가 없으므로 DBRef 값 해석이 발생하지 않으며, NoOpDbRefResolver에 의해 예외가 발생할 일은 없다
        // 단지 MappingMongoConverter의 생성자가 DbRefResolver 타입의 인자를 받기 때문에 NoOpDbRefResolver를 전달하는 것이다
        MappingMongoConverter mappingConverter =
                new MappingMongoConverter(NoOpDbRefResolver.INSTANCE, context);
        // MongoCustomConversions 객체를 mappingConverter에 설정한다
        mappingConverter.setCustomConversions(
                // 커스텀 컨버터 한 개로 구성된 리스트를 추가해서 MongoCustomConversions를 생성한다
                new MongoCustomConversions(Collections.singletonList(CONVERTER))); // <3>

        return mappingConverter;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println(name);
        System.out.println(version);
        System.out.println(group);
        System.out.println(javaVersion);
        System.out.println(springVersion);
    }
}