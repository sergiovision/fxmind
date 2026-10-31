# syntax=docker/dockerfile:1.7
# ---- build stage -----------------------------------------------------------------
FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY businesslogic/pom.xml businesslogic/
COPY webclient/pom.xml webclient/
RUN --mount=type=cache,target=/root/.m2 ./mvnw -B -q dependency:go-offline -Denforcer.skip
COPY businesslogic/src businesslogic/src
COPY webclient/src webclient/src
RUN --mount=type=cache,target=/root/.m2 ./mvnw -B -Pproduction package -DskipTests -Dspotless.check.skip -Djacoco.skip \
    && java -Djarmode=tools -jar webclient/target/fxmind.jar extract --layers --launcher --destination /workspace/extracted

# ---- runtime stage ---------------------------------------------------------------
FROM eclipse-temurin:25-jre AS runtime
RUN groupadd --system fxmind && useradd --system --gid fxmind --home /app fxmind
WORKDIR /app
COPY --from=build /workspace/extracted/dependencies/ ./
COPY --from=build /workspace/extracted/spring-boot-loader/ ./
COPY --from=build /workspace/extracted/snapshot-dependencies/ ./
COPY --from=build /workspace/extracted/application/ ./
USER fxmind
EXPOSE 8080 2011
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError"
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
