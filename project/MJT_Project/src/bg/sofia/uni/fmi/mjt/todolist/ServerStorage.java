package bg.sofia.uni.fmi.mjt.todolist;

import bg.sofia.uni.fmi.mjt.todolist.argument.ArgumentType;
import bg.sofia.uni.fmi.mjt.todolist.collaboration.Collaboration;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.CollaborationAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.CorruptedDataException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.IOUnsuccessfulOperationException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.IllegalTaskNameException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.InvalidPasswordException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.InvalidUsernameException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.LabelAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.NoSuchArgumentException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.NoSuchCollaborationExistsException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.NoSuchLabelException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.NoSuchTaskException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.NoSuchUserException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.TaskAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.UserAlreadyAddedException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.UsernameAlreadyTakenException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.WrongPasswordException;
import bg.sofia.uni.fmi.mjt.todolist.path.PathBuilder;
import bg.sofia.uni.fmi.mjt.todolist.request.SuccessfulOperations;
import bg.sofia.uni.fmi.mjt.todolist.task.Task;
import bg.sofia.uni.fmi.mjt.todolist.task.TaskBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ServerStorage {
    private Map<String, String> users;
    private static final int FIRST_ARGUMENT = 0;
    private static final int SECOND_ARGUMENT = 1;
    private static final String WHITESPACE = " ";
    private static final String DOT = ".";
    //private static final String EQUALS = "=";
    private static final String SEPARATOR = FileSystems.getDefault().getSeparator();
    private static final String CREDENTIALS_PATH = "data";
    private static final String INBOX_PATH = "inbox";
    private static final String COLLABORATIONS_PATH = "collaborations";
    private static final String OWNER_PATH = "owner";
    private static final String CREDENTIALS_FILE = "users.txt";
    private static final String LABELS_FILE = "labels";
    private static final String PARTICIPANT_PATH = "participant";
    private static final Path PATH_TO_FILE = Paths.get(CREDENTIALS_PATH);
    private static final Path FULL_PATH = Paths.get(CREDENTIALS_PATH + SEPARATOR + CREDENTIALS_FILE);
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final String NO_TASKS_FOUND = "No tasks found with the current filters: <filter1>, <filter2>";
    private static final String NO_LABELS_FOUND = "No labels found for user <username>"; //format

    public ServerStorage() {
        this.users = new HashMap<>();

        loadRegisteredUsers();
    }

    public String registerUser(String username, String password)
            throws UsernameAlreadyTakenException, InvalidUsernameException, InvalidPasswordException {

        if (isUsernameInvalid(username)) {
            throw new InvalidUsernameException();
        }

        if (isPasswordInvalid(password)) {
            throw new InvalidPasswordException();
        }

        if (users.containsKey(username)) {
            throw new UsernameAlreadyTakenException("Username is already taken");
        }

        saveNewlyRegisteredUser(username, password);

        return String.format(SuccessfulOperations.SUCCESSFUL_REGISTER.getMessage(), username);
    }

    public String loginUser(String username, String password)
            throws InvalidUsernameException, InvalidPasswordException, NoSuchUserException, WrongPasswordException {

        if (isUsernameInvalid(username)) {
            throw new InvalidUsernameException();
        }

        if (isPasswordInvalid(password)) {
            throw new InvalidPasswordException();
        }

        if (!users.containsKey(username)) {
            throw new NoSuchUserException("User with this username does not exist");
        }

        if (!users.get(username).equals(password)) {
            throw new WrongPasswordException("Password does not match");
        }

        return String.format(SuccessfulOperations.SUCCESSFUL_LOGIN.getMessage(), username);
    }

    public String addTask(String username, String taskName, String date, String dueToDate, String description)
            throws IllegalArgumentException, TaskAlreadyExistsException, IOUnsuccessfulOperationException {

        PathBuilder pathBuilder = new PathBuilder().addDirectory(username).addDirectory(date, INBOX_PATH);
        Path directoryPath = pathBuilder.buildDirectoryPath();
        Path fullPath = pathBuilder.addDirectory(taskName).buildFilePath();

        if (checkStateOfStorage(directoryPath, fullPath)) {
            throw new TaskAlreadyExistsException("Task with the same name at the same date exists");
        }

        String taskData = new TaskBuilder(taskName)
                .setDate(date)
                .setDueToDate(dueToDate)
                .setDescription(description)
                .build()
                .convertToJson();

        writeToFile(fullPath, taskData, StandardOpenOption.WRITE);

        return String.format(SuccessfulOperations.SUCCESSFUL_ADD_TASK.getMessage(), taskName);
    }

    public String updateTask(String username, String taskName, String date,
                             String dueToDate, String description, String completed)
            throws IllegalArgumentException, NoSuchTaskException,
            IOUnsuccessfulOperationException, NoSuchArgumentException {

        String taskJsonFormat;
        Path fullPath = new PathBuilder()
                .addDirectory(username)
                .addDirectory(date, INBOX_PATH)
                .addDirectory(taskName)
                .addDirectory(completed)
                .buildFilePath();

        if (!doesFileExist(fullPath)) {
            String message = date == null ?
                    String.format(NoSuchTaskException.DEFAULT_MESSAGE_FORMAT1, taskName) :
                    String.format(NoSuchTaskException.DEFAULT_MESSAGE_FORMAT2, taskName, date);

            throw new NoSuchTaskException(message);
        }

        try (BufferedReader bufferedReader = Files.newBufferedReader(fullPath)) {
            taskJsonFormat = bufferedReader.readLine();
        } catch (IOException e) {
            throw new IOUnsuccessfulOperationException("Failed reading task named - <name> at date - <date>"); //format
        }

        //empty .txt file handling
        TaskBuilder taskBuilder = TaskBuilder.of(Task.of(taskJsonFormat));

        if (taskBuilder == null) {
            taskBuilder = new TaskBuilder(taskName).setDate(date);
        }

        taskBuilder.setDueToDate(dueToDate);
        taskBuilder.setDescription(description);

        String updatedTaskData = taskBuilder.build().convertToJson();

        writeToFile(fullPath, updatedTaskData, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);

        return String.format(SuccessfulOperations.SUCCESSFUL_UPDATE_TASK.getMessage(), taskName);
    }

    public String deleteTask(String username, String taskName, String date, String completed)
            throws IllegalArgumentException, TaskAlreadyExistsException,
            NoSuchTaskException, IOUnsuccessfulOperationException {

        Path fullPath = new PathBuilder()
                .addDirectory(username)
                .addDirectory(date, INBOX_PATH)
                .addDirectory(completed)
                .addDirectory(taskName)
                .buildFilePath();

        if (!doesFileExist(fullPath)) {
            String message = date == null ?
                    String.format(NoSuchTaskException.DEFAULT_MESSAGE_FORMAT1, taskName) :
                    String.format(NoSuchTaskException.DEFAULT_MESSAGE_FORMAT2, taskName, date);

            throw new NoSuchTaskException(message);
        }

        deleteFile(fullPath);

        return String.format(SuccessfulOperations.SUCCESSFUL_DELETE_TASK.getMessage(), taskName);
    }

    public String getTask(String username, String taskName, String date, String completed)
            throws IOUnsuccessfulOperationException, NoSuchTaskException {

        String taskJsonFormat;
        Path fullPath = new PathBuilder()
                .addDirectory(username)
                .addDirectory(date, INBOX_PATH)
                .addDirectory(completed)
                .addDirectory(taskName)
                .buildFilePath();

        if (!doesFileExist(fullPath)) {
            String message = date == null ?
                    String.format(NoSuchTaskException.DEFAULT_MESSAGE_FORMAT1, taskName) :
                    String.format(NoSuchTaskException.DEFAULT_MESSAGE_FORMAT2, taskName, date);

            throw new NoSuchTaskException(message);
        }

        try (BufferedReader bufferedReader = Files.newBufferedReader(fullPath)) {
            taskJsonFormat = bufferedReader.readLine();
        } catch (IOException e) {
            throw new IOUnsuccessfulOperationException("Failed reading task named - <name> at date - <date>"); //format
        }

        return taskJsonFormat;
    }

    public String finishTask(String username, String taskName, String date)
            throws NoSuchTaskException, IOUnsuccessfulOperationException {

        PathBuilder pathBuilder = new PathBuilder().addDirectory(username).addDirectory(date, INBOX_PATH);
        PathBuilder newPathPathBuilder = pathBuilder.copy();

        Path fullPath = pathBuilder.addDirectory(taskName).buildFilePath();
        Path newDirectoryPath = newPathPathBuilder
                .addDirectory(ArgumentType.COMPLETED_ARG.getLabel())
                .buildDirectoryPath();
        Path newFullPath = newPathPathBuilder.addDirectory(taskName).buildFilePath();

        if (!doesFileExist(fullPath)) {
            String message = date == null ?
                    String.format(NoSuchTaskException.DEFAULT_MESSAGE_FORMAT1, taskName) :
                    String.format(NoSuchTaskException.DEFAULT_MESSAGE_FORMAT2, taskName, date);

            throw new NoSuchTaskException(message);
        }

        refactorFile(fullPath, newDirectoryPath, newFullPath);

        return String.format(SuccessfulOperations.SUCCESSFUL_FINISH_TASK.getMessage(), taskName);
    }

    public String listTasks(String username, String date, String completed, String label) {

        String completedArg = completed == null ?
                null : completed.equals("true") ?
                ArgumentType.COMPLETED_ARG.getLabel() : null;

        Path fullPath = new PathBuilder()
                .addDirectory(username)
                .addDirectory(date, INBOX_PATH)
                .addDirectory(completedArg)
                .addDirectory(label)
                .buildDirectoryPath();
        File[] filesInDirectory = new File(fullPath.toString()).listFiles();

        if (filesInDirectory == null) {
            return NO_TASKS_FOUND; //format
        }

        return Stream.of(filesInDirectory)
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .map(filename -> filename.substring(FIRST_ARGUMENT, filename.lastIndexOf(DOT)))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    public String addLabel(String username, String labelName)
            throws IOUnsuccessfulOperationException, LabelAlreadyExistsException {

        PathBuilder pathBuilder = new PathBuilder().addDirectory(username);
        Path directoryPath = pathBuilder.buildDirectoryPath();
        Path fullPath = pathBuilder.addDirectory(LABELS_FILE).buildFilePath();

        checkStateOfStorage(directoryPath, fullPath);

        if (doesFileContainString(fullPath, labelName)) {
            throw new LabelAlreadyExistsException(String
                    .format(LabelAlreadyExistsException.DEFAULT_MESSAGE_FORMAT, labelName, username));
        }

        writeToFile(fullPath, labelName, StandardOpenOption.APPEND);

        return String.format(SuccessfulOperations.SUCCESSFUL_ADD_LABEL.getMessage(), labelName);
    }

    public String deleteLabel(String username, String labelName)
            throws IOUnsuccessfulOperationException, NoSuchLabelException {
        //daemon thread for removing deleted labels from task that have it

        PathBuilder pathBuilder = new PathBuilder().addDirectory(username);
        Path directoryPath = pathBuilder.buildDirectoryPath();
        Path fullPath = pathBuilder.addDirectory(LABELS_FILE).buildFilePath();

        checkStateOfStorage(directoryPath, fullPath);

        if (!doesFileContainString(fullPath, labelName)) {
            throw new NoSuchLabelException(String
                    .format(NoSuchLabelException.DEFAULT_MESSAGE_FORMAT, username, labelName));
        }

        removeStringFromFile(fullPath, labelName);

        return String.format(SuccessfulOperations.SUCCESSFUL_DELETE_LABEL.getMessage(), labelName);
    }

    public String listLabels(String username)
            throws IOUnsuccessfulOperationException, LabelAlreadyExistsException {

        PathBuilder pathBuilder = new PathBuilder().addDirectory(username);
        Path directoryPath = pathBuilder.buildDirectoryPath();
        Path fullPath = pathBuilder.addDirectory(LABELS_FILE).buildFilePath();

        checkStateOfStorage(directoryPath, fullPath);

        String[] fileContents;

        try {
            fileContents = Files.lines(fullPath).toArray(String[]::new);
        } catch (IOException e) {
            throw new IOUnsuccessfulOperationException(String
                    .format(IOUnsuccessfulOperationException.DEFAULT_MESSAGE_FORMAT, e.getMessage()), e);
        }

        if (fileContents.length == 0) {
            return NO_LABELS_FOUND;
        }

        return Arrays.stream(fileContents).collect(Collectors.joining(System.lineSeparator()));
    }

    public String labelTask(String username, String taskName, String date, String completed, String labelName)
            throws IOUnsuccessfulOperationException, NoSuchTaskException {

        PathBuilder pathBuilder = new PathBuilder()
                .addDirectory(username)
                .addDirectory(date, INBOX_PATH)
                .addDirectory(completed);
        PathBuilder newPathBuilder = pathBuilder.copy();
        Path fullPath = pathBuilder.addDirectory(taskName).buildFilePath();
        Path newDirectoryPath = newPathBuilder.addDirectory(labelName).buildDirectoryPath();
        Path newFullPath = newPathBuilder.addDirectory(taskName).buildFilePath();

        if (!doesFileExist(fullPath)) {
            String message = date == null ?
                    String.format(NoSuchTaskException.DEFAULT_MESSAGE_FORMAT1, taskName) :
                    String.format(NoSuchTaskException.DEFAULT_MESSAGE_FORMAT2, taskName, date);

            throw new NoSuchTaskException(message);
        }

        refactorFile(fullPath, newDirectoryPath, newFullPath);

        return String.format(SuccessfulOperations.SUCCESSFUL_LABEL_TASK.getMessage(), taskName, labelName);
    }

    public String addCollaboration(String username, String collaborationName)
            throws CollaborationAlreadyExistsException, IOUnsuccessfulOperationException {

        PathBuilder pathBuilder = new PathBuilder()
                .addDirectory(username)
                .addDirectory(COLLABORATIONS_PATH)
                .addDirectory(OWNER_PATH)
                .addDirectory(collaborationName);
        Path directoryPath = pathBuilder.buildDirectoryPath();
        Path fullPath = pathBuilder.addDirectory(collaborationName).buildFilePath();

        if (checkStateOfStorage(directoryPath, fullPath)) {
            throw new CollaborationAlreadyExistsException(String
                    .format(CollaborationAlreadyExistsException.DEFAULT_MESSAGE_FORMAT, collaborationName, username));
        }

        Collaboration toSave = new Collaboration(collaborationName, username);

        writeToFile(fullPath, toSave.convertToJson(), StandardOpenOption.WRITE);

        return String.format(SuccessfulOperations.SUCCESSFUL_ADD_COLLABORATION.getMessage(), collaborationName);
    }

    public String deleteCollaboration(String username, String collaborationName)
            throws NoSuchCollaborationExistsException, IOUnsuccessfulOperationException, CorruptedDataException {

        PathBuilder pathBuilder = new PathBuilder()
                .addDirectoriesSequentially(username, COLLABORATIONS_PATH, OWNER_PATH, collaborationName);

        Path directoryPath = pathBuilder.buildDirectoryPath();
        Path collaborationPath = pathBuilder.addDirectory(collaborationName).buildFilePath();

        if (!doesFileExist(collaborationPath)) {
            throw new NoSuchCollaborationExistsException(String
                    .format(NoSuchCollaborationExistsException.DEFAULT_MESSAGE_FORMAT, collaborationName, username));
        }

        Collaboration toDelete = Collaboration.of(getFileContents(collaborationPath));

        deleteDirectoryAndContents(directoryPath);

        if (toDelete == null) {
            throw new CorruptedDataException("Wrong collaboration format," +
                    " unable to handle deleting collaboration named <name> owned by <user> properly");
        }

        List<String> participants = toDelete.getParticipants();

        if (participants != null) {
            for (String participant : participants) {
                removeUserFromCollab(participant, collaborationName);
            }
        }

        return String.format(SuccessfulOperations.SUCCESSFUL_DELETE_COLLABORATION.getMessage(), collaborationName);
    }

    public String listCollaborations(String username) throws IOUnsuccessfulOperationException {

        PathBuilder pathBuilder = new PathBuilder().addDirectoriesSequentially(username, COLLABORATIONS_PATH);
        PathBuilder pathBuilderCopy = pathBuilder.copy();
        Path ownedCollabPath = pathBuilder
                .addDirectory(OWNER_PATH)
                .buildDirectoryPath();
        Path participantCollabDirectoryPath = pathBuilderCopy.buildDirectoryPath();
        Path participantCollabFilePath = pathBuilderCopy
                .addDirectory(PARTICIPANT_PATH)
                .buildFilePath();

        checkStateOfStorage(participantCollabDirectoryPath, participantCollabFilePath);

        File[] filesInDirectory = new File(ownedCollabPath.toString()).listFiles();

        Stream<String> participantCollaborations;
        Stream<String> ownedCollaborations = null;

        try {
            participantCollaborations = Files
                    .lines(participantCollabFilePath)
                    .map(line -> line.split(WHITESPACE)[SECOND_ARGUMENT]);
        } catch (IOException e) {
            throw new IOUnsuccessfulOperationException(String
                    .format(IOUnsuccessfulOperationException.DEFAULT_MESSAGE_FORMAT, e.getMessage()), e);
        }

        if (filesInDirectory != null) {
            ownedCollaborations = Stream
                    .of(filesInDirectory)
                    .filter(File::isDirectory)
                    .map(File::getName);
        }

        if (ownedCollaborations != null) {
            return Stream
                    .concat(participantCollaborations, ownedCollaborations)
                    .collect(Collectors.joining(System.lineSeparator()));
        }

        return participantCollaborations
                .collect(Collectors.joining(System.lineSeparator()));
    }

    public String addUserToCollaboration(String username, String collaborationName, String toAddUsername)
            throws NoSuchCollaborationExistsException, IOUnsuccessfulOperationException, UserAlreadyAddedException {

        Path filePath = new PathBuilder()
                .addDirectoriesSequentially(username, COLLABORATIONS_PATH,
                        OWNER_PATH, collaborationName, collaborationName)
                .buildFilePath();


        PathBuilder pathBuilder = new PathBuilder().addDirectoriesSequentially(toAddUsername, COLLABORATIONS_PATH);
        Path toAddUserDirectoryPath = pathBuilder.buildDirectoryPath();
        Path toAddUserFilePath = pathBuilder.addDirectory(PARTICIPANT_PATH).buildFilePath();

        checkStateOfStorage(toAddUserDirectoryPath, toAddUserFilePath);

        if (!doesFileExist(filePath)) {
            throw new NoSuchCollaborationExistsException(String
                    .format(NoSuchCollaborationExistsException.DEFAULT_MESSAGE_FORMAT, collaborationName, username));
        }

        String toAppend = collaborationName + WHITESPACE + username + System.lineSeparator();
        Collaboration collaboration = Collaboration.of(getFileContents(filePath));

        collaboration.addUser(toAddUsername);

        writeToFile(toAddUserFilePath, toAppend, StandardOpenOption.APPEND);
        writeToFile(filePath, collaboration.convertToJson(), StandardOpenOption.WRITE);

        return String.format(SuccessfulOperations.SUCCESSFUL_ADD_USER_TO_COLLABORATION.getMessage(),
                toAddUsername, collaborationName, username);
    }

    public String addCollaborationTask(String username, String collaborationName,
                                       String taskName, String date, String dueToDate, String description)
            throws IOUnsuccessfulOperationException, TaskAlreadyExistsException, IllegalTaskNameException {

        if (collaborationName.equals(taskName)) {
            throw new IllegalTaskNameException(String
                    .format(IllegalTaskNameException.DEFAULT_MESSAGE_FORMAT, collaborationName));
        }

        PathBuilder pathBuilder = new PathBuilder()
                .addDirectoriesSequentially(username, COLLABORATIONS_PATH, OWNER_PATH, collaborationName);
        PathBuilder pathToCollabBuilder = pathBuilder.copy();
        Path collaborationPath = pathToCollabBuilder.addDirectory(collaborationName).buildFilePath();
        Path directoryPath = pathBuilder.addDirectory(date).buildDirectoryPath();
        Path filePath = pathBuilder.addDirectory(taskName).buildFilePath();

        if (checkStateOfStorage(directoryPath, filePath)) {
            throw new TaskAlreadyExistsException("Task with the same name at the same date in this collab exists");
        }

        Task toAdd = new TaskBuilder(taskName)
                .setDate(date)
                .setDueToDate(dueToDate)
                .setDescription(description)
                .build();
        String taskData = toAdd.convertToJson();
        Collaboration toUpdate = Collaboration.of(getFileContents(collaborationPath));

        toUpdate.addTask(toAdd);

        writeToFile(collaborationPath, toUpdate.convertToJson(), StandardOpenOption.WRITE);
        writeToFile(filePath, taskData, StandardOpenOption.WRITE);

        return String.format(SuccessfulOperations.SUCCESSFUL_ADD_COLLABORATION_TASK.getMessage(),
                taskName, collaborationName);
    }

    public String assignTask(String username, String collaborationName, String taskName, String date, String assignee)
            throws NoSuchCollaborationExistsException, IOUnsuccessfulOperationException, NoSuchTaskException,
            NoSuchUserException {

        Path collaborationPath = new PathBuilder()
                .addDirectoriesSequentially(username, COLLABORATIONS_PATH, OWNER_PATH)
                .addDirectoriesSequentially(collaborationName, collaborationName)
                .buildFilePath();

        if (!doesFileExist(collaborationPath)) {
            throw new NoSuchCollaborationExistsException(String
                    .format(NoSuchCollaborationExistsException.DEFAULT_MESSAGE_FORMAT, collaborationName, username));
        }

        Collaboration toUpdate = Collaboration.of(getFileContents(collaborationPath));
        Task assigneeTask = toUpdate.getTaskByParameters(taskName, date);

        toUpdate.assignTaskToUser(assigneeTask, assignee);

        writeToFile(collaborationPath, toUpdate.convertToJson(), StandardOpenOption.WRITE);

        return date == null ?
                String.format(SuccessfulOperations.SUCCESSFUL_ASSIGN_TASK1.getMessage(),
                        taskName, collaborationName, assignee) :
                String.format(SuccessfulOperations.SUCCESSFUL_ASSIGN_TASK2.getMessage(),
                        taskName, date, collaborationName, assignee);
    }

    public String listCollaborationTasks(String collaborationName, String ownerUsername)
            throws IOUnsuccessfulOperationException, NoSuchCollaborationExistsException {

        PathBuilder pathBuilder = new PathBuilder()
                .addDirectoriesSequentially(ownerUsername, COLLABORATIONS_PATH, OWNER_PATH, collaborationName);
        Path directoryPath = pathBuilder.buildDirectoryPath();
        Path collaborationPath = pathBuilder.addDirectory(collaborationName).buildDirectoryPath();
        Path collaborationFilePath = pathBuilder.buildFilePath();

        if (!doesFileExist(collaborationFilePath)) {
            throw new NoSuchCollaborationExistsException(String
                    .format(NoSuchCollaborationExistsException.DEFAULT_MESSAGE_FORMAT,
                            collaborationName, ownerUsername));
        }

        try {
            return Files.walk(directoryPath)
                    .filter(path -> !new File(String.valueOf(path)).isDirectory())
                    .filter(path -> !path.equals(collaborationPath))
                    .map(path -> {
                        try {
                            return getFileContents(path);
                        } catch (IOUnsuccessfulOperationException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new IOUnsuccessfulOperationException(String
                    .format(IOUnsuccessfulOperationException.DEFAULT_MESSAGE_FORMAT, e.getMessage()), e);
        }
    }

    public String listCollaborationParticipants(String username, String collaborationName, String ownerUsername)
            throws NoSuchCollaborationExistsException, IOUnsuccessfulOperationException {

        Path collaborationPath = new PathBuilder()
                .addDirectoriesSequentially(ownerUsername, COLLABORATIONS_PATH,
                        OWNER_PATH, collaborationName, collaborationName)
                .buildFilePath();

        if (!doesFileExist(collaborationPath)) {
            throw new NoSuchCollaborationExistsException(String
                    .format(NoSuchCollaborationExistsException.DEFAULT_MESSAGE_FORMAT, collaborationName, username));
        }

        Collaboration collaboration = Collaboration.of(getFileContents(collaborationPath));

        List<String> participants = new ArrayList<>(collaboration.getParticipants());

        participants.add(collaboration.getOwnerUsername());

        return String.join(System.lineSeparator(), participants);
    }

    private boolean isUsernameInvalid(String username) {
        return username == null || username.isEmpty() || username.isBlank();
    }

    private boolean isPasswordInvalid(String password) {
        return password == null || password.isEmpty() || password.isBlank() || password.length() < MIN_PASSWORD_LENGTH;
    }

    private void removeUserFromCollab(String username, String collaborationName)
            throws IOUnsuccessfulOperationException {
        Path fullPath = new PathBuilder()
                .addDirectory(username)
                .addDirectory(COLLABORATIONS_PATH)
                .addDirectory(PARTICIPANT_PATH)
                .buildFilePath();

        removeStringFromFile(fullPath, collaborationName + WHITESPACE + username);
    }

    private void saveNewlyRegisteredUser(String username, String password) {
        writeToFile(FULL_PATH, username + WHITESPACE + password, StandardOpenOption.APPEND);
        users.put(username, password);
    }

    private void loadRegisteredUsers() {
        checkStateOfUsers();

        try {
            users = Files.lines(FULL_PATH)
                    .map(line -> line.split(WHITESPACE))
                    .collect(Collectors.toMap(line -> line[FIRST_ARGUMENT], line -> line[SECOND_ARGUMENT]));
        } catch (IOException e) {
            throw new RuntimeException(String
                    .format(IOUnsuccessfulOperationException.DEFAULT_MESSAGE_FORMAT, e.getMessage()), e);
        }
    }

    private void refactorFile(Path oldPath, Path newDirectoryPath, Path newPath)
            throws IOUnsuccessfulOperationException {

        if (!doesFileExist(newDirectoryPath)) {
            try {
                createDirectories(newDirectoryPath);
            } catch (IOException e) {
                throw new IOUnsuccessfulOperationException(String
                        .format(IOUnsuccessfulOperationException.DEFAULT_MESSAGE_FORMAT, e.getMessage()), e);
            }
        }

        try {
            Files.move(oldPath, newPath);
        } catch (IOException e) {
            throw new IOUnsuccessfulOperationException(String
                    .format(IOUnsuccessfulOperationException.DEFAULT_MESSAGE_FORMAT, e.getMessage()), e);
        }
    }

    private void writeToFile(Path pathToFile, String stringToWrite, StandardOpenOption... openOptions) {
        try {
            Files.writeString(pathToFile,
                    stringToWrite + System.lineSeparator(),
                    StandardCharsets.UTF_8,
                    openOptions);
        } catch (IOException e) {
            throw new RuntimeException(String
                    .format(IOUnsuccessfulOperationException.DEFAULT_MESSAGE_FORMAT, e.getMessage()), e);
        }
    }

    private void deleteFile(Path pathToFile) throws IOUnsuccessfulOperationException {

        try {
            Files.delete(pathToFile);
        } catch (IOException e) {
            throw new IOUnsuccessfulOperationException(String
                    .format(IOUnsuccessfulOperationException.DEFAULT_MESSAGE_FORMAT, e.getMessage()), e);
        }
    }

    private void deleteDirectoryAndContents(Path directoryPath) throws IOUnsuccessfulOperationException {
        try {
            Files.walk(directoryPath)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (IOException e) {
            throw new IOUnsuccessfulOperationException(String
                    .format(IOUnsuccessfulOperationException.DEFAULT_MESSAGE_FORMAT, e.getMessage()), e);
        }
    }

    private boolean doesFileContainString(Path fullPath, String toCheck) throws IOUnsuccessfulOperationException {
        try {
            return Files.lines(fullPath).anyMatch(label -> label.equals(toCheck));
        } catch (IOException e) {
            throw new IOUnsuccessfulOperationException(String
                    .format(IOUnsuccessfulOperationException.DEFAULT_MESSAGE_FORMAT, e.getMessage()), e);
        }
    }

    private void removeStringFromFile(Path fullPath, String toRemove) throws IOUnsuccessfulOperationException {
        try {
            String updatedContents = Files
                    .lines(fullPath)
                    .filter(label -> !label.equals(toRemove))
                    .collect(Collectors.joining(SEPARATOR));

            writeToFile(fullPath, updatedContents, StandardOpenOption.WRITE);
        } catch (IOException e) {
            throw new IOUnsuccessfulOperationException(String
                    .format(IOUnsuccessfulOperationException.DEFAULT_MESSAGE_FORMAT, e.getMessage()), e);
        }
    }

    private boolean checkStateOfStorage(Path directoriesPath, Path localFullPath)
            throws IOUnsuccessfulOperationException {

        if (!doesFileExist(localFullPath)) {
            try {
                createDirectories(directoriesPath);
                createFile(localFullPath);
            } catch (IOException e) {
                throw new IOUnsuccessfulOperationException(String
                        .format(IOUnsuccessfulOperationException.DEFAULT_MESSAGE_FORMAT, e.getMessage()), e);
            }

            return false;
        }

        return true;
    }

    private void checkStateOfUsers() {
        if (!doesFileExist(FULL_PATH)) {
            try {
                createDirectories(PATH_TO_FILE);
                createFile(FULL_PATH);
            } catch (IOException e) {
                throw new RuntimeException(String
                        .format(IOUnsuccessfulOperationException.DEFAULT_MESSAGE_FORMAT, e.getMessage()), e);
            }
        }
    }

    private String getFileContents(Path fullPath) throws IOUnsuccessfulOperationException {
        try {
            return Files.lines(fullPath).collect(Collectors.joining(SEPARATOR));
        } catch (IOException e) {
            throw new IOUnsuccessfulOperationException(String
                    .format(IOUnsuccessfulOperationException.DEFAULT_MESSAGE_FORMAT, e.getMessage()), e);
        }
    }

    private boolean doesFileExist(Path pathToFile) {
        return Files.exists(pathToFile);
    }

    private void createDirectories(Path directoriesToCreate) throws IOException {
        Files.createDirectories(directoriesToCreate);
    }

    private void createFile(Path pathToFile) throws IOException {
        Files.createFile(pathToFile);
    }
}
