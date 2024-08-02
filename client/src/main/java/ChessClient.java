import server.ServerFacade;

public class ChessClient {
    private final ServerFacade server;

    public ChessClient(String serverUrl) {
        this.server = new ServerFacade(serverUrl);
    }

    public String help() {
        return """
                - list
                - delete <pet id>
                - add <name> <CAT|DOG|FROG|FISH> [<friend name>]*
                - clear
                - quit
                """;
    }
}
