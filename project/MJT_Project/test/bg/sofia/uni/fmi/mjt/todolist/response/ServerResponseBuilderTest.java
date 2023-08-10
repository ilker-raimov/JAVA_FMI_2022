package bg.sofia.uni.fmi.mjt.todolist.response;

import bg.sofia.uni.fmi.mjt.todolist.command.CommandType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServerResponseBuilderTest {
    private static final String ERROR_MESSAGE1 = "Constructing and parsing server response should give the same result" +
            "and work properly";

    @Test
    void testBuilderParsing() {
        String JsonToParse = "{\"responseStatus\":\"OK\",\"commandType\":\"LOGIN\"," +
                "\"clientUsername\":\"user1\",\"message\":\"message\"}";

        ServerResponse built = new ServerResponseBuilder()
                .setResponseStatus(ResponseStatus.OK)
                .setMessage("message")
                .setClientUsername("user1")
                .setCommandType(CommandType.LOGIN)
                .build();

        ServerResponse toCheck = ServerResponse.convertFromJson(JsonToParse);

        assertEquals(toCheck.getResponseStatus(), built.getResponseStatus(), ERROR_MESSAGE1);
        assertEquals(toCheck.getCommandType().getLabel(), built.getCommandType().getLabel(), ERROR_MESSAGE1);
        assertEquals(toCheck.getClientUsername(), built.getClientUsername(), ERROR_MESSAGE1);
        assertEquals(toCheck.getMessage(), built.getMessage(), ERROR_MESSAGE1);
        assertEquals(built.convertToJson(), JsonToParse);
    }
}
