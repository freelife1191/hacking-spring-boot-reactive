# 빌더(builder)로 사용할 컨테이너를 만든다
FROM adoptopenjdk/openjdk11:latest as builder
WORKDIR application
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
# extract 명령으로 레이어를 추출한다
RUN java -Djarmode=layertools -jar app.jar extract

# 두 번째 컨테이너를 만든다
FROM adoptopenjdk/openjdk11:latest
WORKDIR application
# 빌더 컨테이너에서 추출한 여러 레이어를 두 번째 컨테이너에 복사한다
# COPY 명령에는 도커의 계층 캐시 알고리즘이 적용된다
# 그래서 서드파티 라이브러리는 캐시될 수 있다
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
# snapshot 의존관계가 없어 해당 계층이 만들어지지 않으면 주석 처리
#COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./
# java -jar가 아니라 스프링 부트의 커스텀 런처(custom launcher)로 애플리케이션을 실행한다
# 이 린처는 애플리케이션 시작 시 불필요한 JAR 파일 압축 해제를 하지 않으므로 효율적이다
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]