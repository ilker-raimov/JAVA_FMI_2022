package bg.sofia.uni.fmi.mjt.todolist.response;

public enum ResponseStatus {
    OK("ok"),
    ERROR("error");

    private final String label;

    private ResponseStatus(String label) {
        this.label = label;
    }
}
