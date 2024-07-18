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
[Updated Sequence Diagram](https://sequencediagram.org/index.html#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIei0azF5vDgHYsgwr5kvDhWExPB+Sy7H0ALnAWspqig5QIAePKwvuh6ouisTYgmhgumGbpkhSBq0uWo4mkS4YWhyMDcryBqCsKMCvmIrrSkml6weURraI6zoErhLLlJ63pBgGQYhsx5qRpR0YwLGwZcVhnb-mmKE8tmuaYEpnY-lcAzEaMC6Tn006zs246thB7Ygqc2Q9jA-aDr0ukjvpZnVlOQYmfOrltsuq7eH4gReCg6B7gevjMMe6SZJgNkXkU1DXtIACiu5JfUSXNC0D6qE+3TGY26CWWySlXJBQIdjBTryjACH2OFyFhb6aEYphcooDhZF4eSYCySJdYFWgpFMm6FHlAA4hSMmidoMDJBkqRTf1c6hp1LKsVh5TTvGbUanxpJGCg3CZL1W3aENZoRoUloyIdFKGLJ22wYpUElih4XpjmCB5i9UDrVeZQ9N+-2drFYB9gOQ5LpwfnroEkK2ru0IwGNo6spFp4xeezCVdeY1pZl9ijnlHkDUVyY-aVGk-WyG01dCKOjKoyH06jzUYdxKgdcN-HdSdJNzud5GSeNk0PTNc1+otM6k+Jqh-exMCnfIHPYbxq37cgsQM2ofVyfIgsjcLyOTYjsQwBAABmyOoyt3MSZV4LaxoCmsWc5XKSzjMfepmmu9ppZTITjPjJU-RBygACS0grF8J6ZAaQF9F8OgIKADYJ6OMdPOHAByo4TBBMCNEDCVWYUoPlBU9mNEOgeoyHFRh6OUdZ1Mcf6np9xJ08Kdpxn+kF9no556MBd7EXUMrp4-kbtgPhQNg3DwLqmTW6MKRRWeOTY2xJSV7UDQE0TwT8+gQ656OJfHOT7s6RfLkThZSkFLTgmZNrsJwCvKDa2zWIq1zC6pRebCXygLW2F1RrG2YGLeQs15pS08hA8M8sqpwUVtNZWWECiywEt-D+StgCzHvigA2a18hRhtGvJie05YvzaqUbWUcVbPVvsvL079Rzey+lTd2qDLg9DrqMFu5QACMvYADMAAWGAV8KrWSxqUKuA4a69CEZHaOYjJEyMnjDAKARLCHQQskGAAApCAPJqGBF7iABsmNt403+vvSkd4Wjh2JktM+vRF7AEMVAOAEAEJQGIc3aQcjfr5BKoI54qc-EBKCSsAA6iwCO6UWgACFdwKDgAAaS+OHERMBxHSNkbw1M9D2IACsLFoA-uYnkv8UBohagAtWdt3QgL9LrTyZCJJXUohNGBmDgDwMlrJJBstUHgilo9NBODaHAIpAQ4ZIThHSF6XLChHJqK2m1kKMITDpDILdFM+U2tWm4L8FoThoxdaMnfLEyg8ToCzG1hsgolDbRVEebASA1CAEFBKmYmpey1I8OfhUvegNLKsQrsopwqjoW+WnrDAIXhfFdi9LAYA2BF6EHiIkDeGNQaONLpXZKqV0qZWMDCyJFNSxlXKfkDaIBuB4AULi5AIACVoE-myrMTT0L-2wfkXBrKsWwneVs0oiAsUyWVHaAUXFJnStlXgeVCAZnKtoR8jkarYAKAVU7AFdLb76u4d9PhkKALhNhYo+FiLdFAA)
