import server.Server;

public class Main {
    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client: ♕");
        Server server = new Server();
        server.run(8080);
    }
}