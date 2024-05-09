package kiwi.sofia.mail.common;

import kiwi.sofia.mail.view.LoginView;

import java.util.prefs.Preferences;

/**
 * A record of a host, username, password, and port for a connection.
 */
public record ConnectionRecord(String host, String username, String password, int port) {

    /**
     * @return a ConnectionSet for SMTP with the default fallback host and port
     */
    public static ConnectionRecord getSmtpConnectionSet() {
        return getConnectionSet("smtp", "smtp.gmail.com", 465);
    }

    /**
     * @return a ConnectionSet for IMAP with the default fallback host and port
     */
    public static ConnectionRecord getImapConnectionSet() {
        return getConnectionSet("imap", "imap.gmail.com", 993);
    }

    /**
     * @param key          the key to use for the preferences (smtp -> smtpUsername)
     * @param fallbackHost the host to use if the preference is not set
     * @param fallbackPort the port to use if the preference is not set
     * @return a ConnectionSet with the given key, fallback host, and fallback port
     */
    public static ConnectionRecord getConnectionSet(String key, String fallbackHost, int fallbackPort) {
        Preferences prefs = Preferences.userNodeForPackage(LoginView.class);
        String username = prefs.get(key + "Username", "");
        String password = prefs.get(key + "Password", "");
        String host = prefs.get(key + "Host", fallbackHost);
        int port = prefs.getInt(key + "Port", fallbackPort);

        return new ConnectionRecord(host, username, password, port);
    }
}
