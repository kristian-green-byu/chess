import server.ServerFacade;

public class ChessClient {
    private final ServerFacade server;

    public ChessClient(String serverUrl) {
        this.server = new ServerFacade(serverUrl);
    }

    public String eval(String line){
        return line;
    }

    public String help() {
        return """
                - register <username> <password> <email>
                - login <username> <password>
                - logout
                - listGames
                - createGame <gameName>
                - joinGame <WHITE|BLACK> <gameID>
                - clear
                - quit
                """;
    }
}
