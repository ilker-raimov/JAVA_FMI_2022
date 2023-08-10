package bg.sofia.uni.fmi.mjt.todolist.task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TaskBuilderTest {
    private static final String ERROR_MESSAGE1 = "Task builder and parsing task from json format should work properly" +
            "and give the same result";
    private static final String TASK_JSON_FORMAT = "{\"name\":\"name\",\"date\":\"date1\",\"dueToDate\":\"date2\"," +
            "\"description\":\"cool description\"}";

    @Test
    void testBuilder() {
        Task taskBuilt = new TaskBuilder("name")
                .setDate("date1")
                .setDueToDate("date2")
                .setDescription("cool description")
                .build();

        Task taskFromJson = Task.of(TASK_JSON_FORMAT);

        assertEquals(taskBuilt.getName(), taskFromJson.getName(), ERROR_MESSAGE1);
        assertEquals(taskBuilt.getDate(), taskFromJson.getDate(), ERROR_MESSAGE1);
        assertEquals(taskBuilt.getDueToDate(), taskFromJson.getDueToDate(), ERROR_MESSAGE1);
        assertEquals(taskBuilt.getDescription(), taskFromJson.getDescription(), ERROR_MESSAGE1);
        assertEquals(taskBuilt.convertToJson(), TASK_JSON_FORMAT, ERROR_MESSAGE1);
    }
}
