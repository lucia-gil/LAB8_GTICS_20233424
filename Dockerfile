FROM eclipse-temurin:17-jre
RUN groupadd -r appgroup && useradd -r -g appgroup appuser
VOLUME /tmp
EXPOSE 8080
ADD target/lab8.jar app.jar
USER appuser
ENTRYPOINT ["java","-jar","app.jar"]
