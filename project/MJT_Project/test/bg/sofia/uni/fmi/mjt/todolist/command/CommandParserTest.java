package bg.sofia.uni.fmi.mjt.todolist.command;

import bg.sofia.uni.fmi.mjt.todolist.argument.IsOptional;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.InvalidCommandSyntaxException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.NoSuchArgumentException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class CommandParserTest {
    private static final String USERNAME = "user1";
    private static final String[] secondaryArguments = new String[]{"username=ivan", "password=12344321"};
    private static final String ERROR_MESSAGE1 = "Parsing command of null or blank client request" +
            "should throw exception";
    private static final String ERROR_MESSAGE2 = "Parsing command should work properly";
    private static final String ERROR_MESSAGE3 = "Parsing optional argument should work properly";
    private static final String ERROR_MESSAGE4 = "Parsing non-optional argument should work properly and" +
            "throw exception in case of missing argument";

    @Test
    void testParseCommandNullAndBlankCommand() {
        final String nullString = null;
        final String blankString = "      ";

        assertThrows(InvalidCommandSyntaxException.class, () -> CommandParser.parseCommand(USERNAME, nullString),
                ERROR_MESSAGE1);
        assertThrows(InvalidCommandSyntaxException.class, () -> CommandParser.parseCommand(USERNAME, blankString),
                ERROR_MESSAGE1);
    }

    @Test
    void testParseCommandLegalCommand() {
        final String commandToParse = "login username=ivan, password=12344321";
        Command parsedCommand;

        try {
            parsedCommand = CommandParser.parseCommand(USERNAME, commandToParse);
        } catch (InvalidCommandSyntaxException e) {
            throw new RuntimeException(e);
        }

        assertEquals(parsedCommand.username(), USERNAME, ERROR_MESSAGE2);
        assertEquals(parsedCommand.commandType(), CommandType.LOGIN, ERROR_MESSAGE2);
        assertEquals(parsedCommand.arguments()[0], secondaryArguments[0], ERROR_MESSAGE2);
        assertEquals(parsedCommand.arguments()[1], secondaryArguments[1], ERROR_MESSAGE2);
    }

    @Test
    void testParseStringValueOptional() {
        String value1, value2;

        try {
            value1 = CommandParser.parseStringValue(secondaryArguments, "username",
                    IsOptional.OPTIONAL.getOptionality());
            value2 = CommandParser.parseStringValue(secondaryArguments, "notExistingArg",
                    IsOptional.OPTIONAL.getOptionality());
        } catch (NoSuchArgumentException e) {
            throw new RuntimeException(e);
        }

        assertEquals("ivan", value1, ERROR_MESSAGE3);
        assertEquals(null, value2, ERROR_MESSAGE3);
    }

    @Test
    void testParseStringValueNotOptional() {
        String value1;

        try {
            value1 = CommandParser.parseStringValue(secondaryArguments, "username",
                    IsOptional.NOT_OPTIONAL.getOptionality());
        } catch (NoSuchArgumentException e) {
            throw new RuntimeException(e);
        }

        assertEquals("ivan", value1, ERROR_MESSAGE4);
        assertThrows(NoSuchArgumentException.class, () -> CommandParser.parseStringValue(secondaryArguments,
                "nonExistingArg", IsOptional.NOT_OPTIONAL.getOptionality()), ERROR_MESSAGE4);
    }

}
