package be.ac.ucl.info.inginious_submit;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class LoginDialogWrapper extends DialogWrapper {

    public JTextField login;
    public JPasswordField password;
    public String url;
    public LoginDialogWrapper(String url) {
        super(true); // use current window as parent
        this.url = url;
        init();
        setTitle("INGInious Login");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel();
        dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.PAGE_AXIS));

        JLabel label = new JLabel("<html>Please enter your INGInious account login informations. <br>" +
                "Note that it is the INGInious password, not the password given by your university or workplace.<br>" +
                "Your password will be saved until your restart your IDE. <br>" +
                "<br>" +
                "You login will be sent to "+url+"</html>");
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(label);

        JPanel innerPanel = new JPanel();
        GridLayout table = new GridLayout(2,2);
        innerPanel.setLayout(table);

        JLabel loginText = new JLabel("Username");
        login = new JTextField();
        JLabel passwordText = new JLabel("Password");
        password = new JPasswordField();

        innerPanel.add(loginText);
        innerPanel.add(login);
        innerPanel.add(passwordText);
        innerPanel.add(password);

        dialogPanel.add(topPanel);
        dialogPanel.add(innerPanel);

        return dialogPanel;
    }
}
