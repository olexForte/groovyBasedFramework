package com.fortegrp.at.common.utils

import groovy.util.logging.Slf4j

import javax.mail.*

@Slf4j
class GmailHelper {
    static getGMAILInboxLastUnreadEmail(email, password, waitTimeout = 10, waitInterval = 1) {
        def session = Session.getDefaultInstance(new Properties(["mail.store.protocol": "imaps",
                                                                 "mail.imaps.host"    : "imap.gmail.com",
                                                                 "mail.imaps.port"    : "993"]), null)
        Store store = session.getStore("imaps")
        try {
            store.connect('imap.gmail.com', email, password)
        } catch (AuthenticationFailedException ex) {
            throw new AssertionError("Gmail: authentication error", ex)
        }
        def folder = store.getFolder("INBOX")
        try {
            folder.open(Folder.READ_WRITE)
        } catch (MessagingException ex) {
            throw new AssertionError("Gmail: Error reading INBOX folder", ex)
        }
        for (int i = 0; i < waitTimeout; i++) {
            if (folder.getUnreadMessageCount() == 0) {
                sleep(waitInterval * 1000)
                try {
                    folder = store.getFolder("INBOX")
                    folder.open(Folder.READ_WRITE)
                } catch (MessagingException ex) {
                    throw new AssertionError("Gmail: Error reading INBOX folder", ex)
                }
            } else {
                break
            }
        }
        if (folder.getUnreadMessageCount() == 0) {
            throw new AssertionError("Gmail: No new messages detected on GMail box")
        }
        def result = folder.messages.last().content
        folder.setFlags(folder.messages, new Flags(Flags.Flag.SEEN), true)
        folder.close(true)
        store.close()
        result
    }
}
