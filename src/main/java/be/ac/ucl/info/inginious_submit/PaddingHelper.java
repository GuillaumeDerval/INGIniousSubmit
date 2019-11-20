package be.ac.ucl.info.inginious_submit;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;

import java.util.StringJoiner;

public class PaddingHelper {
    public static String getWhiteSpaceBefore(PsiElement elem) {
        StringBuilder w = new StringBuilder();
        while (elem.getPrevSibling() instanceof PsiWhiteSpace) {
            elem = elem.getPrevSibling();
            w.insert(0, elem.getText());
        }
        return w.toString();
    }

    public static String trim(String input) {
        int startIndex = 0;
        while (startIndex < input.length() && input.charAt(startIndex) == '\n')
            startIndex++;
        int endIndex = input.length();
        while (endIndex > startIndex && input.charAt(endIndex-1) == '\n' || input.charAt(endIndex-1) == '\t' || input.charAt(endIndex-1) == ' ')
            endIndex--;
        input = input.substring(startIndex, endIndex);

        // compute initial prefix
        String[] lines = input.split("\n");

        int prefixSize = 0;
        while (prefixSize < lines[0].length()) {
            char c = lines[0].charAt(prefixSize);
            if(c == ' ' || c == '\t')
                prefixSize++;
            else
                break;
        }

        // do the union of the prefixes
        for(int l = 1; l < lines.length; l++) {
            if(lines[l].equals(""))
                continue; //do not count into prefix

            int same = 0;
            for(; same <= prefixSize && same < lines[l].length(); same++) {
                if(lines[0].charAt(same) != lines[l].charAt(same))
                    break;
            }
            prefixSize = same;
        }

        // cut all the common prefixes
        for(int l = 0; l < lines.length; l++) {
            if(lines[l].equals(""))
                continue; //do not count into prefix

            lines[l] = lines[l].substring(prefixSize);
        }

        // done
        StringJoiner sj = new StringJoiner("\n");
        for(String s: lines)
            sj.add(s);
        input = sj.toString();

        return input;
    }
}
