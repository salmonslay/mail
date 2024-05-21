package kiwi.sofia.mail.common;

public enum AuthorMode {
    /**
     * This is a new email being composed and all fields should be empty by default.
     */
    NEW,
    /**
     * This is a draft that the user wants to edit, and the email fields should be pre-filled with the draft's data.
     */
    EDIT,

    /**
     * The user is replying to an email, and the email fields should be pre-filled with the original email's data.
     */
    REPLY,

    /**
     * The user is replying to all recipients, and the email fields should be pre-filled with the original email's data.
     */
    REPLY_ALL,

    /**
     * The user is forwarding an email, and the content and subject should be pre-filled, but not recipient-field.
     */
    FORWARD
}
