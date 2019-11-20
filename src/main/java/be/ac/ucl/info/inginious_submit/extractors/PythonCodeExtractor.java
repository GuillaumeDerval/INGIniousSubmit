package be.ac.ucl.info.inginious_submit.extractors;

import be.ac.ucl.info.inginious_submit.INGIniousIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.jetbrains.python.psi.*;

import static be.ac.ucl.info.inginious_submit.PaddingHelper.getWhiteSpaceBefore;

public class PythonCodeExtractor implements LanguageCodeExtractor {
    @Override
    public String getLanguageId() {
        return "python";
    }

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
        PsiElement anyElement = PsiManager.getInstance(project).findFile(project.getProjectFile());
        PsiElement cls = PyPsiFacade.getInstance(project).createClassByQName(qualifiedName, anyElement);

        if(cls == null) {
            Messages.showMessageDialog(project, "Error", "Class " + qualifiedName + " not found.", INGIniousIcons.ERROR);
            return null;
        }

        if(withSignature)
            return getWhiteSpaceBefore(cls) + cls.getText();
        else {
            PsiElement[] children = cls.getChildren();

            if(children.length == 0) {
                return "";
            }
            else {
                PsiElement first = children[0];
                if(children[0] instanceof PyArgumentList) {
                    if(children.length == 1)
                        return "";
                    first = children[1];
                }

                while (first.getPrevSibling() instanceof PsiWhiteSpace)
                    first = first.getPrevSibling();
                return cls.getText().substring(first.getStartOffsetInParent());
            }
        }
    }

    public String getMethodContent(Project project, String qualifiedClassName, String functionName, boolean withSignature) {
        PsiElement anyElement = PsiManager.getInstance(project).findFile(project.getProjectFile());
        PyClass cls = PyPsiFacade.getInstance(project).createClassByQName(qualifiedClassName, anyElement);

        if(cls == null) {
            Messages.showMessageDialog(project, "Error", "Class " + qualifiedClassName + " not found.", INGIniousIcons.ERROR);
            return null;
        }

        PyFunction[] functions = cls.getMethods();
        PyFunction found = null;
        for(PyFunction f: functions) {
            if(f.getName().equals(functionName)) {
                if(found != null) {
                    Messages.showMessageDialog(project, "Error", "Multiple methods in class " + qualifiedClassName + " have name " + functionName, INGIniousIcons.ERROR);
                    return null;
                }
                found = f;
            }
        }

        if(found == null) {
            Messages.showMessageDialog(project, "Error", "Method " + qualifiedClassName + "." + functionName +" not found.", INGIniousIcons.ERROR);
            return null;
        }

        if(withSignature)
            return getWhiteSpaceBefore(found) + found.getText();
        else {
            PsiElement[] children = found.getChildren();

            if(children.length == 0) {
                return "";
            }
            else {
                PsiElement first = children[0];
                if(children[0] instanceof PyParameterList) {
                    if(children.length == 1)
                        return "";
                    first = children[1];
                }

                while (first.getPrevSibling() instanceof PsiWhiteSpace)
                    first = first.getPrevSibling();
                return found.getText().substring(first.getStartOffsetInParent());
            }
        }
    }
}
