package command;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is never meant to be instantiated (that's why it has a private constructor).
 * It is used to add commands to a queue (waiting list of commands). And after adding all
 * commands to it, iterate through it and execute each one.
 */

public class Console {

    /**
     * List of {@link Command} objects
     */
    private static final List<Command> commands = new ArrayList<>();

    /**
     * Explicit private constructor
     */
    private Console() {}

    /**
     * Add a {@link Command} to the list of commands, queue it to the list
     * @param command to add in the list
     */
    public static void addRequest(Command command) {
        commands.add(command);
    }

    /**
     * Execute the commands one by one and dequeue
     */
    public static void executeCommands() {
        commands.forEach(Command::execute);
        commands.clear();
    }
}
