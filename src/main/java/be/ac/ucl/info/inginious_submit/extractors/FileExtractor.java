package be.ac.ucl.info.inginious_submit.extractors;

import be.ac.ucl.info.inginious_submit.INGIniousIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FileExtractor implements LanguageCodeExtractor {
    @Override
    public String getLanguageId() {
        return "file";
    }

    @Override
    public String getContent(Project project, String[] args) {
        if(args.length != 1)
            throw new IllegalArgumentException();

        try {
            VirtualFile vf = LocalFileSystem.getInstance().findFileByPath(project.getBasePath()).findFileByRelativePath(args[0]);
            byte[] content = vf.contentsToByteArray();
            return new String(content, StandardCharsets.UTF_8);
        } catch (IOException | NullPointerException e ) {
            Messages.showMessageDialog(project, "Error", "File " + args[0] + " not found, or it is not a text file.", INGIniousIcons.ERROR);
            return null;
        }
    }
}
