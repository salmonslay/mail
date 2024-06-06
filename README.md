# SofMail
SofMail is a simple but yet feature rich email client with support for stuff like sending/receiving HTML multipart through SMTP/IMAP, attachments, email folders and flags. Its GUI is created using [JavaFX](https://openjfx.io/) and the program is using [Jakarta Mail](https://jakarta.ee/specifications/mail/2.0/jakarta-mail-spec-2.0). 

I don't recommend anyone to actually use this, there are way better email clients out there. 

## Features
### Sending multipart messages via SMTP
- Messages are sent as HTML with a plain text fall back for clients that can't/won't display it
- Messages are composed using a WYSIWYG editor
- Messages can have attachments
- Messages can be sent to multiple addresses
- You can customize the name of the user recieving the message (name <email>)
- You can customize your own name

### Recieve multipart messages via IMAP
- The client downloads and caches images embedded in the message
- Attachments can be saved
- You can reply / forward the message
- HTML is prioritized, fall back to plain text

### Logging in
- Support for both authorized and unauthorized connections
- Support for both TLS/SSL
- Support for saving your credentials locally (separate option to save password, however not encouraged)

### The Inbox
- Unread messages shown in bold
- User can mark messages as unread manually
- Messages can be starred/printed/moved to folders
- Opening a draft will open the authoring screen

### Folders
- User can choose which folder to display messages from
- New folders can be created
- Messages can be moved between folders
- If Gmail is used, its special folders (starred, important, spam etc) will be displayed at the top with custom icons

## Images
![java_5998_khi](https://github.com/salmonslay/sofmail/assets/47401343/254970fb-2a4e-495c-85cb-8501e85b30a2)
![java_5999_26E](https://github.com/salmonslay/sofmail/assets/47401343/7320313c-97af-4a53-a359-1e36a69e249f)
![java_6008_4aL](https://github.com/salmonslay/sofmail/assets/47401343/6191b370-7513-415f-b53d-868b642a0477)
