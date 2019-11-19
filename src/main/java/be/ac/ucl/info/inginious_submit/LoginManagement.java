package be.ac.ucl.info.inginious_submit;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.CredentialAttributesKt;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;

public class LoginManagement {
    private static CredentialAttributes createCredentialAttributes(String key) {
        // ensure we store password temporarily, only in memory.
        return new CredentialAttributes(CredentialAttributesKt.generateServiceName("be/ac/ucl/info/inginious_submit", key), null, null, true);
    }

    static LoginPassword getSavedPassword(String inginiousURL) {
        CredentialAttributes credentialAttributes = createCredentialAttributes(inginiousURL);

        Credentials credentials = PasswordSafe.getInstance().get(credentialAttributes);
        if (credentials != null) {
            LoginPassword out = new LoginPassword();
            out.username = credentials.getUserName();
            out.password = credentials.getPasswordAsString();
            if(out.password == null || out.password.isEmpty())
                return null;
            return out;
        }
        return null;
    }

    static void savePassword(String inginiousURL, String username, String password) {
        CredentialAttributes credentialAttributes = createCredentialAttributes(inginiousURL);

        Credentials credentials = new Credentials(username, password);
        PasswordSafe.getInstance().set(credentialAttributes, credentials);
    }

    static void invalidatePassword(String inginiousURL) {
        CredentialAttributes credentialAttributes = createCredentialAttributes(inginiousURL);
        PasswordSafe.getInstance().set(credentialAttributes, null);
    }
}
