package bg.sofia.uni.fmi.mjt.todolist.path;

import org.junit.jupiter.api.Test;

import java.nio.file.FileSystems;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PathBuilderTest {
    private static final String SEPARATOR = FileSystems.getDefault().getSeparator();
    private static final String ERROR_MESSAGE1 = "PathBuilder class should construct as path correctly";

    @Test
    void testBuilder() {
        Path toCheck = Path.of("data" + SEPARATOR + "dir1" + SEPARATOR + "dir2" + SEPARATOR + "fileName1.txt");

        PathBuilder pathBuilder1 = new PathBuilder()
                .addDirectoriesSequentially("dir1", "dir2")
                .addDirectory("fileName1")
                .addDirectory(null, null);
        PathBuilder pathBuilder2 = pathBuilder1.copy();
        Path pathBuilt1 = pathBuilder1.buildFilePath();
        Path pathBuilt2 = pathBuilder2.buildFilePath();

        assertEquals(toCheck, pathBuilt1, ERROR_MESSAGE1);
        assertEquals(toCheck, pathBuilt2, ERROR_MESSAGE1);
    }
}
