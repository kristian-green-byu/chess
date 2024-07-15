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

[![New Sequence Diagram]](https://sequencediagram.org/index.html#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIei0azF5vDgHYsgwr5kvDhWExPB+Sy7H0ALnAWspqig5QIAePKwvuh6ouisTYgmhgumGbpkhSBq0uWo4mkS4YWhyMDcryBqCsKMCvmIrrSkml6weUtHaI6zoErhLL4WAsb+tOs7oO++ivEspFMm6FHlNGMBCfGcrYcmUElihPLZrmmD-iCMElFcAzEaMC6Tn0ImNs246thB7b6YU2Q9jA-aDr0xkjqZNnVlOQaifO3ltsuq7eH4gReCg6B7gevjMMe6SZJgTkXkU1DXtIACiu4ZfUGXNC0D6qE+3SWXO9lsnpVyQUCHYGeCMAIfYsXITFvpoRimEqRqfGkkYKDcJkQkBn5VloNJZoRoUloyP1FKGEpXFYZ2lXRU1vrpjmCB5upUCsT+pQ9N+V4OV2ORgH2A5DkunAheugSQrau7QjAADio6svFp5JeezAGdeL05fl9ijiVI1lT+FU7VVuk7WyWHwdCb2jKoyGI+97UYdxia8WReENWjyPDXWo3jeRkaUU9sSve9oa4yxdXykjahY6pf5Q3uBNqBtOl6Z2+2llMwPI+MlT9ELKAAJLSCsXwnpkBpAX0Xw6AgoANgro4y084sAHKjhMEEwI0R1pSdyXnZUrmNEOgvvSLFRi6OUta1Mcv6iZ9xK08KtqxrpkG9ro566MBt7Eb10rp4oUbtgPhQNg3DwLqmTU6MKQJWeZ1w8d5Q3g0QMg8EYPoEOuujibxxqTVaaO6MwdjhOdl6QU8MwJ6epM7CcDJygTMY1iLMFMxpLkmAndl6MpNugU03UbaTNCmETP17TMksntKmlIqyqD-kw-um3Ped6VYkwBPKBT+v+SWt3Xop0zLPLezt8d6O3NbTD1cb6bB223XmvlAAIy9gAMwABYYAV1qqcc2pQKhWxtn0c+ItgHgMgWYG6Uc7oBEsP1BCyQYAACkIA8lToYAIPsQANm+lnP6aZqiUjvC0cWoNiZziHAnYAuCoBwAgAhKAsxz5QN2vkSqPQ5iq24bw-hKwADqLAJa5RaAAIV3AoOAABpL4yCgGgIgdVVMLdN4ACsSFoE7sQnkfcUBog6rvfepRR7H2LmgQRQcSKrwmjPDkc8yGLzISvfe38nSM1HPYnq7o-BaEyM4thp9OFSL4dANx-9J6ePDN40olJsDRMMA-LCrEVqWPMW-bSH9m5GJ-odeyrFYHwIHNbXoEdbphQCF4LhXYvSwGANgBOhB4iJHTl9c22cf6VEytlXK+VjA1NEezapzd8jwxANwPAChenIBAAMtAXdVlZhsehAeBS94RI9Hs9J09r4ckQF0mQ2g9AGEHgUSqNy8Dv22l-SphlSzCNqT9OBCCmkYJXEAA)
