package be.ac.ucl.info.inginious_submit;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.apache.commons.io.Charsets;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class SubmitAction extends AnAction {
    private static LoginPassword askForLogin(String inginiousURL) {
        LoginPassword lp = LoginManagement.getSavedPassword(inginiousURL);
        if(lp != null)
            return lp;

        RunnableFuture<LoginPassword> askForLogin = new FutureTask<>(() -> {
            LoginDialogWrapper loginDialog = new LoginDialogWrapper(inginiousURL);
            if(loginDialog.showAndGet()) {
                LoginPassword out = new LoginPassword();
                out.username = loginDialog.login.getText();
                out.password = new String(loginDialog.password.getPassword());
                return out;
            }
            else
                return null;
        });
        ApplicationManager.getApplication().invokeAndWait(askForLogin);

        try {
            lp = askForLogin.get();
            if(lp != null)
                LoginManagement.savePassword(inginiousURL, lp.username, lp.password);
            return lp;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void showError(Project project, String message) {
        ApplicationManager.getApplication().invokeLater(() -> {
            Messages.showMessageDialog(project, message, "INGInious - An Error Occured", INGIniousIcons.ERROR);
        });
    }

    private static String submit(Project project, String INGIniousBaseURL, String course, String task, LoginPassword lp, Map<String, String> submissionContent) {
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPostLogin = new HttpPost(INGIniousBaseURL+"/api/v0/authentication");

            ArrayList<NameValuePair> paramsLogin = new ArrayList<NameValuePair>();
            paramsLogin.add(new BasicNameValuePair("login", lp.username));
            paramsLogin.add(new BasicNameValuePair("password", lp.password));

            httpPostLogin.setEntity(new UrlEncodedFormEntity(paramsLogin));
            CloseableHttpResponse responseLogin = client.execute(httpPostLogin);

            if(responseLogin.getStatusLine().getStatusCode() == 403) {
                showError(project, "Invalid login/password");
                LoginManagement.invalidatePassword(INGIniousBaseURL);
                return null;
            }
            if(responseLogin.getStatusLine().getStatusCode() != 200) {
                showError(project, "Something went wrong");
                LoginManagement.invalidatePassword(INGIniousBaseURL);
                return null;
            }

            HttpPost httpPostSubmit = new HttpPost(INGIniousBaseURL+"/api/v0/courses/"+course+"/tasks/"+task+"/submissions");
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            for(Map.Entry<String, String> entry: submissionContent.entrySet())
                builder.addTextBody(entry.getKey(), entry.getValue());
            //builder.addBinaryBody("file", new File("test.txt"), ContentType.APPLICATION_OCTET_STREAM, "file.ext");

            HttpEntity multipart = builder.build();
            httpPostSubmit.setEntity(multipart);
            CloseableHttpResponse responseSubmit = client.execute(httpPostSubmit);

            HttpEntity entity = responseSubmit.getEntity();
            Header encodingHeader = entity.getContentEncoding();

            Charset encoding = encodingHeader == null ? StandardCharsets.UTF_8 : Charsets.toCharset(encodingHeader.getValue());
            String retString = EntityUtils.toString(entity, encoding);

            switch (responseSubmit.getStatusLine().getStatusCode()) {
                case 400:
                case 404:
                    showError(project, "This IDE project is outdated; please redownload it from INGInious.");
                    return null;
                case 403:
                case 500:
                    showError(project, "An error occured: " + retString);
                    return null;
                case 200:
                    //everything was ok!
                    break;
                default:
                    showError(project, "An unknown error occured.");
                    return null;

            }

            String submissionid = ((LinkedTreeMap<String, String>)(new Gson()).fromJson(retString, Object.class)).get("submissionid");
            client.close();
            return submissionid;

        } catch (IOException e) {
            e.printStackTrace();
            showError(project, "An unknown error occured.");
            return null;
        }
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();

        ConfigReader.Configuration config;
        try {
             config = ConfigReader.read(project);
        } catch (IOException e) {
            Messages.showMessageDialog(project, "Please check if everything is up-to-date.", "Configuration File Unreadable", INGIniousIcons.ERROR);
            return;
        }

        CodeExtractor extractor = new CodeExtractor();

        Map<String, String> submissionContent;
        try {
            submissionContent = config.entries.stream().collect(Collectors.toMap((c) -> c.subproblemId, (c) -> extractor.getContent(project, c.language, c.extractArgs)));
        } catch (Exception e) {
            Messages.showMessageDialog(project, e.getMessage(), "Code Unreadable", INGIniousIcons.ERROR);
            return;
        }

        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Sending to INGInious") {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                progressIndicator.setIndeterminate(true);

                LoginPassword lp = askForLogin(config.inginiousURL);
                if(lp == null)
                    return;

                String submissionid = submit(project, config.inginiousURL, config.courseId, config.taskId, lp, submissionContent);
                if(submissionid == null)
                    return;

                BrowserUtil.open(config.inginiousURL+"/course/"+config.courseId+"/"+config.taskId+"?load="+submissionid);

                Messages.showMessageDialog(project, "Done!", "Submission Sent", INGIniousIcons.INFO);
            }
        });
    }

    private Project lastProject = null;

    @Override
    public void update(AnActionEvent anActionEvent) {
        // Set the availability based on whether a project is open
        Project project = anActionEvent.getProject();

        boolean activate;

        if(project == null) {
            activate = false;
        }
        else if(lastProject != project) {
            activate = ConfigReader.configExists(project);
            lastProject = project;
        }
        else {
            activate = anActionEvent.getPresentation().isVisible();
        }

        anActionEvent.getPresentation().setVisible(activate);
    }
}
