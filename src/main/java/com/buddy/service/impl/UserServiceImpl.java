package com.buddy.service.impl;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.buddy.domain.ImportantDate;
import com.buddy.domain.User;
import com.buddy.domain.WorkingDetail;
import com.buddy.domain.security.PasswordResetToken;
import com.buddy.domain.security.UserRole;
import com.buddy.repository.PasswordResetTokenRepository;
import com.buddy.repository.RoleRepository;
import com.buddy.repository.UserRepository;
import com.buddy.service.UserService;

@Service
public class UserServiceImpl implements UserService
{

	private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordResetTokenRepository passwordResetTokenRepository;
	
	@Override
	public PasswordResetToken getPasswordResetToken(final String token)
	{
		return passwordResetTokenRepository.findByToken(token);
	}

	@Override
	public void createPasswordResetTokenForUser(final User user, final String token)
	{
		final PasswordResetToken myToken = new PasswordResetToken(token, user);
		passwordResetTokenRepository.save(myToken);
	}

	@Override
	public User findByUsername(String username)
	{
		return userRepository.findByUsername(username);
	}

	@Override
	public User findByEmail(String email)
	{
		return userRepository.findByEmail(email);
	}

	@Override
	public User findByPhone(String phone)
	{
		return userRepository.findByPhone(phone);
	}

	@Override
	public User findById(Long id)
	{
		return userRepository.findOne(id);
	}

	@Override
	public User createUser(User user, Set<UserRole> userRoles)
	{
		User localUser = userRepository.findByUsername(user.getUsername());

		if (localUser != null)
		{
			LOG.info("user {} already exists. Nothing will be done.", user.getUsername());
		} else
		{
			for (UserRole ur : userRoles)
			{
				roleRepository.save(ur.getRole());
			}

			user.getUserRoles().addAll(userRoles);

			localUser = userRepository.save(user);
		}

		return localUser;
	}

	@Override
	public User save(User user)
	{
		return userRepository.save(user);
	}

	@Override
	public void updateImportantDates(User user, ImportantDate importantDate)
	{
		importantDate.setUser(user);
		user.getImportantDateList().add(importantDate);
		save(user);
	}

	@Override
	public void updateWorkingDetail(WorkingDetail workingDetail, User user)
	{
		workingDetail.setUser(user);
		user.getWorkingDetailList().add(workingDetail);
		save(user);
	}

}
