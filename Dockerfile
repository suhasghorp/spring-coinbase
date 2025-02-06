FROM bellsoft/liberica-runtime-container:jdk-21-stream-musl AS builder
WORKDIR /home/app
COPY . ./spring-coinbase
RUN cd spring-coinbase && ./mvnw -Dmaven.test.skip=true clean package

FROM bellsoft/liberica-runtime-container:jdk-21-cds-slim-musl AS optimizer

WORKDIR /home/app
COPY --from=builder /home/app/spring-coinbase/target/*.jar spring-coinbase.jar
RUN java -Djarmode=tools -jar spring-coinbase.jar extract --layers --launcher --destination extracted

FROM bellsoft/liberica-runtime-container:jre-21-stream-musl

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
EXPOSE 8080
COPY --from=optimizer /home/app/extracted/dependencies/ ./
COPY --from=optimizer /home/app/extracted/spring-boot-loader/ ./
COPY --from=optimizer /home/app/extracted/snapshot-dependencies/ ./
COPY --from=optimizer /home/app/extracted/application/ ./