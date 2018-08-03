package com.buddy.repository;

import org.springframework.data.repository.CrudRepository;

import com.buddy.domain.User;

public interface UserRepository extends CrudRepository<User, Long>
{
	User findByUsername(String username);
	
	User findByEmail(String email);
	
	User findByPhone(String phone);
}
