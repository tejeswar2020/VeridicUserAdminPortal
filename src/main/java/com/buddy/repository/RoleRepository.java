package com.buddy.repository;

import org.springframework.data.repository.CrudRepository;

import com.buddy.domain.security.Role;

public interface RoleRepository extends CrudRepository<Role, Long> {
	Role findByname(String name);
}
