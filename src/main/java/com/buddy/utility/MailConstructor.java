package com.buddy.utility;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import com.buddy.domain.User;

@Component
public class MailConstructor
{
	@Autowired
	private Environment env;

	public SimpleMailMessage constructResetTokenEmail(String contextPath, Locale locale, String token, User user,
			String password, String subject)
	{

		String url = contextPath + "/newUser?token=" + token;
		String message = "Hello " + user.getUsername()
				+ ",\n\nPlease click on this link to verify your email and edit your personal information.\n" + url
				+ "\n\nYour password is:" + password
				+ "\n\n Thanks \nsupport@veridicsolutions.com \nVeridic solutions";
		SimpleMailMessage email = new SimpleMailMessage();
		email.setTo(user.getEmail());
		email.setSubject(subject);
		email.setText(message);
		email.setFrom(env.getProperty("support.email"));
		return email;

	}
}
