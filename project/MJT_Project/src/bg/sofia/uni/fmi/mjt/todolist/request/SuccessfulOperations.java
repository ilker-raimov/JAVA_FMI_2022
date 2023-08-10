package bg.sofia.uni.fmi.mjt.todolist.request;

public enum SuccessfulOperations {


    SUCCESSFUL_REGISTER("Successfully registered user: %s"),
    SUCCESSFUL_LOGIN("Successfully logged in user: %s"),
    SUCCESSFUL_ADD_TASK("Successfully added task %s"),
    SUCCESSFUL_UPDATE_TASK("Successfully updated task %s"),
    SUCCESSFUL_DELETE_TASK("Successfully deleted task %s"),
    SUCCESSFUL_FINISH_TASK("Successfully finished task %s"),
    SUCCESSFUL_ADD_LABEL("Successfully added label %s"),
    SUCCESSFUL_DELETE_LABEL("Successfully deleted label %s"),
    SUCCESSFUL_LABEL_TASK("Successfully labeled task %1$s with label %2$s"),
    SUCCESSFUL_ADD_COLLABORATION("Successfully added collaboration %s"),
    SUCCESSFUL_DELETE_COLLABORATION("Successfully deleted collaboration %s"),
    SUCCESSFUL_ADD_USER_TO_COLLABORATION("Successfully added user %1$s to collaboration %2$s owned by %3$s"),
    SUCCESSFUL_ADD_COLLABORATION_TASK("Successfully added task %1$s to collaboration %2$s"),
    SUCCESSFUL_ASSIGN_TASK1("Successfully assigned task %1$s from collaboration %2$s to user %3$s"),
    SUCCESSFUL_ASSIGN_TASK2("Successfully assigned task %1$s at date %2$s from collaboration %3$s to user %4$s");


    private final String message;

    private SuccessfulOperations(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
