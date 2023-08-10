package bg.sofia.uni.fmi.mjt.todolist.argument;

public enum IsOptional {
    OPTIONAL(true),
    NOT_OPTIONAL(false);

    private boolean isOptional;

    private IsOptional(boolean isOptional) {
        this.isOptional = isOptional;
    }

    public boolean getOptionality() {
        return isOptional;
    }
}
