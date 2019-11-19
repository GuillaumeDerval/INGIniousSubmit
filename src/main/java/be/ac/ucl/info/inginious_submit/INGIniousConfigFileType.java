package be.ac.ucl.info.inginious_submit;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class INGIniousConfigFileType implements FileType {
    public static final INGIniousConfigFileType INSTANCE = new INGIniousConfigFileType();

    @NotNull
    @Override
    public String getName() {
        return "INGInious submit plugin config file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "INGInious submit plugin config file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "config.inginious";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return INGIniousIcons.FILE;
    }

    @Override
    public boolean isBinary() {
        return false;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Nullable
    @Override
    public String getCharset(@NotNull VirtualFile file, @NotNull byte[] content) {
        return "utf-8";
    }
}
