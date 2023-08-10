package bg.sofia.uni.fmi.mjt.todolist.task;

public class TaskBuilder {
    private final String name;
    private String date;
    private String dueToDate;
    private String description;

    public TaskBuilder(String name) {
        this.name = name;
    }

    public static TaskBuilder of(Task taskBase) {
        if (taskBase == null) {
            return null;
        }

        return new TaskBuilder(taskBase.getName())
                .setDate(taskBase.getDate())
                .setDueToDate(taskBase.getDueToDate())
                .setDescription(taskBase.getDescription());
    }

    public TaskBuilder setDate(String date) {
        this.date = date;

        return this;
    }

    public TaskBuilder setDueToDate(String dueToDate) {
        this.dueToDate = dueToDate;

        return this;
    }

    public TaskBuilder setDescription(String description) {
        this.description = description;

        return this;
    }

    public Task build() {
        return Task.of(this);
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
