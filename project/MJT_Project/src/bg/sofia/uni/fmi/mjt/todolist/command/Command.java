package bg.sofia.uni.fmi.mjt.todolist.command;

public record Command(String username, CommandType commandType, String[] arguments) {
}
