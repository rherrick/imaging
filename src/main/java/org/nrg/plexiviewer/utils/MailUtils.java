/*
 * org.nrg.plexiViewer.utils.MailUtils
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 10/4/13 10:50 AM
 */
package org.nrg.plexiviewer.utils;

import org.nrg.xdat.XDAT;

import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

public class MailUtils {

    public static void send(String subject, String body) {
        try {
            Properties props = System.getProperties();

            // -- Attaching to default Session, or we could start a new one --

            props.put("mail.smtp.host", "artsci.wustl.edu");
            javax.mail.Session session = javax.mail.Session.getDefaultInstance(props, null);

            // -- Create a new message --
            Message msg = new MimeMessage(session);

            // -- Set the FROM and TO fields --
            final String adminEmail = XDAT.getSiteConfigPreferences().getAdminEmail();
            msg.setFrom(new InternetAddress(adminEmail));
            msg.setRecipients(Message.RecipientType.TO,
                              InternetAddress.parse(adminEmail, false));

            // -- We could include CC recipients too --
            // if (cc != null)
            // msg.setRecipients(Message.RecipientType.CC
            // ,InternetAddress.parse(cc, false));

            // -- Set the subject and body text --
            msg.setSubject(subject);
            msg.setText(body);

            // -- Set some other header information --
            msg.setHeader("X-Mailer", "LOTONtechEmail");
            msg.setSentDate(new Date());

            // -- Send the message --
            Transport.send(msg);

            System.out.println("Message sent OK.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}


