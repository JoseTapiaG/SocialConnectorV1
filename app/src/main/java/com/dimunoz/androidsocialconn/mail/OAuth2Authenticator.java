package com.dimunoz.androidsocialconn.mail;

import com.sun.mail.imap.IMAPSSLStore;
import com.sun.mail.imap.IMAPStore;
import com.sun.mail.smtp.SMTPTransport;
import com.sun.mail.util.BASE64EncoderStream;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Provider;
import java.security.Security;
import java.util.Properties;


/**
 * Performs OAuth2 authentication.
 *
 * <p>Before using this class, you must call {@code initialize} to install the
 * OAuth2 SASL provider.
 */
public class OAuth2Authenticator {
	private static Session mSession;
	
	public static final class OAuth2Provider extends Provider {
		private static final long serialVersionUID = 1L;
		
		public OAuth2Provider() {
			super("Google OAuth2 Provider", 1.0,
					"Provides the XOAUTH2 SASL Mechanism");
			put("SaslClientFactory.XOAUTH2",
					"com.dimunoz.mail.OAuth2SaslClientFactory");
		}
	}
	
	/**
	 * Installs the OAuth2 SASL provider. This must be called exactly once before
	 * calling other methods on this class.
	 */
	public static void initialize() {
		Security.addProvider(new OAuth2Provider());
	}

	/**
	 * Connects and authenticates to an IMAP server with OAuth2. You must have
	 * called {@code initialize}.
	 *
	 * @param host Hostname of the imap server, for example {@code
	 *     imap.googlemail.com}.
	 * @param port Port of the imap server, for example 993.
	 * @param userEmail Email address of the user to authenticate, for example
	 *     {@code oauth@gmail.com}.
	 * @param oauthToken The user's OAuth token.
	 * @param debug Whether to enable debug logging on the IMAP connection.
	 * @return An authenticated IMAPStore that can be used for IMAP operations.
	 */
	public static IMAPStore connectToImap(String host,
			int port,
			String userEmail,
			String oauthToken,
			boolean debug) throws Exception {
		Properties props = new Properties();
		props.put("mail.imaps.sasl.enable", "true");
		props.put("mail.imaps.sasl.mechanisms", "XOAUTH2");
		props.put(OAuth2SaslClientFactory.OAUTH_TOKEN_PROP, oauthToken);
		Session session = Session.getInstance(props);
		session.setDebug(debug);
		
		final URLName unusedUrlName = null;
		IMAPSSLStore store = new IMAPSSLStore(session, unusedUrlName);
		final String emptyPassword = "";
		store.connect(host, port, userEmail, emptyPassword);
		return store;
	}
	
	/**
	 * Connects and authenticates to an SMTP server with OAuth2. You must have
	 * called {@code initialize}.
	 * 
	 * @param host Hostname of the smtp server, for example {@code
	 *     smtp.googlemail.com}.
	 * @param port Port of the smtp server, for example 587.
	 * @param userEmail Email address of the user to authenticate, for example
	 *     {@code oauth@gmail.com}.
	 * @param oauthToken The user's OAuth token.
	 * @param debug Whether to enable debug logging on the connection.
	 * 
	 * @return An authenticated SMTPTransport that can be used for SMTP
	 *     operations.
	 */
	public static SMTPTransport connectToSmtp(String host,
			int port,
			String userEmail,
			String oauthToken,
			boolean debug) throws Exception {
		Properties props = new Properties();
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.starttls.required", "true");
		props.put("mail.smtp.sasl.enable", "false");
		//props.put("mail.smtp.sasl.mechanisms", "XOAUTH2");
		//props.put(OAuth2SaslClientFactory.OAUTH_TOKEN_PROP, oauthToken);
		mSession = Session.getInstance(props);
		mSession.setDebug(debug);

		final URLName unusedUrlName = null;
		SMTPTransport transport = new SMTPTransport(mSession, unusedUrlName);
		// If the password is non-null, SMTP tries to do AUTH LOGIN.
		final String emptyPassword = null;
		transport.connect(host, port, userEmail, emptyPassword);
		
		byte[] response = String.format("user=%s\1auth=Bearer %s\1\1", userEmail,
	            oauthToken).getBytes();
	    response = BASE64EncoderStream.encode(response);

	    transport.issueCommand("AUTH XOAUTH2 " + new String(response),
                235);

		return transport;
	}

    public synchronized void sendMail(String subject, String body, String user,
                                      String oauthToken, String recipients) {
        try {

            SMTPTransport smtpTransport = connectToSmtp("smtp.gmail.com",
                    587,
                    user,
                    oauthToken,
                    true);

            MimeMessage message = new MimeMessage(mSession);
            DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));
            message.setSender(new InternetAddress(user));
            message.setSubject(subject);
            message.setDataHandler(handler);
            if (recipients.indexOf(',') > 0)
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
            else
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
            smtpTransport.sendMessage(message, message.getAllRecipients());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void sendMail(String subject, String body, String user,
                                      String oauthToken, String recipients, File file) {
        try {

            SMTPTransport smtpTransport = connectToSmtp("smtp.gmail.com",
                    587,
                    user,
                    oauthToken,
                    true);

            MimeMessage message = new MimeMessage(mSession);
            message.setSender(new InternetAddress(user));
            message.setSubject(subject);
            if (recipients.indexOf(',') > 0)
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
            else
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
            // attachment
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(body);
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            messageBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(file);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(file.getName());
            multipart.addBodyPart(messageBodyPart);
            message.setContent(multipart);

            smtpTransport.sendMessage(message, message.getAllRecipients());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	public class ByteArrayDataSource implements DataSource {
		private byte[] data;
		private String type;

		public ByteArrayDataSource(byte[] data, String type) {
			super();
			this.data = data;
			this.type = type;
		}

		public ByteArrayDataSource(byte[] data) {
			super();
			this.data = data;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getContentType() {
			if (type == null)
				return "application/octet-stream";
			else
				return type;
		}

		public InputStream getInputStream() throws IOException {
			return new ByteArrayInputStream(data);
		}

		public String getName() {
			return "ByteArrayDataSource";
		}

		public OutputStream getOutputStream() throws IOException {
			throw new IOException("Not Supported");
		}
	}


}
