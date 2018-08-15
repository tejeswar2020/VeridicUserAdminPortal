package com.buddy.service;

import java.util.Optional;
import java.util.Set;

import com.buddy.domain.ImportantDate;
import com.buddy.domain.User;
import com.buddy.domain.WorkingDetail;
import com.buddy.domain.security.PasswordResetToken;
import com.buddy.domain.security.UserRole;

public interface UserService 
{
	PasswordResetToken getPasswordResetToken(final String token);
	
	void createPasswordResetTokenForUser(final User user, final String token);
	
	User findByUsername(String username);
	
	User findByEmail (String email);
	
	User findByPhone (String phone);
	
	Optional<User> findById(Long id);
	
	User createUser(User user, Set<UserRole> userRoles) throws Exception;
	
	User save(User user);
	
	void updateImportantDates(User user, ImportantDate importantDate);
	
	void updateWorkingDetail(WorkingDetail workingDetail, User user);
	
}
