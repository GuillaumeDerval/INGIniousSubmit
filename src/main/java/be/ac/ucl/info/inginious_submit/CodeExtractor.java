package be.ac.ucl.info.inginious_submit;

import be.ac.ucl.info.inginious_submit.extractors.JavaCodeExtractor;
import be.ac.ucl.info.inginious_submit.extractors.LanguageCodeExtractor;
import com.google.common.collect.ImmutableMap;
import com.intellij.openapi.project.Project;

import java.util.Map;

public class CodeExtractor {
    private static final Map<String, LanguageCodeExtractor> map;
    static {
        ImmutableMap.Builder<String, LanguageCodeExtractor> builder = ImmutableMap.builder();

        builder.put("java", new JavaCodeExtractor());

        map = builder.build();
    }



    public static String getContent(Project project, String language, String[] args) {
        return map.get(language).getContent(project, args);
    }
}
