package bg.sofia.uni.fmi.mjt.todolist.argument;

public enum ArgumentCount {

    REGISTER_ARG_COUNT(2),
    LOGIN_ARG_COUNT(2),
    ADD_TASK_MIN_ARG_COUNT(1),
    UPDATE_TASK_MIN_ARG_COUNT(1),
    DELETE_TASK_MIN_ARG_COUNT(1),
    GET_TASK_MIN_ARG_COUNT(1),
    FINISH_TASK_MIN_ARG_COUNT(1),
    LABEL_TASK_MIN_ARG_COUNT(2),
    LIST_LABELS_MAX_ARG_COUNT(0),
    ADD_COLLABORATION_ARG_COUNT(1),
    DELETE_COLLABORATION_ARG_COUNT(1),
    LIST_COLLABORATIONS_ARG_COUNT(0),
    ADD_USER_TO_COLLABORATION(2),
    ADD_COLLABORATION_TASK_MIN_ARG_COUNT(2),
    ASSIGN_TASK_MIN_ARG_COUNT(3),
    LIST_COLLABORATION_TASKS_ARG_COUNT(2);

    private final int value;

    private ArgumentCount(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
