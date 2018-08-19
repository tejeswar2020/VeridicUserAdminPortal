package com.buddy.service;

import java.util.Optional;

import com.buddy.domain.ImportantDate;

public interface ImportantDateService
{
	Optional<ImportantDate> findById(Long id);

	void removeById(Long id);
}
