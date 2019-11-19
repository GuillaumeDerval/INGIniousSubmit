package be.ac.ucl.info.inginious_submit;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Scanner;

public class ConfigReader {

    public static class ConfigurationEntry {
        public String subproblemId;
        public String language;
        public String[] extractArgs;
    }

    public static class Configuration {
        public String inginiousURL;
        public String courseId;
        public String taskId;
        public LinkedList<ConfigurationEntry> entries;
    }

    public static boolean configExists(Project project) {
        return LocalFileSystem.getInstance().findFileByIoFile(Paths.get(project.getBasePath(), "config.inginious").toFile()) != null;
    }

    public static Configuration read(Project project) throws IOException {
        VirtualFile file = LocalFileSystem.getInstance().findFileByIoFile(Paths.get(project.getBasePath(), "config.inginious").toFile());

        String content = new String(file.contentsToByteArray(), StandardCharsets.UTF_8);
        Scanner scan = new Scanner(content);

        Configuration config = new Configuration();
        config.inginiousURL = scan.next();
        config.courseId = scan.next();
        config.taskId = scan.next();
        config.entries = new LinkedList<>();

        assert (scan.nextLine().isEmpty());

        while (scan.hasNext()) {
            String line = scan.nextLine();
            String[] lineContent = line.split(" ");

            ConfigurationEntry entry = new ConfigurationEntry();
            entry.subproblemId = lineContent[0];
            entry.language = lineContent[1];
            entry.extractArgs = new String[lineContent.length - 2];
            System.arraycopy(lineContent, 2, entry.extractArgs, 0, entry.extractArgs.length);

            config.entries.push(entry);
        }

        return config;
    }
}