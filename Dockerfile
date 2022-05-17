FROM eclipse-temurin:17-jre-alpine
EXPOSE 9000
ADD /build/libs/rate-limit.jar rate-limit.jar
ENTRYPOINT ["java","-jar","rate-limit.jar"]