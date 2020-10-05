FROM maven:3.6.3-jdk-11-openj9 as build

COPY ./ /

RUN mvn install

FROM openjdk:11-jre

LABEL maintainer="IBM Java Engineering at IBM Cloud"

COPY --from=build  target/sdk-kubernetes-secret-wallet-1.0-SNAPSHOT.jar /app.jar

EXPOSE 8080

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
