package bg.sofia.uni.fmi.mjt.todolist.collaboration;

import bg.sofia.uni.fmi.mjt.todolist.exceptions.NoSuchTaskException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.NoSuchUserException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.UserAlreadyAddedException;
import bg.sofia.uni.fmi.mjt.todolist.task.Task;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Collaboration {
    private final String collaborationName;
    private final String ownerUsername;
    private final List<String> participants;
    private final List<Task> tasks;
    private final Map<String, String> taskToAssigneeMap;
    private static final Gson parser = new Gson();

    public Collaboration(String collaborationName, String ownerUsername) {
        this.collaborationName = collaborationName;
        this.ownerUsername = ownerUsername;

        this.participants = new ArrayList<>();
        this.tasks = new ArrayList<>();
        this.taskToAssigneeMap = new HashMap<>();
    }

    public String convertToJson() {
        return parser.toJson(this, this.getClass());
    }

    public static Collaboration of(String collaborationJsonFormat) {
        return parser.fromJson(collaborationJsonFormat, Collaboration.class);
    }

    public void addUser(String username) throws UserAlreadyAddedException {
        for (String participant : participants) {
            if (participant.equals(username)) {
                throw new UserAlreadyAddedException("User <username> is already added to collab. <name>");
            }
        }

        if (username.equals(ownerUsername)) {
            throw new UserAlreadyAddedException("User <username> is already added to collab. <name>");
        }

        participants.add(username);
    }

    public Task getTaskByParameters(String name, String date) throws NoSuchTaskException {
        for (Task key : tasks) {
            if (key.getName().equals(name) && key.getDate().equals(date)) {
                return key;
            }
        }

        throw new NoSuchTaskException("Collaboration <name> does not have task named <name> at date <date>");
    }

    public void assignTaskToUser(Task task, String assigneeUsername) throws NoSuchUserException {
        if (!participants.contains(assigneeUsername) && !assigneeUsername.equals(ownerUsername)) {
            throw new NoSuchUserException("Collaboration <name> does not have <username> as a participant");
        }

        taskToAssigneeMap.put(task.convertToJson(), assigneeUsername);
    }

    public void addTask(Task toAdd) {
        tasks.add(toAdd);
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public List<String> getParticipants() {
        return participants;
    }
}
