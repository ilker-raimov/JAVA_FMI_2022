package bg.sofia.uni.fmi.mjt.todolist.command;

public enum CommandType {
    LOGIN("login"),
    REGISTER("register"),
    ADD_TASK("add-task"),
    UPDATE_TASK("update-task"),
    DELETE_TASK("delete-task"),
    GET_TASK("get-task"),
    FINISH_TASK("finish-task"),
    LIST_TASKS("list-tasks"),
    ADD_LABEL("add-label"),
    DELETE_LABEL("delete-label"),
    LIST_LABELS("list-labels"),
    LABEL_TASK("label-task"),
    ADD_COLLABORATION("add-collaboration"),
    DELETE_COLLABORATION("delete-collaboration"),
    LIST_COLLABORATIONS("list-collaborations"),
    ADD_USER("add-user"),
    ADD_COLLABORATION_TASK("add-collaboration-task"),
    ASSIGN_TASK("assign-task"),
    LIST_COLLABORATION_TASKS("list-collaboration-tasks"),
    LIST_USERS("list-users");

    private final String label;

    CommandType(String label) {
        this.label = label;
    }

    public static CommandType getCommandTypeFromString(String labelToFind) {
        for (CommandType commandType : CommandType.values()) {
            if (commandType.label.equals(labelToFind)) {
                return commandType;
            }
        }

        return null;
    }

    public String getLabel() { //not needed ?
        return label;
    }
}
