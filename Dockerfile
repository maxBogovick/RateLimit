FROM openjdk:17
EXPOSE 9000
ADD /build/libs/rate-limit.jar rate-limit.jar
ENTRYPOINT ["java","-jar","rate-limit.jar"]