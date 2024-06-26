# These are my notes

## Lecture 2

How to write basic Java file that reads commandline arguments:
```public class CommandLineArgs {
    public static void main(String[] args) {
        //static method is a method on the class. This function is called CommandLineArgs.main
        /*We are going to write a program that prints our args.*/
        System.out.println("Here are your arguments:");
        for (int i = 0; i < args.length; i++) {
            System.out.println("  " + args[i]);
        }

        /*We can also iterate through our array with a for each loop:
        * for (String a: args) {
        *   System.out.println(a);
        * }
        * */
    }
}