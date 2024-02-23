FROM openjdk:19-buster

WORKDIR /src

RUN git clone https://github.com/Aventix/aventix-bot.git .
RUN ls

COPY discord-bot/* .

RUN ./gradlew build
COPY build/libs/aventix-bot-1.0-SNAPSHOT.jar .

ENTRYPOINT ["java", "-jar", "aventix-bot-1.0-SNAPSHOT.jar"]
