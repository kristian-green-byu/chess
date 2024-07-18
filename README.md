# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```

## Updated URL to Sequence Diagram:
[New Sequence Diagram](https://sequencediagram.org/index.html#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAHZM9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIei0azF5vDgHYsgwr5kvDhWExPB+Sy7H0ALnAWspqig5QIAePKwvuh6ouisTYgmhgumGbpkhSBq0uWo4mkS4YWhyMDcryBqCsKMCvmIrrSkml6weURraI6zoErhLLlJ63pBgGQYhsx5qRpR0YwLGwZcVhnb-mmKE8tmuaYEpnY-lcAzEaMC6Tn006zs246thB7Ygqc2Q9jA-aDr0ukjvpZnVlOQYmfOrltsuq7eH4gReCg6B7gevjMMe6SZJgNkXkU1DXtIACiu5JfUSXNC0D6qE+3TGY26CWWySnlPlc5DpBQIdjBTryjACH2OFyFhb6aEYphcooDhZF4eSYCySJdYFWgpFMm6FHlAA4hSMmidoMDJBkqSzUNc6hj1LKsVhpVzfI3GJrxG2kkYKDcJkA3TmJfESYUloyKdFKGLJ8adYpUElih4XpjmCB5u9UBbVeZQ9N+QOdrFYB9gOFVmJwfnroEkK2ru0IwJNo6spFp4xeezA1dek1pZl9ijnlHnDUVyb-Ttq3oDDmk1eC9XQujoyqMhLMY21GH7V1h1jfxfUXeTc6jWaEa3ZR03MM982LX6K0zhT4mqID7EwJd8mvd1AvHcgsSs2osJi+RklTTNKOxDAEAAGZoxj626xGjPyobGgKaxZxVcpnNs996kM-jaY9FMJNs+MlT9GHKAAJLSCsXwnpkBpAX0Xw6AgoANino4J080cAHKjhMEEwI0oMJVZhQQ+UFT2Y0Q6hxjEcVFHo5x3nUxJ-qen3GnTwZ1nOf6SX+ejkXowl3sZdLnDnj+Ru2A+FA2DcPAuqZPbowpFFZ45HjbElLXtQNMTpPBCLdO9IXo4V8cVPezTSvlY5N8uROFlKQU20wIJmSG7COAG8UCG25liXmOtxalCFsJMq6ATbjTNmjGast5ALSWorTyjtxZq1qnBDWu1gAQPyCrASwCAFvxQAgza+Qow2i3kxa6qtv6dVKIbOOvM3qP3Xl6f+o5-a-Q0v9XBlwQ59Gjh3coABGXsABmAALDAO+1VrK41KHXAcDdehN1GJImAMiFFKNhiueeCMAiWFOghZIMAABSEAeQMMCIPEADYcb7zZNpSoVRKR3haNHMmtNny9FXsACxUA4AQAQlAWYEjpDKIBvkEqcCgliJCWEiJUSVgAHUWAx3Si0AAQruBQcAADSXxYkRwMYoyqqYWHsQAFb2LQAAuxPJQEoDRO1YhMgmHQIpMLQJ1CbrsnNjLQh6CFaySwSrXBTNUFEKwgUUhfUKHt2kMM1WtCOTUVtIbIUYR2HSGweGOZrtRzENIWkygGToAxxaaOWE1zwmROgLMQ2myCiWl2TAKomd0mvNgA4w2Jy3RnLgoqZUly+l+C0Hw0Yg05LyHfP8m5gL3kkVBTQuhto-mhI7JABhECCglVsc0-ZalBFf3qUfEGllWI1w0U4LRdLfKmICgELwoSuxelgMAbAq9CDxESDvbGEMPFA1rslVK6VMrGHpYk6mpZanAhpeCEA3A8AKAFcgEAwq0CAM1VmTp6FwFLJIX0jVvLjZYvUF8jkiBeUyWVHaAUXFZnbNKI6vAzqECKwdB6y03rYAKBdW7YlirH7BoEX9b2Ijg7xIZWoplLLZ4riAA)
