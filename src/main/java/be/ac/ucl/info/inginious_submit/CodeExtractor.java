package be.ac.ucl.info.inginious_submit;

import be.ac.ucl.info.inginious_submit.extractors.LanguageCodeExtractor;
import com.google.common.collect.ImmutableMap;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;

import java.util.List;
import java.util.Map;

public class CodeExtractor {
    private final Map<String, LanguageCodeExtractor> map;

    public CodeExtractor() {
        ExtensionPointName<LanguageCodeExtractor> extractorsPoint = com.intellij.openapi.extensions.ExtensionPointName.create("be.ac.ucl.info.inginious_submit.INGIniousSubmitLangExtractor");
        List<LanguageCodeExtractor> extractors = extractorsPoint.getExtensionList();

        ImmutableMap.Builder<String, LanguageCodeExtractor> builder = ImmutableMap.builder();
        for(LanguageCodeExtractor lce: extractors)
            builder.put(lce.getLanguageId(), lce);
        map = builder.build();
    }

    public String getContent(Project project, String language, String[] args) {
        return PaddingHelper.trim(map.get(language).getContent(project, args));
    }
}
