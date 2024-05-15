package kiwi.sofia.mail.common;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;

import java.util.Properties;

public class SmtpManager {
    private static Transport transport;
    private static Session session;

    public static Pair<Transport, Exception> getTransportExc() {
        try {
            ConnectionRecord set = ConnectionRecord.getSmtpConnectionSet();
            Transport transport = getSessionExc().getA().getTransport("smtp");
            transport.connect(set.host(), set.username(), set.password());

            return new Pair<>(transport, null);
        } catch (Exception e) {
            return new Pair<>(null, e);
        }
    }

    public static Pair<Session, Exception> getSessionExc() {
        try {
            ConnectionRecord set = ConnectionRecord.getSmtpConnectionSet();

            Properties props = PropertiesCreator.createSmtpProperties();

            Session session = Session.getInstance(props, !set.password().isBlank() ?
                    new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(set.username(), set.password());
                        }
                    } : null); // null authenticator if no password is provided


            return new Pair<>(session, null);
        } catch (Exception e) {
            return new Pair<>(null, e);
        }
    }

    /**
     * This method is always blocking.
     */
    public static Transport getTransport() {
        return getTransportExc().getA();
    }

    public static Transport getCachedTransport() {
        if (transport == null) {
            System.out.println("Re-fetching non-cached transport");
            transport = getTransport();
        }

        return transport;
    }

    public static Session getSession() {
        return getSessionExc().getA();
    }

    public static Session getCachedSession() {
        if (session == null) {
            System.out.println("Re-fetching non-cached session");
            session = getSession();
        }

        return session;
    }
}
