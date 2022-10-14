FROM openjdk:8
COPY *.java /usr/src/quotes/
WORKDIR /usr/src/quotes
RUN javac Server.java
EXPOSE 5000
CMD ["java", "Server"]