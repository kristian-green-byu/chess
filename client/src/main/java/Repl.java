import java.util.Scanner;
import static ui.EscapeSequences.*;

public class Repl {
    private final ChessClient client;

    public Repl(int port) {
        this.client = new ChessClient(port);
    }
    public void run(){
        System.out.println("Welcome to CS240 Chess. Type help to receive a list of commands.");
        printPrompt();
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")){
                String line = scanner.nextLine();
                try {
                    result = client.eval(line);
                    System.out.print(SET_TEXT_COLOR_BLUE + result);
                    System.out.println();
                    printPrompt();
                } catch (Throwable e) {
                    System.out.print(e.getMessage());
                }
        }
    }
    private void printPrompt() {
        System.out.print(SET_TEXT_BLINKING + RESET_TEXT_COLOR + "\n" + RESET_TEXT_BOLD_FAINT + ">>> " + SET_TEXT_COLOR_GREEN);
    }
}
