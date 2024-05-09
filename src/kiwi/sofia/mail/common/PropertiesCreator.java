package kiwi.sofia.mail.common;

import java.util.Properties;

/**
 * Helper class for creating Properties objects.
 */
public class PropertiesCreator {
    public static Properties createSmtpProperties(){
        ConnectionRecord set = ConnectionRecord.getSmtpConnectionSet();

        Properties props = new Properties();
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.connectiontimeout", "5000");

        if (set.port() == 465)
            props.put("mail.smtp.ssl.enable", "true");
        else if (set.port() == 587)
            props.put("mail.smtp.starttls.enable", "true");

        if (!set.password().isBlank())
            props.put("mail.smtp.auth", "true");

        props.put("mail.smtp.host", set.host());
        props.put("mail.smtp.port", set.port());

        return props;
    }

    public static Properties createImapProperties(){
        ConnectionRecord set = ConnectionRecord.getImapConnectionSet();

        Properties properties = new Properties();
        properties.put("mail.imap.host", set.host());
        properties.put("mail.imap.port", set.port());
        properties.put("mail.imap.ssl.enable", "true");

        return properties;
    }
}
