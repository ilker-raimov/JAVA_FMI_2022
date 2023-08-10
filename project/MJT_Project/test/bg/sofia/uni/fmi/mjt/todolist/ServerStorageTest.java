package bg.sofia.uni.fmi.mjt.todolist;

import bg.sofia.uni.fmi.mjt.todolist.exceptions.CollaborationAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.IOUnsuccessfulOperationException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.IllegalTaskNameException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.InvalidPasswordException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.InvalidUsernameException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.LabelAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.NoSuchCollaborationExistsException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.NoSuchLabelException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.NoSuchTaskException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.NoSuchUserException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.TaskAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.UserAlreadyAddedException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.UsernameAlreadyTakenException;
import bg.sofia.uni.fmi.mjt.todolist.exceptions.WrongPasswordException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ServerStorageTest {
    private static final ServerStorage storage = new ServerStorage();
    private static final String INITIAL_PATH = "data";
    private static final String INVALID_USERNAME1 = null;
    private static final String INVALID_USERNAME2 = "    ";
    private static final String VALID_USERNAME = "ivan";
    private static final String VALID_USERNAME2 = "ivan2";
    private static final String VALID_USERNAME3 = "ivan3";
    private static final String VALID_USERNAME_NOT_REG = "hristo";
    private static final String INVALID_PASSWORD1 = null;
    private static final String INVALID_PASSWORD2 = "    ";
    private static final String INVALID_PASSWORD3 = "123456";
    private static final String VALID_PASSWORD = "12345678";
    private static final String VALID_PASSWORD_NOT_REG = "1do8bez1";
    private static final String DATE1 = "12.12.2024";
    private static final String DATE2 = "13.12.2024";
    private static final String TASK1 = "task1";
    private static final String TASK2 = "task2";
    private static final String DUE_TO_DATE1 = "20.12.2024";
    private static final String DESCRIPTION1 = "description1";
    private static final String LABEL1 = "label1";
    private static final String LABEL2 = "label2";
    private static final String COLLABORATION1 = "collab1";
    private static final String COLLABORATION2 = "collab2";
    private static final String COLLABORATION3 = "collab3";
    private static final String ERROR_MESSAGE1 = "Trying to register with invalid username should throw exception";
    private static final String ERROR_MESSAGE2 = "Trying to login with invalid username should throw exception";
    private static final String ERROR_MESSAGE3 = "Trying to add task " +
            "that has been already added should throw exception";
    private static final String ERROR_MESSAGE4 = "Trying to update task that does not exist should throw exception";
    private static final String ERROR_MESSAGE5 = "Trying to update task that exists should not throw exception";
    private static final String ERROR_MESSAGE6 = "Trying to delete task that does not exist should not throw exception";
    private static final String ERROR_MESSAGE7 = "Trying to get task that does not exist should throw exception";
    private static final String ERROR_MESSAGE8 = "Trying to get task that exists should not throw exception";
    private static final String ERROR_MESSAGE9 = "Trying to finish task that does not exist should throw exception";
    private static final String ERROR_MESSAGE10 = "Trying to finish task that exists should not throw exception";
    private static final String ERROR_MESSAGE11 = "Getting list of tasks with specified filters" +
            "should return correct tasks";
    private static final String ERROR_MESSAGE12 = "Trying to add label that exists should throw exception";
    private static final String ERROR_MESSAGE13 = "Trying to add label that does not exist should not throw exception";
    private static final String ERROR_MESSAGE14 = "Trying to delete label that does not exist should throw exception";
    private static final String ERROR_MESSAGE15 = "Trying to delete label that exists should not throw exception";
    private static final String ERROR_MESSAGE16 = "Listing labels should work properly";
    private static final String ERROR_MESSAGE17 = "Trying to label task that does not exist should throw exception";
    private static final String ERROR_MESSAGE18 = "Trying to add collaboration that exists should throw exception";
    private static final String ERROR_MESSAGE19 = "Trying to add collaboration " +
            "that does not exist should not throw exception";
    private static final String ERROR_MESSAGE20 = "Trying to delete collaboration" +
            "that does not exist should throw exception";
    private static final String ERROR_MESSAGE21 = "Trying to delete collaboration " +
            "that exists should not throw exception";
    private static final String ERROR_MESSAGE22 = "Listing collaborations a user is part of should work correctly";
    private static final String ERROR_MESSAGE23 = "Trying to add user to collaboration " +
            "that does not exist should throw exception";
    private static final String ERROR_MESSAGE24 = "Trying to add user to collaboration " +
            "that he is already part of should throw exception";
    private static final String ERROR_MESSAGE25 = "Trying to add user to collaboration " +
            "that he is not part of should not throw exception";
    private static final String ERROR_MESSAGE26 = "Trying to add task to a collaboration " +
            "named the same as the collaboration should throw exception";
    private static final String ERROR_MESSAGE27 = "Trying to add task to a collaboration " +
            "that does not exist should throw exception";
    private static final String ERROR_MESSAGE28 = "Trying to add task to a collaboration " +
            "that is not part of should not throw exception";
    private static final String ERROR_MESSAGE29 = "Trying to assign task to a user in collaboration " +
            "that does not exist should throw exception";
    private static final String ERROR_MESSAGE30 = "Trying to assign task to a user " +
            "that is not part of collaboration that exists should throw exception";
    private static final String ERROR_MESSAGE31 = "Trying to assign task that is not part of a collaboration to a user "
            + "that is part of collaboration that exists should throw exception";
    private static final String ERROR_MESSAGE32 = "Trying to assign task that is part of a collaboration to a user " +
            "that is part of collaboration that exists should not throw exception";
    private static final String ERROR_MESSAGE33 = "Trying to list tasks of collaboration " +
            "that does not exist should throw exception";
    private static final String ERROR_MESSAGE34 = "Trying to list tasks of collaboration " +
            "that exists should not throw exception";
    private static final String ERROR_MESSAGE35 = "Trying to list participants of collaboration " +
            "that does not exist should throw exception";
    private static final String ERROR_MESSAGE36 = "Trying to list participants of collaboration " +
            "that exists should not throw exception";

    @BeforeAll
    static void setUpBeforeAll() {
        try {
            storage.registerUser(VALID_USERNAME, VALID_PASSWORD);
            storage.registerUser(VALID_USERNAME2, VALID_PASSWORD);
            storage.registerUser(VALID_USERNAME3, VALID_PASSWORD);
            storage.addTask(VALID_USERNAME, TASK1, DATE1, DUE_TO_DATE1, DESCRIPTION1);
            storage.addTask(VALID_USERNAME, TASK2, DATE2, DUE_TO_DATE1, DESCRIPTION1);
            storage.addTask(VALID_USERNAME2, TASK1, DATE1, DUE_TO_DATE1, DESCRIPTION1);
            storage.addLabel(VALID_USERNAME, LABEL1);
            storage.addLabel(VALID_USERNAME2, LABEL2);
            storage.addCollaboration(VALID_USERNAME, COLLABORATION1);
            storage.addCollaboration(VALID_USERNAME2, COLLABORATION2);
            storage.addCollaborationTask(VALID_USERNAME, COLLABORATION1, TASK1, DATE1, DUE_TO_DATE1, DESCRIPTION1);
        } catch (UsernameAlreadyTakenException | InvalidUsernameException | InvalidPasswordException |
                 TaskAlreadyExistsException | IOUnsuccessfulOperationException | LabelAlreadyExistsException |
                 CollaborationAlreadyExistsException | IllegalTaskNameException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testRegisterInvalidUsername() {

        assertThrows(InvalidUsernameException.class, () -> storage.registerUser(INVALID_USERNAME1, VALID_PASSWORD),
                ERROR_MESSAGE1);
        assertThrows(InvalidUsernameException.class, () -> storage.registerUser(INVALID_USERNAME2, VALID_PASSWORD),
                ERROR_MESSAGE1);
    }

    @Test
    void testRegisterInvalidPassword() {

        assertThrows(InvalidPasswordException.class, () -> storage.registerUser(VALID_USERNAME, INVALID_PASSWORD1),
                ERROR_MESSAGE1);
        assertThrows(InvalidPasswordException.class, () -> storage.registerUser(VALID_USERNAME, INVALID_PASSWORD2),
                ERROR_MESSAGE1);
        assertThrows(InvalidPasswordException.class, () -> storage.registerUser(VALID_USERNAME, INVALID_PASSWORD3),
                ERROR_MESSAGE1);
    }

    @Test
    void testRegisterValidCredentials() {

        assertThrows(UsernameAlreadyTakenException.class, () -> storage.registerUser(VALID_USERNAME, VALID_PASSWORD),
                ERROR_MESSAGE1);
    }


    @Test
    void testLoginInvalidUsername() {

        assertThrows(InvalidUsernameException.class, () -> storage.loginUser(INVALID_USERNAME1, VALID_PASSWORD),
                ERROR_MESSAGE2);
        assertThrows(InvalidUsernameException.class, () -> storage.loginUser(INVALID_USERNAME2, VALID_PASSWORD),
                ERROR_MESSAGE2);
        assertThrows(NoSuchUserException.class, () -> storage.loginUser(VALID_USERNAME_NOT_REG, VALID_PASSWORD),
                ERROR_MESSAGE1);
        assertThrows(WrongPasswordException.class, () -> storage.loginUser(VALID_USERNAME, VALID_PASSWORD_NOT_REG),
                ERROR_MESSAGE1);
    }

    @Test
    void testLoginInvalidPassword() {

        assertThrows(InvalidPasswordException.class, () -> storage.loginUser(VALID_USERNAME, INVALID_PASSWORD1),
                ERROR_MESSAGE2);
        assertThrows(InvalidPasswordException.class, () -> storage.loginUser(VALID_USERNAME, INVALID_PASSWORD2),
                ERROR_MESSAGE2);
        assertThrows(InvalidPasswordException.class, () -> storage.loginUser(VALID_USERNAME, INVALID_PASSWORD3),
                ERROR_MESSAGE2);
    }

    @Test
    void testLoginValidCredentials() {

        assertDoesNotThrow(() -> storage.loginUser(VALID_USERNAME, VALID_PASSWORD),
                ERROR_MESSAGE2);
    }

    @Test
    void testAddTaskAlreadyAdded() {
        assertThrows(TaskAlreadyExistsException.class,
                () -> storage.addTask(VALID_USERNAME, TASK1, DATE1, DUE_TO_DATE1, DESCRIPTION1), ERROR_MESSAGE3);
    }

    @Test
    void testUpdateTask() {
        assertThrows(NoSuchTaskException.class,
                () -> storage.updateTask(VALID_USERNAME, TASK1,
                        null, null, null, null), ERROR_MESSAGE4);
        assertDoesNotThrow(() -> storage.updateTask(VALID_USERNAME, TASK1, DATE1,
                null, null, null), ERROR_MESSAGE5);
    }

    @Test
    void testDeleteTask() {
        assertThrows(NoSuchTaskException.class, () -> storage.deleteTask(VALID_USERNAME_NOT_REG, TASK1,
                null, null), ERROR_MESSAGE6);
    }

    @Test
    void testGetTask() {
        assertThrows(NoSuchTaskException.class, () -> storage.getTask(VALID_USERNAME_NOT_REG, TASK1,
                null, null), ERROR_MESSAGE7);

        assertDoesNotThrow(() -> storage.getTask(VALID_USERNAME, TASK1, DATE1, null), ERROR_MESSAGE8);
    }

    @Test
    void testFinishTask() {
        assertThrows(NoSuchTaskException.class, () -> storage.finishTask(VALID_USERNAME_NOT_REG, TASK1, null),
                ERROR_MESSAGE9);

        assertDoesNotThrow(() -> storage.finishTask(VALID_USERNAME, TASK2, DATE2), ERROR_MESSAGE10);
    }

    @Test
    void testListTasks() {
        assertEquals(TASK1, storage.listTasks(VALID_USERNAME, DATE1, null, null), ERROR_MESSAGE11);
    }

    @Test
    void testAddLabel() {
        assertThrows(LabelAlreadyExistsException.class,
                () -> storage.addLabel(VALID_USERNAME, LABEL1), ERROR_MESSAGE12);
        assertDoesNotThrow(() -> storage.addLabel(VALID_USERNAME_NOT_REG, LABEL2), ERROR_MESSAGE13);
    }

    @Test
    void testDeleteLabel() {
        assertThrows(NoSuchLabelException.class, () -> storage.deleteLabel(VALID_USERNAME, LABEL2), ERROR_MESSAGE14);
        assertDoesNotThrow(() -> storage.deleteLabel(VALID_USERNAME2, LABEL2), ERROR_MESSAGE15);
    }

    @Test
    void testListLabels() {
        try {
            assertEquals(LABEL1, storage.listLabels(VALID_USERNAME), ERROR_MESSAGE16);
        } catch (IOUnsuccessfulOperationException | LabelAlreadyExistsException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testLabelTask() {
        assertThrows(NoSuchTaskException.class,
                () -> storage.labelTask(VALID_USERNAME2, TASK1, null, null, LABEL1), ERROR_MESSAGE17);
        assertDoesNotThrow(() -> storage.labelTask(VALID_USERNAME2, TASK1, DATE1, null, LABEL1));
    }

    @Test
    void testAddCollaboration() {
        assertThrows(CollaborationAlreadyExistsException.class,
                () -> storage.addCollaboration(VALID_USERNAME, COLLABORATION1), ERROR_MESSAGE18);
        assertDoesNotThrow(() -> storage.addCollaboration(VALID_USERNAME2, COLLABORATION1), ERROR_MESSAGE19);
    }

    @Test
    void testDeleteCollaboration() {
        assertThrows(NoSuchCollaborationExistsException.class,
                () -> storage.deleteCollaboration(VALID_USERNAME, COLLABORATION2), ERROR_MESSAGE20);
        assertDoesNotThrow(() -> storage.deleteCollaboration(VALID_USERNAME2, COLLABORATION2), ERROR_MESSAGE21);
    }

    @Test
    void testListCollaborations() {
        try {
            assertEquals(COLLABORATION1, storage.listCollaborations(VALID_USERNAME), ERROR_MESSAGE22);
        } catch (IOUnsuccessfulOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testAddUserToCollaboration() {
        assertThrows(NoSuchCollaborationExistsException.class,
                () -> storage.addUserToCollaboration(VALID_USERNAME, COLLABORATION3, VALID_USERNAME2), ERROR_MESSAGE23);
        assertThrows(UserAlreadyAddedException.class,
                () -> storage.addUserToCollaboration(VALID_USERNAME, COLLABORATION1, VALID_USERNAME), ERROR_MESSAGE24);
        assertDoesNotThrow(() -> storage.addUserToCollaboration(VALID_USERNAME, COLLABORATION1, VALID_USERNAME2),
                ERROR_MESSAGE25);
    }

    @Test
    void addCollaborationTask() {
        assertThrows(IllegalTaskNameException.class,
                () -> storage.addCollaborationTask(VALID_USERNAME, COLLABORATION1,
                        COLLABORATION1, null, null, null), ERROR_MESSAGE26);
        assertThrows(TaskAlreadyExistsException.class,
                () -> storage.addCollaborationTask(VALID_USERNAME, COLLABORATION1,
                        TASK1, DATE1, DUE_TO_DATE1, DESCRIPTION1), ERROR_MESSAGE27);
        assertDoesNotThrow(() -> storage.addCollaborationTask(VALID_USERNAME, COLLABORATION1,
                TASK2, DATE2, DUE_TO_DATE1, DESCRIPTION1), ERROR_MESSAGE28);
    }

    @Test
    void testAssignTask() {
        assertThrows(NoSuchCollaborationExistsException.class,
                () -> storage.assignTask(VALID_USERNAME, COLLABORATION3, TASK1, DATE1, null), ERROR_MESSAGE29);
        assertThrows(NoSuchUserException.class,
                () -> storage.assignTask(VALID_USERNAME, COLLABORATION1,
                        TASK1, DATE1, VALID_USERNAME3), ERROR_MESSAGE30);
        assertThrows(NoSuchTaskException.class,
                () -> storage.assignTask(VALID_USERNAME, COLLABORATION1, TASK1, DATE2, VALID_USERNAME3), ERROR_MESSAGE31);
        assertDoesNotThrow(() -> storage.assignTask(VALID_USERNAME, COLLABORATION1, TASK1, DATE1, VALID_USERNAME2),
                ERROR_MESSAGE32);
    }

    @Test
    void testListCollaborationTasks() {
        assertThrows(NoSuchCollaborationExistsException.class,
                () -> storage.listCollaborationTasks(COLLABORATION1, VALID_USERNAME3), ERROR_MESSAGE33);
        assertDoesNotThrow(() -> storage.listCollaborationTasks(COLLABORATION1, VALID_USERNAME), ERROR_MESSAGE34);
    }

    @Test
    void testListCollaborationParticipants() {
        assertThrows(NoSuchCollaborationExistsException.class,
                () -> storage.listCollaborationParticipants(VALID_USERNAME3, COLLABORATION2, VALID_USERNAME3),
                ERROR_MESSAGE35);
        assertDoesNotThrow(() -> storage.listCollaborationParticipants(VALID_USERNAME, COLLABORATION1, VALID_USERNAME),
                ERROR_MESSAGE36);
    }

    @AfterAll
    static void clearUp() {
        File folder = new File(INITIAL_PATH);

        recursiveDelete(folder);
    }

    private static void recursiveDelete(File folder) {
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    recursiveDelete(file);
                } else {
                    file.delete();
                }
            }
        }

        folder.delete();
    }
}
