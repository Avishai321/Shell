package shell.core;

import sun.misc.Signal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Shell {
    private final ShellContext context;
    private final BufferedReader reader;

    public Shell() {
        this.context = new ShellContext();

        InputStreamReader decoder = new InputStreamReader(System.in, StandardCharsets.UTF_8);
        this.reader = new BufferedReader(decoder);

        //todo: there is something that needs to be resolved before executing the program
        // mvn gives errors, here is one:
        //  [WARNING] /home/avishai/development/Java/Shell/src/main/java/shell/core/Shell.java:[3,16]
        //  sun.misc.Signal is internal proprietary API and may be removed in a future release
        Signal.handle(
                new Signal("INT"),
                sig -> System.out.print("\n" + this.context.getPwd() + "> ")
        );
    }

    public void start() {
        while (this.context.isRunning()) {
            System.out.print(this.context.getPwd() + "> ");
            try {
                String input = this.reader.readLine();
                if (input.isBlank()) continue;

                // todo: map those into real commands instead of nested if-else statements
                if (input.equalsIgnoreCase("exit")) {
                    System.exit(0);
                }

                System.out.println(input);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
