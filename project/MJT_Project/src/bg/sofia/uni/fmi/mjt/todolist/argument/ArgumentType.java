package bg.sofia.uni.fmi.mjt.todolist.argument;

public enum ArgumentType {
    USERNAME_ARG("username"),
    PASSWORD_ARG("password"),
    NAME_ARG("name"),
    DATE_ARG("date"),
    DUE_TO_DATE("due-to-date"),
    DESCRIPTION_ARG("description"),
    LABEL_ARG("label"),
    COMPLETED_ARG("completed"),
    COLLABORATION_ARG("collaboration"),
    ASSIGNEE_ARG("assignee"),
    OWNER_ARG("owner");

    private String label;

    private ArgumentType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
