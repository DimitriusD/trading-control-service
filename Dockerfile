# syntax=docker/dockerfile:1

FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle gradle
COPY application application
COPY infrastructure infrastructure
# OpenAPI contracts come from GitHub Packages, which requires a token even for reads
RUN --mount=type=secret,id=gpr_user,env=GITHUB_ACTOR \
    --mount=type=secret,id=gpr_key,env=GITHUB_TOKEN \
    --mount=type=cache,target=/root/.gradle \
    ./gradlew :infrastructure:app:bootJar --no-daemon --console=plain
RUN java -Djarmode=tools -jar infrastructure/app/build/libs/app.jar extract --layers --destination extracted

FROM eclipse-temurin:21-jre
RUN groupadd --system app && useradd --system --gid app app
WORKDIR /app
COPY --from=build /workspace/extracted/dependencies/ ./
COPY --from=build /workspace/extracted/spring-boot-loader/ ./
COPY --from=build /workspace/extracted/snapshot-dependencies/ ./
COPY --from=build /workspace/extracted/application/ ./
USER app
EXPOSE 8095
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75"
ENTRYPOINT ["java", "-jar", "app.jar"]
