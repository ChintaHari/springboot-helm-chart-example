FROM openjdk:17

WORKDIR /app

COPY ./target/springboot-helm-chart-example.jar /app

EXPOSE 8080

CMD ["java", "-jar", "springboot-helm-chart-example.jar"]

