import java.util.Scanner;
import static ui.EscapeSequences.*;

public class repl {
    private final ChessClient client;

    public repl(String url) {
        this.client = new ChessClient(url);
    }
    public void run(){
        System.out.println(WHITE_KING + " Welcome to CS240 Chess. Type help to receive a list of commands. "+BLACK_KING);
        var result = "";
        while (!result.equals("quit")){
            Scanner scanner = new Scanner(System.in);
            while (!result.equals("quit")) {
                printPrompt();
                String line = scanner.nextLine();

                try {
                    result = client.eval(line);
                    System.out.print(SET_TEXT_COLOR_BLUE + result);
                } catch (Throwable e) {
                    System.out.print(e.getMessage());
                }
            }
            System.out.println();
        }
    }
    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_BOLD_FAINT + ">>> " + SET_TEXT_COLOR_GREEN);
    }
}
