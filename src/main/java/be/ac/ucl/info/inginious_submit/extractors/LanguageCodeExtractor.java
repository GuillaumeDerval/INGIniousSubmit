package be.ac.ucl.info.inginious_submit.extractors;

import com.intellij.openapi.project.Project;

public interface LanguageCodeExtractor {
    public String getContent(Project project, String[] args);
}
