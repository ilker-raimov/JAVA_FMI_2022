package bg.sofia.uni.fmi.mjt.todolist.command;

import bg.sofia.uni.fmi.mjt.todolist.ServerStorage;
import bg.sofia.uni.fmi.mjt.todolist.argument.ArgumentCount;
import bg.sofia.uni.fmi.mjt.todolist.argument.ArgumentType;
import bg.sofia.uni.fmi.mjt.todolist.argument.IsOptional;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.CollaborationAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.CorruptedDataException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.IOUnsuccessfulOperationException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.IllegalTaskNameException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.InvalidArgumentCountException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.InvalidDateException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.InvalidDateFormatException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.InvalidPasswordException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.InvalidUsernameException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.LabelAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.NoSuchArgumentException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.NoSuchCollaborationExistsException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.NoSuchLabelException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.NoSuchTaskException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.NoSuchUserException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.RestrictedPermissionCommandException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.TaskAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.UnknownCommandException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.UserAlreadyAddedException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.UsernameAlreadyTakenException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.WrongPasswordException;
import bg.sofia.uni.fmi.mjt.todolist.response.ResponseStatus;
import bg.sofia.uni.fmi.mjt.todolist.response.ServerResponseBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

public class CommandExecutor {
    private final ServerStorage serverStorage;


    private static final boolean CHECK_PERIOD = true;
    private static final boolean DO_NOT_CHECK_PERIOD = false;
    private static final String REQUIRED_DATE_FORMAT = "dd.MM.yyyy";
    private static final String DAY_AND_MONTH_FORMAT = "%02d";
    private static final String DATE_SPLITTER = "\\.";
    private static final String DATE_JOINER = ".";

    public CommandExecutor(ServerStorage serverStorage) {
        this.serverStorage = serverStorage;
    }

    public ServerResponseBuilder execute(Command toExecute)
            throws UnknownCommandException, InvalidArgumentCountException,
            NoSuchArgumentException, RestrictedPermissionCommandException {

        //null check command

        if (toExecute.commandType() == null) {
            throw new UnknownCommandException("Unknown command");
        }

        ServerResponseBuilder result;

        if (toExecute.username() == null) {
            result = switch (toExecute.commandType()) {
                case REGISTER -> registerUser(toExecute.arguments());
                case LOGIN -> loginUser(toExecute.arguments());
                default -> throw new RestrictedPermissionCommandException("You must first login " +
                        "before accessing other commands");
            };
        } else {
            result = switch (toExecute.commandType()) {
                case REGISTER -> registerUser(toExecute.arguments());
                case LOGIN -> loginUser(toExecute.arguments());
                case ADD_TASK -> addTask(toExecute.username(), toExecute.arguments());
                case UPDATE_TASK -> updateTask(toExecute.username(), toExecute.arguments());
                case DELETE_TASK -> deleteTask(toExecute.username(), toExecute.arguments());
                case GET_TASK -> getTask(toExecute.username(), toExecute.arguments());
                case FINISH_TASK -> finishTask(toExecute.username(), toExecute.arguments());
                case LIST_TASKS -> listTasks(toExecute.username(), toExecute.arguments());
                case ADD_LABEL -> addLabel(toExecute.username(), toExecute.arguments());
                case DELETE_LABEL -> deleteLabel(toExecute.username(), toExecute.arguments());
                case LIST_LABELS -> listLabels(toExecute.username(), toExecute.arguments());
                case LABEL_TASK -> labelTask(toExecute.username(), toExecute.arguments());
                case ADD_COLLABORATION -> addCollaboration(toExecute.username(), toExecute.arguments());
                case DELETE_COLLABORATION -> deleteCollaboration(toExecute.username(), toExecute.arguments());
                case LIST_COLLABORATIONS -> listCollaborations(toExecute.username(), toExecute.arguments());
                case ADD_USER -> addUserToCollaboration(toExecute.username(), toExecute.arguments());
                case ADD_COLLABORATION_TASK -> addCollaborationTask(toExecute.username(), toExecute.arguments());
                case ASSIGN_TASK -> assignTask(toExecute.username(), toExecute.arguments());
                case LIST_COLLABORATION_TASKS -> listCollaborationTasks(toExecute.username(), toExecute.arguments());
                case LIST_USERS -> listCollaborationParticipants(toExecute.username(), toExecute.arguments());
            };
        }

        if (result == null) {
            result = new ServerResponseBuilder()
                    .setResponseStatus(ResponseStatus.ERROR)
                    .setClientUsername(toExecute.username())
                    .setMessage("Unknown exception");
        }

        return result.setCommandType(toExecute.commandType());
    }

    private ServerResponseBuilder registerUser(String[] arguments)
            throws InvalidArgumentCountException, NoSuchArgumentException {

        if (arguments.length != ArgumentCount.REGISTER_ARG_COUNT.getValue()) {
            throw new InvalidArgumentCountException("Register command must have 2 arguments: username and password");
        }

        String username = CommandParser.parseStringValue(arguments,
                ArgumentType.USERNAME_ARG.getLabel(), IsOptional.NOT_OPTIONAL.getOptionality());
        String password = CommandParser.parseStringValue(arguments,
                ArgumentType.PASSWORD_ARG.getLabel(), IsOptional.NOT_OPTIONAL.getOptionality());

        ServerResponseBuilder result = new ServerResponseBuilder().setClientUsername(username);

        try {
            result.setMessage(serverStorage.registerUser(username, password));

        } catch (InvalidPasswordException | InvalidUsernameException | UsernameAlreadyTakenException e) {

            return result.setResponseStatus(ResponseStatus.ERROR).setMessage(e.getMessage());
        }

        return result.setResponseStatus(ResponseStatus.OK);
    }

    private ServerResponseBuilder loginUser(String[] arguments)
            throws InvalidArgumentCountException, NoSuchArgumentException {

        if (arguments.length != ArgumentCount.LOGIN_ARG_COUNT.getValue()) {
            throw new InvalidArgumentCountException("Login command must have 2 arguments: username and password");
        }

        String username = CommandParser.parseStringValue(arguments,
                ArgumentType.USERNAME_ARG.getLabel(), IsOptional.NOT_OPTIONAL.getOptionality());
        String password = CommandParser.parseStringValue(arguments,
                ArgumentType.PASSWORD_ARG.getLabel(), IsOptional.NOT_OPTIONAL.getOptionality());

        ServerResponseBuilder result = new ServerResponseBuilder().setClientUsername(username);

        try {
            result.setMessage(serverStorage.loginUser(username, password));

        } catch (InvalidPasswordException | InvalidUsernameException | NoSuchUserException | WrongPasswordException e) {

            return result.setResponseStatus(ResponseStatus.ERROR).setMessage(e.getMessage());
        }

        return result.setResponseStatus(ResponseStatus.OK);
    }

    private ServerResponseBuilder addTask(String username, String[] arguments)
            throws InvalidArgumentCountException, NoSuchArgumentException {

        if (arguments.length < ArgumentCount.ADD_TASK_MIN_ARG_COUNT.getValue()) {
            throw new InvalidArgumentCountException("Add-task command must have at least 1 argument - name");
        }

        String name = CommandParser.parseStringValue(arguments,
                ArgumentType.NAME_ARG.getLabel(), IsOptional.NOT_OPTIONAL.getOptionality());
        String date = CommandParser.parseStringValue(arguments,
                ArgumentType.DATE_ARG.getLabel(), IsOptional.OPTIONAL.getOptionality());
        String dueToDate = CommandParser.parseStringValue(arguments,
                ArgumentType.DUE_TO_DATE.getLabel(), IsOptional.OPTIONAL.getOptionality());
        String description = CommandParser.parseStringValue(arguments,
                ArgumentType.DESCRIPTION_ARG.getLabel(), IsOptional.OPTIONAL.getOptionality());

        ServerResponseBuilder result = new ServerResponseBuilder().setClientUsername(username);

        try {
            date = dateFormatter(date);
            dueToDate = dateFormatter(dueToDate);
            dateCheck(date, CHECK_PERIOD);
            dateCheck(dueToDate, CHECK_PERIOD);

            result.setMessage(serverStorage.addTask(username, name, date, dueToDate, description));

        } catch (IllegalArgumentException | TaskAlreadyExistsException | IOUnsuccessfulOperationException |
                 InvalidDateFormatException | InvalidDateException e) {

            return result.setResponseStatus(ResponseStatus.ERROR).setMessage(e.getMessage());
        }

        return result.setResponseStatus(ResponseStatus.OK);
    }

    private ServerResponseBuilder updateTask(String username, String[] arguments)
            throws InvalidArgumentCountException, NoSuchArgumentException {

        if (arguments.length < ArgumentCount.UPDATE_TASK_MIN_ARG_COUNT.getValue()) {
            throw new InvalidArgumentCountException("Update-task command must have at least 1 argument - name");
        }

        String name = CommandParser.parseStringValue(arguments,
                ArgumentType.NAME_ARG.getLabel(), IsOptional.NOT_OPTIONAL.getOptionality());
        String date = CommandParser.parseStringValue(arguments,
                ArgumentType.DATE_ARG.getLabel(), IsOptional.OPTIONAL.getOptionality());
        String dueToDate = CommandParser.parseStringValue(arguments,
                ArgumentType.DUE_TO_DATE.getLabel(), IsOptional.OPTIONAL.getOptionality());
        String description = CommandParser.parseStringValue(arguments,
                ArgumentType.DESCRIPTION_ARG.getLabel(), IsOptional.OPTIONAL.getOptionality());
        String completed = CommandParser.parseStringValue(arguments,
                ArgumentType.COMPLETED_ARG.getLabel(), IsOptional.OPTIONAL.getOptionality());

        ServerResponseBuilder result = new ServerResponseBuilder().setClientUsername(username);

        try {
            date = dateFormatter(date);
            dueToDate = dateFormatter(dueToDate);
            dateCheck(date, CHECK_PERIOD);
            dateCheck(dueToDate, CHECK_PERIOD);

            result.setMessage(serverStorage.updateTask(username, name, date, dueToDate, description, completed));

        } catch (IllegalArgumentException | IOUnsuccessfulOperationException | NoSuchArgumentException |
                 NoSuchTaskException | InvalidDateFormatException | InvalidDateException e) {

            return result.setResponseStatus(ResponseStatus.ERROR).setMessage(e.getMessage());
        }

        return result.setResponseStatus(ResponseStatus.OK);
    }

    private ServerResponseBuilder deleteTask(String username, String[] arguments)
            throws InvalidArgumentCountException, NoSuchArgumentException {

        if (arguments.length < ArgumentCount.DELETE_TASK_MIN_ARG_COUNT.getValue()) {
            throw new InvalidArgumentCountException("Add-task command must have at least 1 argument - name");
        }

        String name = CommandParser.parseStringValue(arguments,
                ArgumentType.NAME_ARG.getLabel(), IsOptional.NOT_OPTIONAL.getOptionality());
        String date = CommandParser.parseStringValue(arguments,
                ArgumentType.DATE_ARG.getLabel(), IsOptional.OPTIONAL.getOptionality());
        String completed = CommandParser.parseStringValue(arguments,
                ArgumentType.COMPLETED_ARG.getLabel(), IsOptional.OPTIONAL.getOptionality());

        ServerResponseBuilder result = new ServerResponseBuilder().setClientUsername(username);

        try {
            date = dateFormatter(date);
            dateCheck(date, DO_NOT_CHECK_PERIOD);

            result.setMessage(serverStorage.deleteTask(username, name, date, completed));
        } catch (IllegalArgumentException | TaskAlreadyExistsException | NoSuchTaskException |
                 IOUnsuccessfulOperationException | InvalidDateFormatException | InvalidDateException e) {
            return result.setResponseStatus(ResponseStatus.ERROR).setMessage(e.getMessage());
        }

        return result.setResponseStatus(ResponseStatus.OK);
    }

    private ServerResponseBuilder getTask(String username, String[] arguments)
            throws InvalidArgumentCountException, NoSuchArgumentException {

        if (arguments.length < ArgumentCount.GET_TASK_MIN_ARG_COUNT.getValue()) {
            throw new InvalidArgumentCountException("Get-task command must have at least 1 argument - name");
        }

        String name = CommandParser.parseStringValue(arguments,
                ArgumentType.NAME_ARG.getLabel(), IsOptional.NOT_OPTIONAL.getOptionality());
        String date = CommandParser.parseStringValue(arguments,
                ArgumentType.DATE_ARG.getLabel(), IsOptional.OPTIONAL.getOptionality());
        String completed = CommandParser.parseStringValue(arguments,
                ArgumentType.COMPLETED_ARG.getLabel(), IsOptional.OPTIONAL.getOptionality());

        ServerResponseBuilder result = new ServerResponseBuilder().setClientUsername(username);

        try {
            date = dateFormatter(date);
            dateCheck(date, CHECK_PERIOD);

            result.setMessage(serverStorage.getTask(username, name, date, completed));

        } catch (IllegalArgumentException | NoSuchTaskException | IOUnsuccessfulOperationException |
                 InvalidDateFormatException | InvalidDateException e) {

            return result.setResponseStatus(ResponseStatus.ERROR).setMessage(e.getMessage());
        }

        return result.setResponseStatus(ResponseStatus.OK);
    }

    private ServerResponseBuilder finishTask(String username, String[] arguments)
            throws InvalidArgumentCountException, NoSuchArgumentException {

        if (arguments.length < ArgumentCount.FINISH_TASK_MIN_ARG_COUNT.getValue()) {
            throw new InvalidArgumentCountException("Add-task command must have at least 1 argument - name");
        }

        String name = CommandParser.parseStringValue(arguments,
                ArgumentType.NAME_ARG.getLabel(), IsOptional.NOT_OPTIONAL.getOptionality());
        String date = CommandParser.parseStringValue(arguments,
                ArgumentType.DATE_ARG.getLabel(), IsOptional.OPTIONAL.getOptionality());

        ServerResponseBuilder result = new ServerResponseBuilder().setClientUsername(username);

        try {
            date = dateFormatter(date);
            dateCheck(date, DO_NOT_CHECK_PERIOD);

            result.setMessage(serverStorage.finishTask(username, name, date));
        } catch (IllegalArgumentException | NoSuchTaskException | IOUnsuccessfulOperationException |
                 InvalidDateFormatException | InvalidDateException e) {

            return result.setResponseStatus(ResponseStatus.ERROR).setMessage(e.getMessage());
        }

        return result.setResponseStatus(ResponseStatus.OK);
    }

    private ServerResponseBuilder listTasks(String username, String[] arguments)
            throws NoSuchArgumentException {

        String date = CommandParser.parseStringValue(arguments,
                ArgumentType.DATE_ARG.getLabel(), IsOptional.OPTIONAL.getOptionality());
        String completed = CommandParser.parseStringValue(arguments,
                ArgumentType.COMPLETED_ARG.getLabel(), IsOptional.OPTIONAL.getOptionality());
        String label = CommandParser.parseStringValue(arguments,
                ArgumentType.LABEL_ARG.getLabel(), IsOptional.OPTIONAL.getOptionality());

        ServerResponseBuilder result = new ServerResponseBuilder().setClientUsername(username);

        try {
            date = dateFormatter(date);
            dateCheck(date, CHECK_PERIOD);

            result.setMessage(serverStorage.listTasks(username, date, completed, label));
        } catch (IllegalArgumentException | InvalidDateFormatException | InvalidDateException e) {

            return result.setResponseStatus(ResponseStatus.ERROR).setMessage(e.getMessage());
        }

        return result.setResponseStatus(ResponseStatus.OK);
    }

    private ServerResponseBuilder addLabel(String username, String[] arguments) throws NoSuchArgumentException {

        String label = CommandParser.parseStringValue(arguments,
                ArgumentType.NAME_ARG.getLabel(), IsOptional.NOT_OPTIONAL.getOptionality());

        ServerResponseBuilder result = new ServerResponseBuilder().setClientUsername(username);

        try {
            result.setMessage(serverStorage.addLabel(username, label));
        } catch (IOUnsuccessfulOperationException | LabelAlreadyExistsException e) {

            return result.setResponseStatus(ResponseStatus.ERROR).setMessage(e.getMessage());
        }

        return result.setResponseStatus(ResponseStatus.OK);
    }

    private ServerResponseBuilder deleteLabel(String username, String[] arguments) throws NoSuchArgumentException {

        String labelName = CommandParser.parseStringValue(arguments,
                ArgumentType.NAME_ARG.getLabel(), IsOptional.NOT_OPTIONAL.getOptionality());

        ServerResponseBuilder result = new ServerResponseBuilder().setClientUsername(username);

        try {
            result.setMessage(serverStorage.deleteLabel(username, labelName));
        } catch (IOUnsuccessfulOperationException | NoSuchLabelException e) {

            return result.setResponseStatus(ResponseStatus.ERROR).setMessage(e.getMessage());
        }

        return result.setResponseStatus(ResponseStatus.OK);
    }

    private ServerResponseBuilder listLabels(String username, String[] arguments) throws InvalidArgumentCountException {

        if (arguments.length > ArgumentCount.LIST_LABELS_MAX_ARG_COUNT.getValue()) {
            throw new InvalidArgumentCountException("List-labels command has no additional arguments");
        }

        ServerResponseBuilder result = new ServerResponseBuilder().setClientUsername(username);

        try {
            result.setMessage(serverStorage.listLabels(username));
        } catch (IOUnsuccessfulOperationException | LabelAlreadyExistsException e) {

            return result.setResponseStatus(ResponseStatus.ERROR).setMessage(e.getMessage());
        }

        return result.setResponseStatus(ResponseStatus.OK);
    }

    private ServerResponseBuilder labelTask(String username, String[] arguments)
            throws InvalidArgumentCountException, NoSuchArgumentException {

        if (arguments.length < ArgumentCount.LABEL_TASK_MIN_ARG_COUNT.getValue()) {
            throw new InvalidArgumentCountException("Label-task must have" +
                    " at least 2 arguments - task name and label name");
        }

        String name = CommandParser.parseStringValue(arguments,
                ArgumentType.NAME_ARG.getLabel(), IsOptional.NOT_OPTIONAL.getOptionality());
        String date = CommandParser.parseStringValue(arguments,
                ArgumentType.DATE_ARG.getLabel(), IsOptional.OPTIONAL.getOptionality());
        String completed = CommandParser.parseStringValue(arguments,
                ArgumentType.COMPLETED_ARG.getLabel(), IsOptional.OPTIONAL.getOptionality());
        String label = CommandParser.parseStringValue(arguments,
                ArgumentType.LABEL_ARG.getLabel(), IsOptional.NOT_OPTIONAL.getOptionality());

        ServerResponseBuilder result = new ServerResponseBuilder().setClientUsername(username);

        try {
            dateCheck(date, DO_NOT_CHECK_PERIOD);

            result.setMessage(serverStorage.labelTask(username, name, date, completed, label));
        } catch (IllegalArgumentException | IOUnsuccessfulOperationException | InvalidDateFormatException |
                 InvalidDateException | NoSuchTaskException e) {

            return result.setResponseStatus(ResponseStatus.ERROR).setMessage(e.getMessage());
        }

        return result.setResponseStatus(ResponseStatus.OK);
    }

    private ServerResponseBuilder addCollaboration(String username, String[] arguments)
            throws InvalidArgumentCountException, NoSuchArgumentException {

        if (arguments.length != ArgumentCount.ADD_COLLABORATION_ARG_COUNT.getValue()) {
            throw new InvalidArgumentCountException("Add-collaboration command has only 1 argument - name");
        }

        String name = CommandParser.parseStringValue(arguments,
                ArgumentType.NAME_ARG.getLabel(), IsOptional.NOT_OPTIONAL.getOptionality());

        ServerResponseBuilder result = new ServerResponseBuilder().setClientUsername(username);

        try {
            result.setMessage(serverStorage.addCollaboration(username, name));
        } catch (CollaborationAlreadyExistsException | IOUnsuccessfulOperationException e) {
            return result.setResponseStatus(ResponseStatus.ERROR).setMessage(e.getMessage());
        }

        return result.setResponseStatus(ResponseStatus.OK);
    }

    private ServerResponseBuilder deleteCollaboration(String username, String[] arguments)
            throws InvalidArgumentCountException, NoSuchArgumentException {

        if (arguments.length != ArgumentCount.DELETE_COLLABORATION_ARG_COUNT.getValue()) {
            throw new InvalidArgumentCountException("Add-collaboration command has only 1 argument - name");
        }

        String name = CommandParser.parseStringValue(arguments,
                ArgumentType.NAME_ARG.getLabel(), IsOptional.NOT_OPTIONAL.getOptionality());

        ServerResponseBuilder result = new ServerResponseBuilder().setClientUsername(username);

        try {
            result.setMessage(serverStorage.deleteCollaboration(username, name));
        } catch (IOUnsuccessfulOperationException | NoSuchCollaborationExistsException | CorruptedDataException e) {
            return result.setResponseStatus(ResponseStatus.ERROR).setMessage(e.getMessage());
        }

        return result.setResponseStatus(ResponseStatus.OK);
    }

    private ServerResponseBuilder listCollaborations(String username, String[] arguments)
            throws InvalidArgumentCountException {

        if (arguments.length != ArgumentCount.LIST_COLLABORATIONS_ARG_COUNT.getValue()) {
            throw new InvalidArgumentCountException("Add-collaboration command has only 1 argument - name"); //fix
        }

        ServerResponseBuilder result = new ServerResponseBuilder().setClientUsername(username);

        try {
            result.setMessage(serverStorage.listCollaborations(username));
        } catch (IOUnsuccessfulOperationException e) {
            return result.setResponseStatus(ResponseStatus.ERROR).setMessage(e.getMessage());
        }

        return result.setResponseStatus(ResponseStatus.OK);
    }

    private ServerResponseBuilder addUserToCollaboration(String username, String[] arguments)
            throws InvalidArgumentCountException, NoSuchArgumentException {

        if (arguments.length != ArgumentCount.ADD_USER_TO_COLLABORATION.getValue()) {
            throw new InvalidArgumentCountException("Add-collaboration command has only 1 argument - name"); //fix
        }

        String name = CommandParser.parseStringValue(arguments,
                ArgumentType.NAME_ARG.getLabel(), IsOptional.NOT_OPTIONAL.getOptionality());
        String usernameToAdd = CommandParser.parseStringValue(arguments,
                ArgumentType.USERNAME_ARG.getLabel(), IsOptional.NOT_OPTIONAL.getOptionality());

        ServerResponseBuilder result = new ServerResponseBuilder().setClientUsername(username);

        try {
            result.setMessage(serverStorage.addUserToCollaboration(username, name, usernameToAdd));
        } catch (IOUnsuccessfulOperationException | NoSuchCollaborationExistsException | UserAlreadyAddedException e) {
            return result.setResponseStatus(ResponseStatus.ERROR).setMessage(e.getMessage());
        }

        return result.setResponseStatus(ResponseStatus.OK);
    }

    private ServerResponseBuilder addCollaborationTask(String username, String[] arguments)
            throws InvalidArgumentCountException, NoSuchArgumentException {

        if (arguments.length < ArgumentCount.ADD_COLLABORATION_TASK_MIN_ARG_COUNT.getValue()) {
            throw new InvalidArgumentCountException("Add-collaboration command has only 1 argument - name"); //fix
        }

        String collaborationName = CommandParser.parseStringValue(arguments,
                ArgumentType.COLLABORATION_ARG.getLabel(), IsOptional.NOT_OPTIONAL.getOptionality());
        String name = CommandParser.parseStringValue(arguments,
                ArgumentType.NAME_ARG.getLabel(), IsOptional.NOT_OPTIONAL.getOptionality());
        String date = CommandParser.parseStringValue(arguments,
                ArgumentType.DATE_ARG.getLabel(), IsOptional.OPTIONAL.getOptionality());
        String dueToDate = CommandParser.parseStringValue(arguments,
                ArgumentType.DUE_TO_DATE.getLabel(), IsOptional.OPTIONAL.getOptionality());
        String description = CommandParser.parseStringValue(arguments,
                ArgumentType.DESCRIPTION_ARG.getLabel(), IsOptional.OPTIONAL.getOptionality());

        ServerResponseBuilder result = new ServerResponseBuilder().setClientUsername(username);

        try {
            result.setMessage(serverStorage.addCollaborationTask(username,
                    collaborationName, name, date, dueToDate, description));
        } catch (IOUnsuccessfulOperationException | TaskAlreadyExistsException | IllegalTaskNameException e) {
            return result.setResponseStatus(ResponseStatus.ERROR).setMessage(e.getMessage());
        }

        return result.setResponseStatus(ResponseStatus.OK);
    }

    private ServerResponseBuilder assignTask(String username, String[] arguments)
            throws InvalidArgumentCountException, NoSuchArgumentException {

        if (arguments.length < ArgumentCount.ASSIGN_TASK_MIN_ARG_COUNT.getValue()) {
            throw new InvalidArgumentCountException("Assign-task command has at least 3 arguments - " +
                    "collab name, task name and assignee username"); //fix
        }

        String collaborationName = CommandParser.parseStringValue(arguments,
                ArgumentType.COLLABORATION_ARG.getLabel(), IsOptional.NOT_OPTIONAL.getOptionality());
        String name = CommandParser.parseStringValue(arguments,
                ArgumentType.NAME_ARG.getLabel(), IsOptional.NOT_OPTIONAL.getOptionality());
        String date = CommandParser.parseStringValue(arguments,
                ArgumentType.DATE_ARG.getLabel(), IsOptional.OPTIONAL.getOptionality());
        String assigneeUsername = CommandParser.parseStringValue(arguments,
                ArgumentType.ASSIGNEE_ARG.getLabel(), IsOptional.NOT_OPTIONAL.getOptionality());

        ServerResponseBuilder result = new ServerResponseBuilder().setClientUsername(username);

        try {
            result.setMessage(serverStorage.assignTask(username, collaborationName, name, date, assigneeUsername));
        } catch (IOUnsuccessfulOperationException | NoSuchCollaborationExistsException | NoSuchTaskException |
                 NoSuchUserException e) {
            return result.setResponseStatus(ResponseStatus.ERROR).setMessage(e.getMessage());
        }

        return result.setResponseStatus(ResponseStatus.OK);
    }

    private ServerResponseBuilder listCollaborationTasks(String username, String[] arguments)
            throws InvalidArgumentCountException, NoSuchArgumentException {

        if (arguments.length != ArgumentCount.LIST_COLLABORATION_TASKS_ARG_COUNT.getValue()) {
            throw new InvalidArgumentCountException("List-collaboration-tasks command has 2 arguments - " +
                    "collab name and owner username"); //fix
        }

        String collaborationName = CommandParser.parseStringValue(arguments,
                ArgumentType.COLLABORATION_ARG.getLabel(), IsOptional.NOT_OPTIONAL.getOptionality());
        String ownerUsername = CommandParser.parseStringValue(arguments,
                ArgumentType.OWNER_ARG.getLabel(), IsOptional.NOT_OPTIONAL.getOptionality());

        ServerResponseBuilder result = new ServerResponseBuilder().setClientUsername(username);

        try {
            result.setMessage(serverStorage.listCollaborationTasks(collaborationName, ownerUsername));
        } catch (IOUnsuccessfulOperationException | NoSuchCollaborationExistsException e) {
            return result.setResponseStatus(ResponseStatus.ERROR).setMessage(e.getMessage());
        }

        return result.setResponseStatus(ResponseStatus.OK);
    }

    private ServerResponseBuilder listCollaborationParticipants(String username, String[] arguments)
            throws InvalidArgumentCountException, NoSuchArgumentException {

        if (arguments.length != ArgumentCount.LIST_COLLABORATION_TASKS_ARG_COUNT.getValue()) {
            throw new InvalidArgumentCountException("List-collaboration-tasks command has 2 arguments - " +
                    "collab name and owner username"); //fix
        }

        String collaborationName = CommandParser.parseStringValue(arguments,
                ArgumentType.COLLABORATION_ARG.getLabel(), IsOptional.NOT_OPTIONAL.getOptionality());
        String ownerUsername = CommandParser.parseStringValue(arguments,
                ArgumentType.OWNER_ARG.getLabel(), IsOptional.NOT_OPTIONAL.getOptionality());

        ServerResponseBuilder result = new ServerResponseBuilder().setClientUsername(username);

        try {
            result.setMessage(serverStorage.listCollaborationParticipants(username, collaborationName, ownerUsername));
        } catch (NoSuchCollaborationExistsException | IOUnsuccessfulOperationException e) {
            return result.setResponseStatus(ResponseStatus.ERROR).setMessage(e.getMessage());
        }

        return result.setResponseStatus(ResponseStatus.OK);
    }


    private void dateCheck(String toCheck, boolean checkIsAfter) throws InvalidDateFormatException, InvalidDateException {
        if (toCheck != null) {
            Date dateDate = checkDateFormat(toCheck);

            if (!isTodayOrAfterToday(dateDate) && checkIsAfter) {
                throw new InvalidDateException();
            }
        }
    }

    private Date checkDateFormat(String dateToParse) throws InvalidDateFormatException {
        Date result;
        SimpleDateFormat simpleDateFormatter = new SimpleDateFormat(REQUIRED_DATE_FORMAT);

        try {
            result = simpleDateFormatter.parse(dateToParse);
        } catch (ParseException e) {
            throw new InvalidDateFormatException(String
                    .format(InvalidDateFormatException.DEFAULT_MESSAGE_FORMAT, REQUIRED_DATE_FORMAT), e);
        }

        return result;
    }

    private boolean isTodayOrAfterToday(Date date) {
        Calendar today = Calendar.getInstance();
        Calendar specifiedDate = Calendar.getInstance();
        specifiedDate.setTime(date);

        if (today.get(Calendar.YEAR) == specifiedDate.get(Calendar.YEAR)) {
            if (today.get(Calendar.MONTH) == specifiedDate.get(Calendar.MONTH)) {
                return today.get(Calendar.DAY_OF_MONTH) <= specifiedDate.get(Calendar.DAY_OF_MONTH);
            }

            return today.get(Calendar.MONTH) <= specifiedDate.get(Calendar.MONTH);
        }

        return today.get(Calendar.YEAR) <= specifiedDate.get(Calendar.YEAR);
    }

    public static String dateFormatter(String toFormat) {
        if (toFormat == null) {
            return null;
        }

        String[] splitDate = toFormat.split(DATE_SPLITTER);

        splitDate[0] = String.format(DAY_AND_MONTH_FORMAT, Integer.parseInt(splitDate[0]));
        splitDate[1] = String.format(DAY_AND_MONTH_FORMAT, Integer.parseInt(splitDate[1]));

        return String.join(DATE_JOINER, splitDate);
    }
}