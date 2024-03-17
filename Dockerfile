FROM openjdk:19-buster as builder

WORKDIR /src

COPY . .

RUN sh gradlew installDist

FROM openjdk:19-alpine

WORKDIR /opt/aventix-bot

COPY --from=builder /src/build/install/aventix-bot ./

ENTRYPOINT ["sh", "./bin/aventix-bot"]