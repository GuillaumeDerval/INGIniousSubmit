package be.ac.ucl.info.inginious_submit.extractors;

import be.ac.ucl.info.inginious_submit.INGIniousIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;

public class JavaCodeExtractor implements LanguageCodeExtractor {
    public String getContent(Project project, String[] args) {
        if(args.length < 1)
            throw new IllegalArgumentException();
        if(args[0].equals("method")) {
            if(args.length != 4)
                throw new IllegalArgumentException();
            return getMethodContent(project, args[2], args[3], args[1].equals("true"));
        }
        else if(args[0].equals("class")) {
            if(args.length != 3)
                throw new IllegalArgumentException();
            return getClassContent(project, args[2], args[1].equals("true"));
        }
        else
            throw new IllegalArgumentException();
    }

    public String getClassContent(Project project, String qualifiedName, boolean withSignature) {
        PsiClass cls = JavaPsiFacade.getInstance(project).findClass(qualifiedName, GlobalSearchScope.projectScope(project));
        if(cls == null) {
            Messages.showMessageDialog(project, "Error", "Class " + qualifiedName + " not found.", INGIniousIcons.ERROR);
            return null;
        }
        if(withSignature)
            return cls.getText();
        else
            return cls.getText().substring(cls.getLBrace().getStartOffsetInParent() + cls.getLBrace().getTextLength(), cls.getRBrace().getStartOffsetInParent()).trim();
    }

    public String getMethodContent(Project project, String qualifiedClassName, String functionName, boolean withSignature) {
        PsiClass cls = JavaPsiFacade.getInstance(project).findClass(qualifiedClassName, GlobalSearchScope.projectScope(project));
        if(cls == null) {
            Messages.showMessageDialog(project, "Error", "Class " + qualifiedClassName + " not found.", INGIniousIcons.ERROR);
            return null;
        }

        PsiMethod found = null;
        for(PsiMethod method: cls.getMethods()) {
            if(method.getName().equals(functionName)) {
                if(found != null) {
                    Messages.showMessageDialog(project, "Error", "Multiple methods in class " + qualifiedClassName + " have name " + functionName, INGIniousIcons.ERROR);
                    return null;
                }
                found = method;
            }
        }

        if(found == null) {
            Messages.showMessageDialog(project, "Error", "Method " + qualifiedClassName + "." + functionName +" not found.", INGIniousIcons.ERROR);
            return null;
        }

        if(withSignature)
            return found.getText();
        else
            return found.getBody().getText().substring(
                    found.getBody().getLBrace().getStartOffsetInParent() + found.getBody().getLBrace().getTextLength(),
                    found.getBody().getRBrace().getStartOffsetInParent()
            ).trim();
    }
}
