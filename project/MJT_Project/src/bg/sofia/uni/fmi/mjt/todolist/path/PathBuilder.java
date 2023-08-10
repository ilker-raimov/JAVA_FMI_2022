package bg.sofia.uni.fmi.mjt.todolist.path;

import java.nio.file.FileSystems;
import java.nio.file.Path;

public class PathBuilder {
    private final StringBuilder fullPathBuilder;

    private static final String CREDENTIALS_PATH = "data";
    private static final String TXT_EXTENSION = ".txt";
    private static final String SEPARATOR = FileSystems.getDefault().getSeparator();

    public PathBuilder() {
        this(CREDENTIALS_PATH);
    }

    public PathBuilder(String initialDirectory) {
        fullPathBuilder = new StringBuilder(initialDirectory);
    }

    public PathBuilder addDirectory(String directoryToAdd) {
        if (directoryToAdd != null) {
            fullPathBuilder.append(SEPARATOR).append(directoryToAdd);
        }

        return this;
    }

    //if the first argument is not present (null) adds the second one instead in its place if it is present (not null)
    public PathBuilder addDirectory(String directoryToAdd, String directoryToAddSecondary) {
        if (directoryToAdd != null) {
            fullPathBuilder.append(SEPARATOR).append(directoryToAdd);
        } else if (directoryToAddSecondary != null) {
            fullPathBuilder.append(SEPARATOR).append(directoryToAddSecondary);
        }

        return this;
    }

    public PathBuilder addDirectoriesSequentially(String... directoriesToAdd) {
        if (directoriesToAdd != null && directoriesToAdd.length != 0) {
            for (String directoryToAdd : directoriesToAdd) {
                this.addDirectory(directoryToAdd);
            }
        }

        return this;
    }

    public Path buildDirectoryPath() {
        return Path.of(fullPathBuilder.toString());
    }

    public Path buildFilePath() {
        return Path.of(fullPathBuilder.toString() + TXT_EXTENSION);
    }

    public PathBuilder copy() {
        return new PathBuilder(this.fullPathBuilder.toString());
    }
}
