FROM openjdk:11
WORKDIR /app
COPY ./target/scala-2.13/crud-wishlist-scala-sqlite-assembly-0.1.0-SNAPSHOT.jar  .
EXPOSE 8080
ENTRYPOINT ["java","-jar","crud-wishlist-scala-sqlite-assembly-0.1.0-SNAPSHOT.jar"]