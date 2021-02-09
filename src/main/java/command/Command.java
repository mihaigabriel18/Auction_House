package command;

/**
 * Main part of the command design pattern implemented inside this package ({@link command})
 * It only includes an empty method of execution that is going to be specifically implemented in each
 * command class, set to execute different types of actions.
 * <p>
 *     Each specific command will initialize it's command parameters in the constructor
 *     as strings and convert them to their desired type.
 *     Every command argument will be declared as {@code private final} and the execute command
 *     will catch the exceptions thrown by calling the specific functions needed to get the intended behaviour
 *     out of the method.
 * </p>
 */

public interface Command {

    /**
     * Abstract method of execution, overridden by all specific commands that implement
     * this interface. Method will do the action the class tells it to do,
     * see documentation above.
     */
    void execute();
}
