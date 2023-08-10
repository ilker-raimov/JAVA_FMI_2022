package bg.sofia.uni.fmi.mjt.todolist.task;

import com.google.gson.Gson;

public class Task {
    private final String name;
    private final String date;
    private final String dueToDate;
    private final String description;
    private static final Gson parser = new Gson();

    private Task(TaskBuilder taskBuilder) {
        this.name = taskBuilder.getName();
        this.date = taskBuilder.getDate();
        this.dueToDate = taskBuilder.getDueToDate();
        this.description = taskBuilder.getDescription();
    }

    public static Task of(TaskBuilder taskBuilder) {
        return new Task(taskBuilder);
    }

    public static Task of(String dataString) {
        return parser.fromJson(dataString, Task.class);
    }

    public String convertToJson() {
        return parser.toJson(this);
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getDueToDate() {
        return dueToDate;
    }

    public String getDescription() {
        return description;
    }
}
