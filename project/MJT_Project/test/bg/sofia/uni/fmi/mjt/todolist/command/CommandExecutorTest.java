package bg.sofia.uni.fmi.mjt.todolist.command;

import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommandExecutorTest {
    private static final String ERROR_MESSAGE1 = "Trying to format null date should return null";
    private static final String DATE1 = "12.12.2024";

    @Test
    void testDateFormatterNullArguments() {
        assertEquals(null, CommandExecutor.dateFormatter(null), ERROR_MESSAGE1);
    }

    @Test
    void testDateFormatterValidArguments() {
        Date toCheck = new GregorianCalendar(2024, 11, 12).getTime();
        SimpleDateFormat DateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String actualDate = DateFormat.format(toCheck);
        assertEquals(actualDate, CommandExecutor.dateFormatter(DATE1), ERROR_MESSAGE1);
    }
}
