package kiwi.sofia.mail.common;

import kiwi.sofia.mail.view.LoginView;

import java.util.prefs.Preferences;

public class ConnectionSet {
    private final String host;
    private final String username;
    private final String password;
    private final int port;

    public ConnectionSet(String host, String username, String password, int port) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.port = port;
    }

    /**
     * @return a ConnectionSet for SMTP with the default fallback host and port
     */
    public static ConnectionSet getSmtpConnectionSet() {
        return getConnectionSet("smtp", "smtp.gmail.com", 465);
    }

    /**
     * @return a ConnectionSet for IMAP with the default fallback host and port
     */
    public static ConnectionSet getImapConnectionSet() {
        return getConnectionSet("imap", "imap.gmail.com", 993);
    }

    /**
     * @param key          the key to use for the preferences (smtp -> smtpUsername)
     * @param fallbackHost the host to use if the preference is not set
     * @param fallbackPort the port to use if the preference is not set
     * @return a ConnectionSet with the given key, fallback host, and fallback port
     */
    public static ConnectionSet getConnectionSet(String key, String fallbackHost, int fallbackPort) {
        Preferences prefs = Preferences.userNodeForPackage(LoginView.class);
        String username = prefs.get(key + "Username", "");
        String password = prefs.get(key + "Password", "");
        String host = prefs.get(key + "Host", fallbackHost);
        int port = prefs.getInt(key + "Port", fallbackPort);

        return new ConnectionSet(host, username, password, port);
    }

    public String getHost() {
        return host;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return port;
    }
}
