package bg.sofia.uni.fmi.mjt.todolist.command;

import bg.sofia.uni.fmi.mjt.todolist.exceptions.InvalidCommandSyntaxException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.NoSuchArgumentException;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CommandParser {
    private static final String COMMAND_SPLIT_REGEX = "\\s+";
    private static final String ARGUMENT_SPLIT_REGEX = "=";
    private static final String ARGUMENTS_SPLIT_REGEX = ",";
    private static final int FIRST_ARGUMENT = 0;
    private static final int SECOND_ARGUMENT = 1;
    private static final int LIMIT_TO_SPLIT = 2;
    private static final int MIN_INITIAL_ARGS_COUNT = 2;

    public static Command parseCommand(String username, String clientRequest) throws InvalidCommandSyntaxException {

        if (clientRequest == null || clientRequest.isEmpty() || clientRequest.isBlank()) {
            throw new InvalidCommandSyntaxException("Client request has invalid syntax");
        }

        String[] requestMainArgs = clientRequest.split(COMMAND_SPLIT_REGEX, LIMIT_TO_SPLIT);
        String[] requestSecondaryArgs = requestMainArgs.length < MIN_INITIAL_ARGS_COUNT ?
                null : requestMainArgs[SECOND_ARGUMENT].split(ARGUMENTS_SPLIT_REGEX);

        requestMainArgs = trimAll(requestMainArgs);

        if (requestSecondaryArgs != null) {

            requestSecondaryArgs = trimAll(requestSecondaryArgs);

            for (String argumentPair : requestSecondaryArgs) {

                if (!argumentPair.contains(ARGUMENT_SPLIT_REGEX)) {
                    throw new InvalidCommandSyntaxException(String
                            .format("Command has incorrect format: %s", argumentPair));
                }
            }

            requestSecondaryArgs = Arrays.stream(requestSecondaryArgs)
                    .map(argPair ->
                            Arrays.stream(argPair.split(ARGUMENT_SPLIT_REGEX))
                                    .map(String::trim)
                                    .collect(Collectors.joining(ARGUMENT_SPLIT_REGEX)))
                    .toArray(String[]::new);
        }

        return new Command(username,
                CommandType.getCommandTypeFromString(requestMainArgs[FIRST_ARGUMENT]), requestSecondaryArgs);
    }

    public static String parseStringValue(String[] arguments, String argumentName, boolean isOptional)
            throws NoSuchArgumentException {

        if (arguments == null) {
            if (!isOptional) {
                throw new NoSuchArgumentException(String
                        .format(NoSuchArgumentException.DEFAULT_MESSAGE_FORMAT, argumentName));
            }

            return null;
        }

        for (String argument : arguments) {
            String[] splitArgument = argument.split(ARGUMENT_SPLIT_REGEX);

            if (splitArgument[FIRST_ARGUMENT].equals(argumentName)) {
                return splitArgument[SECOND_ARGUMENT];
            }
        }

        if (!isOptional) {
            throw new NoSuchArgumentException(String
                    .format(NoSuchArgumentException.DEFAULT_MESSAGE_FORMAT, argumentName));
        }

        return null;
    }

    private static String[] trimAll(String[] toTrim) {
        return Arrays
                .stream(toTrim)
                .map(String::trim)
                .toArray(String[]::new);
    }
}
