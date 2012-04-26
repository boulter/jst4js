package jst.mail;

import org.apache.log4j.Logger;

import javax.mail.internet.*;
import javax.mail.*;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.DataHandler;
import java.util.*;
import java.io.IOException;
import java.io.File;

import jst.spring.JavascriptTemplateBean;

public class Emailer {

    private static final Logger logger = Logger.getLogger(Emailer.class);

    private String from;
    private String username;
    private String password;
    private Properties mailProperties;
    private JavascriptTemplateBean jst;

    protected Emailer() {
    }

    public Emailer( JavascriptTemplateBean jst, Properties mailProperties, String from, String username, String password) throws IOException {
        this.jst = jst;
        this.mailProperties = mailProperties;
        this.from = from;
        this.username = username;
        this.password = password;
    }

    public Email email( String subject, String mailTemplate, String htmlTemplate ) {
        return new Email( subject, mailTemplate, htmlTemplate );
    }

    public Email email(String to, String subject, String mailTemplate, String htmlTemplate) {
        return new Email(to, subject, mailTemplate, htmlTemplate);
    }

    public class Email {
        private String to;
        private String[] bcc;
        private String[] cc;
        private String subject;
        private String textTemplate;
        private String htmlTemplate;
        private Map<String,Object> params;
        private List<File> attachments;

        private Email() {
            this.params = new HashMap<String,Object>();
            this.attachments = new ArrayList<File>();
        }

        public Email( String subject, String textTemplate, String htmlTemplate ) {
            this();
            this.subject = subject;
            this.textTemplate = textTemplate;
            this.htmlTemplate = htmlTemplate;
        }

        public Email(String to, String subject, String textTemplate, String htmlTemplate) {
            this();
            this.to = to;
            this.subject = subject;
            this.textTemplate = textTemplate;
            this.htmlTemplate = htmlTemplate;
        }

        public Email bind(String key, Object obj) {
            params.put(key, obj);
            return this;
        }

        public Email to( String to ) {
            this.to = to;
            return this;
        }

        public Email cc( String... cc ) {
            this.cc = cc;
            return this;
        }

        public Email bcc( String... bcc ) {
            this.bcc = bcc;
            return this;
        }

        public Email attach( File... files ) {
            this.attachments.addAll( Arrays.asList( files ) );
            return this;
        }

        private InternetAddress[] convertToAddress(String... recipients) throws AddressException {
            InternetAddress[] addresses = new InternetAddress[ recipients.length ];
            int i = 0;
            for( String address : recipients ) {
                addresses[i++] = new InternetAddress(address);
            }
            return addresses;
        }

        public void send( Session session, String from ) {
            try {
                MimeMessage mimeMessage = new MimeMessage( session );

                mimeMessage.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(to));

                if( cc != null ) {
                    mimeMessage.setRecipients( MimeMessage.RecipientType.CC, convertToAddress( cc ) );
                }

                if( bcc != null ) {
                    mimeMessage.setRecipients( MimeMessage.RecipientType.BCC, convertToAddress( bcc ) );
                }

                mimeMessage.setFrom(new InternetAddress(from));
                mimeMessage.setSubject(subject);

                if (htmlTemplate == null ) {
                    if( attachments.isEmpty() ) {
                        mimeMessage.setText(jst.evaluate( textTemplate, params ).toString());
                    } else {
                        MimeMultipart part = new MimeMultipart();
                        part.addBodyPart( createBody(jst.evaluate( textTemplate, params ).toString(), "text/plain"));
                        for( File f : attachments ) {
                            part.addBodyPart( createAttachment(f) );
                        }
                        mimeMessage.setContent( part );
                    }
                } else {
                    String text = jst.evaluate( textTemplate, params ).toString();
                    String html = jst.evaluate( htmlTemplate, params ).toString();

                    MimeMultipart part = new MimeMultipart("alternative");
                    part.addBodyPart(createBody(text, "text/plain"));
                    part.addBodyPart(createBody(html, "text/html"));

                    for( File f : attachments ) {
                        part.addBodyPart( createAttachment( f ) );
                    }

                    mimeMessage.setContent(part);
                }

                mimeMessage.saveChanges();

                Transport transport = session.getTransport("smtp");
                transport.connect(username, password);
                transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
                transport.close();
            } catch( Exception mex ) {
                logger.error("There was an problem emailing: " + subject + " to: " + to, mex);
            }

        }

        public void send() {
            send( Session.getInstance(mailProperties), from );
        }

        public void send(String from) {
            send( Session.getInstance(mailProperties), from );
        }

        private BodyPart createBody(String text, String mimetype) throws MessagingException {
            MimeBodyPart body = new MimeBodyPart();
            body.setContent(text, mimetype);
            return body;
        }

        private BodyPart createAttachment(File f) throws MessagingException {
            DataSource source = new FileDataSource( f );
            MimeBodyPart attachment = new MimeBodyPart();
            attachment.setDataHandler( new DataHandler( source ) );
            attachment.setFileName( f.getName() );
            return attachment;
        }

    }
}